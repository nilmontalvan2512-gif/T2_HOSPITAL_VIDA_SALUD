package pe.edu.hospitalvidasalud.appointments;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Locale;
import java.util.UUID;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class AppointmentService {
    private static final EnumSet<AppointmentStatus> ACTIVE_STATUSES =
            EnumSet.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED);

    private final AppointmentRepository repository;
    private final WebClient patientClient;
    private final RabbitTemplate rabbitTemplate;

    public AppointmentService(
            AppointmentRepository repository,
            WebClient.Builder webClientBuilder,
            RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.patientClient = webClientBuilder.baseUrl("http://patient-service").build();
        this.rabbitTemplate = rabbitTemplate;
    }

    public Mono<Appointment> create(AppointmentRequest request) {
        String professionalCode = request.professionalCode().trim().toUpperCase(Locale.ROOT);
        return ensurePatientExists(request.patientId())
                .then(repository.existsByProfessionalCodeAndAppointmentAtAndStatusIn(
                        professionalCode, request.appointmentAt(), ACTIVE_STATUSES))
                .flatMap(conflict -> conflict
                        ? Mono.error(new ResponseStatusException(
                                HttpStatus.CONFLICT, "El profesional ya tiene una cita activa en ese horario"))
                        : repository.save(new Appointment(
                                UUID.randomUUID(),
                                request.patientId(),
                                request.specialty().trim(),
                                request.appointmentAt(),
                                professionalCode,
                                request.administrativeReason().trim(),
                                AppointmentStatus.PENDING,
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                null)))
                .onErrorMap(DuplicateKeyException.class, exception ->
                        new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "El profesional ya tiene una cita activa en ese horario",
                                exception));
    }

    public Mono<Appointment> findById(UUID id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cita no encontrada")));
    }

    public Flux<Appointment> findAll() {
        return repository.findAll();
    }

    public Mono<Appointment> reschedule(UUID id, LocalDateTime newAppointmentAt) {
        return findById(id)
                .flatMap(current -> {
                    if (current.status() != AppointmentStatus.PENDING
                            && current.status() != AppointmentStatus.CONFIRMED) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.CONFLICT, "Solo se pueden reprogramar citas pendientes o confirmadas"));
                    }
                    return repository.existsByProfessionalCodeAndAppointmentAtAndStatusInAndIdNot(
                                    current.professionalCode(), newAppointmentAt, ACTIVE_STATUSES, id)
                            .flatMap(conflict -> conflict
                                    ? Mono.error(new ResponseStatusException(
                                            HttpStatus.CONFLICT,
                                            "El profesional ya tiene una cita activa en ese horario"))
                                    : repository.save(new Appointment(
                                            current.id(),
                                            current.patientId(),
                                            current.specialty(),
                                            newAppointmentAt,
                                            current.professionalCode(),
                                            current.administrativeReason(),
                                            AppointmentStatus.PENDING,
                                            current.createdAt(),
                                            LocalDateTime.now(),
                                            current.version())))
                            .onErrorMap(DuplicateKeyException.class, exception ->
                                    new ResponseStatusException(
                                            HttpStatus.CONFLICT,
                                            "El profesional ya tiene una cita activa en ese horario",
                                            exception));
                });
    }

    public Mono<Appointment> changeStatus(UUID id, AppointmentStatus target) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cita no encontrada")))
                .flatMap(current -> {
                    if (!current.status().canTransitionTo(target)) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Transición de estado no permitida: " + current.status() + " -> " + target));
                    }
                    var updated = new Appointment(
                            current.id(),
                            current.patientId(),
                            current.specialty(),
                            current.appointmentAt(),
                            current.professionalCode(),
                            current.administrativeReason(),
                            target,
                            current.createdAt(),
                            LocalDateTime.now(),
                            current.version());
                    return repository.save(updated)
                            .flatMap(saved -> target == AppointmentStatus.CONFIRMED
                                    ? publishConfirmedEvent(current, saved).thenReturn(saved)
                                    : Mono.just(saved));
                });
    }

    private Mono<Void> ensurePatientExists(UUID patientId) {
        return patientClient.get()
                .uri("/api/patients/{id}", patientId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                        Mono.error(new ResponseStatusException(
                                HttpStatus.UNPROCESSABLE_ENTITY, "El paciente indicado no existe")))
                .onStatus(status -> status.is5xxServerError(), response ->
                        Mono.error(new ResponseStatusException(
                                HttpStatus.SERVICE_UNAVAILABLE,
                                "El servicio de pacientes no está disponible")))
                .toBodilessEntity()
                .then()
                .onErrorMap(WebClientRequestException.class, exception ->
                        new ResponseStatusException(
                                HttpStatus.SERVICE_UNAVAILABLE,
                                "El servicio de pacientes no está disponible",
                                exception));
    }

    private Mono<Void> publishConfirmedEvent(Appointment previous, Appointment appointment) {
        var event = new AppointmentConfirmedEvent(
                UUID.randomUUID(),
                appointment.id(),
                appointment.patientId(),
                LocalDateTime.now(),
                "AppointmentConfirmed",
                previous.status().name(),
                appointment.status().name(),
                "appointment-service");
        return Mono.fromRunnable(() -> rabbitTemplate.convertAndSend(
                        MessagingConfiguration.EXCHANGE,
                        MessagingConfiguration.APPOINTMENT_CONFIRMED_KEY,
                        event))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}

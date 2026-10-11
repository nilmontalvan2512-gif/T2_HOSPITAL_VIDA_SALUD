package pe.edu.hospitalvidasalud.appointments;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AppointmentRepository extends ReactiveCrudRepository<Appointment, UUID> {
    Mono<Boolean> existsByProfessionalCodeAndAppointmentAtAndStatusIn(
            String professionalCode, LocalDateTime appointmentAt, Collection<AppointmentStatus> statuses);

    Mono<Boolean> existsByProfessionalCodeAndAppointmentAtAndStatusInAndIdNot(
            String professionalCode, LocalDateTime appointmentAt,
            Collection<AppointmentStatus> statuses, UUID id);
}

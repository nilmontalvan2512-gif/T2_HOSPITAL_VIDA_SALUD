package pe.edu.hospitalvidasalud.appointments;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<Appointment>> create(@Valid @RequestBody AppointmentRequest request) {
        return service.create(request)
                .map(appointment -> ResponseEntity.created(
                                URI.create("/api/appointments/" + appointment.id()))
                        .body(appointment));
    }

    @GetMapping("/{id}")
    public Mono<Appointment> findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping
    public Flux<Appointment> findAll() {
        return service.findAll();
    }

    @PatchMapping("/{id}/schedule")
    public Mono<Appointment> reschedule(
            @PathVariable UUID id, @Valid @RequestBody RescheduleAppointmentRequest request) {
        return service.reschedule(id, request.appointmentAt());
    }

    @PatchMapping("/{id}/status")
    public Mono<Appointment> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeAppointmentStatusRequest request) {
        return service.changeStatus(id, request.status());
    }
}

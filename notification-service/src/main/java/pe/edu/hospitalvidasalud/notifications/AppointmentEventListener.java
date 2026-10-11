package pe.edu.hospitalvidasalud.notifications;

import java.time.LocalDateTime;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventListener {
    private final NotificationEventRepository repository;

    public AppointmentEventListener(NotificationEventRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "notifications.appointment-confirmed")
    public void onAppointmentConfirmed(AppointmentConfirmedEvent event) {
        var notification = new NotificationEvent(
                event.eventId(),
                event.appointmentId(),
                event.patientId(),
                event.occurredAt(),
                event.eventType(),
                event.previousStatus(),
                event.newStatus(),
                event.source(),
                LocalDateTime.now(),
                null);
        repository.existsById(event.eventId())
                .filter(exists -> !exists)
                .flatMap(ignored -> repository.save(notification))
                .then()
                .block();
    }
}

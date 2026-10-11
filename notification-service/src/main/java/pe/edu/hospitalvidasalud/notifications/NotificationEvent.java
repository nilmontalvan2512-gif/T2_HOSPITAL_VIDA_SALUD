package pe.edu.hospitalvidasalud.notifications;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("notification_event")
public record NotificationEvent(
        @Id UUID eventId,
        UUID appointmentId,
        UUID patientId,
        LocalDateTime occurredAt,
        String eventType,
        String previousStatus,
        String newStatus,
        String source,
        LocalDateTime receivedAt,
        @Version Long version) {
}

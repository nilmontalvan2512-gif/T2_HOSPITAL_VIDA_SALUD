package pe.edu.hospitalvidasalud.appointments;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentConfirmedEvent(
        UUID eventId,
        UUID appointmentId,
        UUID patientId,
        LocalDateTime occurredAt,
        String eventType,
        String previousStatus,
        String newStatus,
        String source) {
}

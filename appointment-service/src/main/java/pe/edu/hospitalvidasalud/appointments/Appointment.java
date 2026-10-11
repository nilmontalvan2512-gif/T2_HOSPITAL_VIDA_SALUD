package pe.edu.hospitalvidasalud.appointments;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("appointment")
public record Appointment(
        @Id UUID id,
        UUID patientId,
        String specialty,
        LocalDateTime appointmentAt,
        String professionalCode,
        String administrativeReason,
        AppointmentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        @Version Long version) {
}

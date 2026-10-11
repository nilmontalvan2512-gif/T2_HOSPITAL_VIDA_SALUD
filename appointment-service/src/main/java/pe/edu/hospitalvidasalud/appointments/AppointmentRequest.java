package pe.edu.hospitalvidasalud.appointments;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentRequest(
        @NotNull UUID patientId,
        @NotBlank String specialty,
        @NotNull @Future LocalDateTime appointmentAt,
        @NotBlank String professionalCode,
        @NotBlank String administrativeReason) {
}

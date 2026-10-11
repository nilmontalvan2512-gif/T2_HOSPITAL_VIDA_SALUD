package pe.edu.hospitalvidasalud.appointments;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record RescheduleAppointmentRequest(@NotNull @Future LocalDateTime appointmentAt) {
}

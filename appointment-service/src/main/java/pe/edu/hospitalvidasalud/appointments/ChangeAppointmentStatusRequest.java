package pe.edu.hospitalvidasalud.appointments;

import jakarta.validation.constraints.NotNull;

public record ChangeAppointmentStatusRequest(@NotNull AppointmentStatus status) {
}

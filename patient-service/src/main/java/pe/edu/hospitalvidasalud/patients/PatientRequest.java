package pe.edu.hospitalvidasalud.patients;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record PatientRequest(
        @NotBlank String documentType,
        @NotBlank String documentNumber,
        @NotBlank String firstNames,
        @NotBlank String lastNames,
        @NotNull @Past LocalDate birthDate,
        @NotBlank String phone,
        @NotBlank @Email String email,
        String address,
        @Pattern(regexp = "(?i)ACTIVO|INACTIVO") String status) {
}

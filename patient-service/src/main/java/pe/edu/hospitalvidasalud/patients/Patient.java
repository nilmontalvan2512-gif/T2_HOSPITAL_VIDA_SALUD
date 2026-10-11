package pe.edu.hospitalvidasalud.patients;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("patient")
public record Patient(
        @Id UUID id,
        String documentType,
        String documentNumber,
        String firstNames,
        String lastNames,
        LocalDate birthDate,
        String phone,
        String email,
        String address,
        LocalDateTime registeredAt,
        String status,
        @Version Long version) {
}

package pe.edu.hospitalvidasalud.payments;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("payment")
public record Payment(
        @Id UUID id,
        UUID patientId,
        String concept,
        BigDecimal amount,
        LocalDateTime paidAt,
        String paymentMethod,
        String operationNumber,
        String status,
        @Version Long version) {
}

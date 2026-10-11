package pe.edu.hospitalvidasalud.payments;

import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface PaymentRepository extends ReactiveCrudRepository<Payment, UUID> {
    Mono<Boolean> existsByOperationNumber(String operationNumber);
}

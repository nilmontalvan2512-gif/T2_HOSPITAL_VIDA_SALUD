package pe.edu.hospitalvidasalud.payments;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {
    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public Mono<Payment> register(PaymentRequest request) {
        if (request.amount() == null || request.amount().signum() <= 0) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El monto del pago debe ser mayor que cero"));
        }
        String operationNumber = request.operationNumber().trim().toUpperCase(Locale.ROOT);
        return repository.existsByOperationNumber(operationNumber)
                .flatMap(exists -> exists
                        ? Mono.error(new ResponseStatusException(
                                HttpStatus.CONFLICT, "El número de operación ya fue registrado"))
                        : repository.save(new Payment(
                                UUID.randomUUID(),
                                request.patientId(),
                                request.concept().trim(),
                                request.amount(),
                                LocalDateTime.now(),
                                request.paymentMethod().trim(),
                                operationNumber,
                                "REGISTRADO",
                                null)))
                .onErrorMap(DuplicateKeyException.class, exception ->
                        new ResponseStatusException(
                                HttpStatus.CONFLICT, "El número de operación ya fue registrado", exception));
    }

    public Mono<Payment> findById(UUID id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pago no encontrado")));
    }

    public Flux<Payment> findAll() {
        return repository.findAll();
    }
}

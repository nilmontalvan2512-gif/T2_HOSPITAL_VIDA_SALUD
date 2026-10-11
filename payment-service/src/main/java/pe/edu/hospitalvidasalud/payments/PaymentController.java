package pe.edu.hospitalvidasalud.payments;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<Payment>> register(@Valid @RequestBody PaymentRequest request) {
        return service.register(request)
                .map(payment -> ResponseEntity.created(URI.create("/api/payments/" + payment.id()))
                        .body(payment));
    }

    @GetMapping("/{id}")
    public Mono<Payment> findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping
    public Flux<Payment> findAll() {
        return service.findAll();
    }
}

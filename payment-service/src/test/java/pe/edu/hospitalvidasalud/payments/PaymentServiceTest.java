package pe.edu.hospitalvidasalud.payments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentRepository repository;

    @InjectMocks
    private PaymentService service;

    @Test
    void rejectsZeroOrNegativePaymentAmounts() {
        var request = new PaymentRequest(
                UUID.randomUUID(), "Servicio administrativo", BigDecimal.ZERO, "EFECTIVO", "TEST-OP-01");

        StepVerifier.create(service.register(request))
                .expectErrorSatisfies(error ->
                        assertEquals(400, ((ResponseStatusException) error).getStatusCode().value()))
                .verify();

        verifyNoInteractions(repository);
    }
}

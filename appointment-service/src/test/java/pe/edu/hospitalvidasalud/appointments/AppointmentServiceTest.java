package pe.edu.hospitalvidasalud.appointments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {
    @Mock
    private AppointmentRepository repository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private AppointmentService service;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        service = new AppointmentService(repository, webClientBuilder, rabbitTemplate);
    }

    @Test
    void rejectsTransitionFromCancelledAppointment() {
        var id = UUID.randomUUID();
        var appointment = new Appointment(
                id, UUID.randomUUID(), "Medicina general", LocalDateTime.now().plusDays(2),
                "PROF-TEST", "Control administrativo", AppointmentStatus.CANCELLED,
                LocalDateTime.now(), LocalDateTime.now(), 0L);
        when(repository.findById(id)).thenReturn(Mono.just(appointment));

        StepVerifier.create(service.changeStatus(id, AppointmentStatus.CONFIRMED))
                .expectErrorSatisfies(error ->
                        assertEquals(409, ((ResponseStatusException) error).getStatusCode().value()))
                .verify();
    }

    @Test
    void confirmsOnlyPendingOrConfirmedAppointments() {
        assertEquals(true, AppointmentStatus.PENDING.canTransitionTo(AppointmentStatus.CONFIRMED));
        assertEquals(false, AppointmentStatus.CANCELLED.canTransitionTo(AppointmentStatus.CONFIRMED));
        assertEquals(false, AppointmentStatus.ATTENDED.canTransitionTo(AppointmentStatus.CANCELLED));
    }
}

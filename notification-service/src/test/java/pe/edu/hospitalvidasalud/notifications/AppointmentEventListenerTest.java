package pe.edu.hospitalvidasalud.notifications;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class AppointmentEventListenerTest {
    @Mock
    private NotificationEventRepository repository;

    @InjectMocks
    private AppointmentEventListener listener;

    @Test
    void persistsNewConfirmedAppointmentEvent() {
        var eventId = UUID.randomUUID();
        var event = new AppointmentConfirmedEvent(
                eventId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now(),
                "AppointmentConfirmed",
                "PENDING",
                "CONFIRMED",
                "appointment-service");
        when(repository.existsById(eventId)).thenReturn(Mono.just(false));
        when(repository.save(any(NotificationEvent.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        listener.onAppointmentConfirmed(event);

        verify(repository).save(any(NotificationEvent.class));
    }

    @Test
    void ignoresRedeliveredEvent() {
        var eventId = UUID.randomUUID();
        var event = new AppointmentConfirmedEvent(
                eventId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now(),
                "AppointmentConfirmed",
                "PENDING",
                "CONFIRMED",
                "appointment-service");
        when(repository.existsById(eventId)).thenReturn(Mono.just(true));

        listener.onAppointmentConfirmed(event);

        verify(repository, never()).save(any(NotificationEvent.class));
    }
}

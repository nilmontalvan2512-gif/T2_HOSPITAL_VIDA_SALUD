package pe.edu.hospitalvidasalud.notifications;

import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface NotificationEventRepository extends ReactiveCrudRepository<NotificationEvent, UUID> {
}

package pe.edu.hospitalvidasalud.notifications;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationEventRepository repository;

    public NotificationController(NotificationEventRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Flux<NotificationEvent> findAll() {
        return repository.findAll();
    }
}

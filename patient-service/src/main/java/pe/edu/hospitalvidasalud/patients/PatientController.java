package pe.edu.hospitalvidasalud.patients;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<Patient>> register(@Valid @RequestBody PatientRequest request) {
        return service.register(request)
                .map(patient -> ResponseEntity.created(URI.create("/api/patients/" + patient.id()))
                        .body(patient));
    }

    @GetMapping("/{id}")
    public Mono<Patient> findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Mono<Patient> update(@PathVariable UUID id, @Valid @RequestBody PatientRequest request) {
        return service.update(id, request);
    }

    @GetMapping
    public Flux<Patient> findAll() {
        return service.findAll();
    }
}

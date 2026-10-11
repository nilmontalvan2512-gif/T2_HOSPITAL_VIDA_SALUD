package pe.edu.hospitalvidasalud.patients;

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
public class PatientService {
    private final PatientRepository repository;

    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

    public Mono<Patient> register(PatientRequest request) {
        String documentType = request.documentType().trim().toUpperCase(Locale.ROOT);
        String documentNumber = request.documentNumber().trim();
        return repository.existsByDocumentTypeAndDocumentNumber(
                        documentType, documentNumber)
                .flatMap(exists -> exists
                        ? Mono.error(new ResponseStatusException(
                                HttpStatus.CONFLICT, "Ya existe un paciente con ese documento"))
                        : repository.save(new Patient(
                                UUID.randomUUID(),
                                documentType,
                                documentNumber,
                                request.firstNames().trim(),
                                request.lastNames().trim(),
                                request.birthDate(),
                                request.phone().trim(),
                                request.email().trim().toLowerCase(),
                                request.address(),
                                LocalDateTime.now(),
                                normalizedStatus(request.status(), "ACTIVO"),
                                null)))
                .onErrorMap(DuplicateKeyException.class, exception ->
                        new ResponseStatusException(
                                HttpStatus.CONFLICT, "Ya existe un paciente con ese documento", exception));
    }

    public Mono<Patient> findById(UUID id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Paciente no encontrado")));
    }

    public Mono<Patient> update(UUID id, PatientRequest request) {
        String documentType = request.documentType().trim().toUpperCase(Locale.ROOT);
        String documentNumber = request.documentNumber().trim();
        return findById(id)
                .flatMap(current -> repository.existsByDocumentTypeAndDocumentNumberAndIdNot(
                                documentType, documentNumber, id)
                        .flatMap(duplicate -> duplicate
                                ? Mono.error(new ResponseStatusException(
                                        HttpStatus.CONFLICT, "Ya existe un paciente con ese documento"))
                                : repository.save(new Patient(
                                        current.id(),
                                        documentType,
                                        documentNumber,
                                        request.firstNames().trim(),
                                        request.lastNames().trim(),
                                        request.birthDate(),
                                        request.phone().trim(),
                                        request.email().trim().toLowerCase(),
                                        request.address(),
                                        current.registeredAt(),
                                        normalizedStatus(request.status(), current.status()),
                                        current.version()))))
                .onErrorMap(DuplicateKeyException.class, exception ->
                        new ResponseStatusException(
                                HttpStatus.CONFLICT, "Ya existe un paciente con ese documento", exception));
    }

    public Flux<Patient> findAll() {
        return repository.findAll();
    }

    private String normalizedStatus(String status, String defaultStatus) {
        return status == null ? defaultStatus : status.trim().toUpperCase(Locale.ROOT);
    }
}

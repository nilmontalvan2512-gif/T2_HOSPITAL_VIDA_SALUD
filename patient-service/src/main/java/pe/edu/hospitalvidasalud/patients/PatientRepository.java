package pe.edu.hospitalvidasalud.patients;

import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface PatientRepository extends ReactiveCrudRepository<Patient, UUID> {
    Mono<Boolean> existsByDocumentTypeAndDocumentNumber(String documentType, String documentNumber);

    Mono<Boolean> existsByDocumentTypeAndDocumentNumberAndIdNot(
            String documentType, String documentNumber, UUID id);
}

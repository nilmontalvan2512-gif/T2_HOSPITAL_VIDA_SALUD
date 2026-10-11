package pe.edu.hospitalvidasalud.patients;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    @Mock
    private PatientRepository repository;

    @InjectMocks
    private PatientService service;

    @Test
    void registersPatientWhenDocumentIsUnique() {
        var request = new PatientRequest(
                "DNI", "TEST-001", "Ana", "Prueba",
                LocalDate.of(1995, 3, 10), "900000001", "ana@example.test", null, null);
        when(repository.existsByDocumentTypeAndDocumentNumber("DNI", "TEST-001"))
                .thenReturn(Mono.just(false));
        when(repository.save(any(Patient.class))).thenAnswer(invocation -> {
        Patient patient = invocation.getArgument(0);
        return Mono.just(new Patient(
                patient.id(), patient.documentType(), patient.documentNumber(),
                patient.firstNames(), patient.lastNames(), patient.birthDate(),
                patient.phone(), patient.email(), patient.address(), patient.registeredAt(),
                patient.status(), 0L));
        });

        StepVerifier.create(service.register(request))
                .assertNext(patient -> {
                    assertEquals("ACTIVO", patient.status());
                    assertEquals("TEST-001", patient.documentNumber());
                })
                .verifyComplete();
    }

    @Test
    void rejectsDuplicateDocumentWithConflict() {
        var request = new PatientRequest(
                "DNI", "TEST-001", "Ana", "Prueba",
                LocalDate.of(1995, 3, 10), "900000001", "ana@example.test", null, null);
        when(repository.existsByDocumentTypeAndDocumentNumber("DNI", "TEST-001"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(service.register(request))
                .expectErrorSatisfies(error -> {
                    var responseError = (ResponseStatusException) error;
                    assertEquals(409, responseError.getStatusCode().value());
                })
                .verify();
    }

    @Test
    void returnsNotFoundForUnknownPatient() {
        when(repository.findById(any(UUID.class))).thenReturn(Mono.empty());

        StepVerifier.create(service.findById(UUID.randomUUID()))
                .expectErrorSatisfies(error ->
                        assertEquals(404, ((ResponseStatusException) error).getStatusCode().value()))
                .verify();
    }
}

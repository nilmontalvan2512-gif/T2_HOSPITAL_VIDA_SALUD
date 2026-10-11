package pe.edu.hospitalvidasalud.patients;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ControlledCiFailureTest {
    @Test
    void temporaryFailureDemonstratesCiDetection() {
        assertEquals("expected", "intentional failure");
    }
}

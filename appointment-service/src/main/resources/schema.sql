CREATE TABLE IF NOT EXISTS appointment (
    id UUID PRIMARY KEY,
    patient_id UUID NOT NULL,
    specialty VARCHAR(100) NOT NULL,
    appointment_at TIMESTAMP NOT NULL,
    professional_code VARCHAR(50) NOT NULL,
    administrative_reason VARCHAR(200) NOT NULL,
    status VARCHAR(15) NOT NULL CHECK (status IN ('PENDING', 'CONFIRMED', 'ATTENDED', 'CANCELLED', 'ABSENT')),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_appointment_patient ON appointment (patient_id);
CREATE INDEX IF NOT EXISTS idx_appointment_schedule ON appointment (professional_code, appointment_at, status);
CREATE UNIQUE INDEX IF NOT EXISTS uk_appointment_active_slot
    ON appointment (professional_code, appointment_at)
    WHERE status IN ('PENDING', 'CONFIRMED');

ALTER TABLE appointment ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS payment (
    id UUID PRIMARY KEY,
    patient_id UUID NOT NULL,
    concept VARCHAR(160) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
    paid_at TIMESTAMP NOT NULL,
    payment_method VARCHAR(40) NOT NULL,
    operation_number VARCHAR(80) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL CHECK (status IN ('REGISTRADO')),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_payment_patient ON payment (patient_id);
ALTER TABLE payment ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

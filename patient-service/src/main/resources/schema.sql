CREATE TABLE IF NOT EXISTS patient (
    id UUID PRIMARY KEY,
    document_type VARCHAR(20) NOT NULL,
    document_number VARCHAR(30) NOT NULL,
    first_names VARCHAR(100) NOT NULL,
    last_names VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(150) NOT NULL,
    address VARCHAR(250),
    registered_at TIMESTAMP NOT NULL,
    status VARCHAR(15) NOT NULL CHECK (status IN ('ACTIVO', 'INACTIVO')),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_patient_document UNIQUE (document_type, document_number)
);

ALTER TABLE patient ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

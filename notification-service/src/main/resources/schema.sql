CREATE TABLE IF NOT EXISTS notification_event (
    event_id UUID PRIMARY KEY,
    appointment_id UUID NOT NULL,
    patient_id UUID NOT NULL,
    occurred_at TIMESTAMP NOT NULL,
    event_type VARCHAR(80) NOT NULL,
    previous_status VARCHAR(20) NOT NULL,
    new_status VARCHAR(20) NOT NULL,
    source VARCHAR(80) NOT NULL,
    received_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_notification_received ON notification_event (received_at DESC);
ALTER TABLE notification_event ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

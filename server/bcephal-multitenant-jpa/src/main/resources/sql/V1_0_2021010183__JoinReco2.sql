ALTER TABLE BCP_WRITE_OFF_JOIN_MODEL ADD COLUMN IF NOT EXISTS writeOffTypeColumnId BIGINT;
ALTER TABLE BCP_WRITE_OFF_JOIN_MODEL ADD COLUMN IF NOT EXISTS writeOffTypeValue TEXT;

ALTER TABLE BCP_RECONCILIATION_JOIN_MODEL_BUTTON_COLUMN ADD COLUMN IF NOT EXISTS name VARCHAR(255);
ALTER TABLE BCP_RECONCILIATION_JOIN_MODEL_BUTTON_COLUMN ADD COLUMN IF NOT EXISTS backgroundColor INTEGER;
ALTER TABLE BCP_RECONCILIATION_JOIN_MODEL_BUTTON_COLUMN ADD COLUMN IF NOT EXISTS foregroundColor INTEGER;
ALTER TABLE BCP_RECONCILIATION_JOIN_MODEL_BUTTON_COLUMN ADD COLUMN IF NOT EXISTS width INTEGER;

ALTER TABLE BCP_AUTO_RECO ADD COLUMN IF NOT EXISTS forModel BOOLEAN;
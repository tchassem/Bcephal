ALTER TABLE BCP_FORM_MODEL_FIELD ADD COLUMN IF NOT EXISTS spot BIGINT;
ALTER TABLE BCP_FORM_MODEL_FIELD ADD COLUMN IF NOT EXISTS selectAllValues BOOLEAN DEFAULT TRUE;
ALTER TABLE BCP_FORM_MODEL_FIELD ADD COLUMN IF NOT EXISTS valuesImpl TEXT;
UPDATE BCP_FORM_MODEL_FIELD SET selectAllValues = TRUE WHERE selectAllValues IS NULL;
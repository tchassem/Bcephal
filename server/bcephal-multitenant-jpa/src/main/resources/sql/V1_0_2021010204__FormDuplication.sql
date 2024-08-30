ALTER TABLE BCP_FORM_MODEL_FIELD ADD COLUMN IF NOT EXISTS allowDuplication BOOLEAN DEFAULT TRUE;
ALTER TABLE BCP_FORM_MODEL_FIELD ADD COLUMN IF NOT EXISTS duplicationValue VARCHAR(25);
UPDATE BCP_FORM_MODEL_FIELD SET allowDuplication = true WHERE allowDuplication IS NULL;
UPDATE BCP_FORM_MODEL_FIELD SET duplicationValue = 'CURRENT_VALUE' WHERE duplicationValue IS NULL;
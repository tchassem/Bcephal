ALTER TABLE BCP_JOIN ADD COLUMN IF NOT EXISTS republishGridsBeforePublication BOOLEAN;
UPDATE BCP_JOIN SET republishGridsBeforePublication = false WHERE republishGridsBeforePublication is null;
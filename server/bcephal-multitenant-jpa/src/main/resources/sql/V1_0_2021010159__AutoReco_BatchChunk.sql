ALTER TABLE BCP_AUTO_RECO ADD COLUMN IF NOT EXISTS batchChunk INTEGER DEFAULT 100;
UPDATE BCP_AUTO_RECO SET batchChunk = 100 WHERE batchChunk IS NULL;


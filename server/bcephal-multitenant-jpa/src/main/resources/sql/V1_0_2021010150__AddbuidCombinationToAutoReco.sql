ALTER TABLE BCP_AUTO_RECO ADD COLUMN IF NOT EXISTS buildCombinationsAsc BOOLEAN DEFAULT true;

UPDATE BCP_AUTO_RECO SET buildCombinationsAsc = true WHERE buildCombinationsAsc IS NULL;
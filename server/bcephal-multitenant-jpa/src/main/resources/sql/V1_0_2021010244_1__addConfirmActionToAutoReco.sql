
ALTER TABLE BCP_AUTO_RECO ADD COLUMN IF NOT EXISTS confirmAction BOOLEAN DEFAULT TRUE;
ALTER TABLE BCP_BILLING_MODEL ADD COLUMN IF NOT EXISTS confirmAction BOOLEAN DEFAULT TRUE;
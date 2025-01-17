ALTER TABLE BCP_ALARM ADD COLUMN IF NOT EXISTS category VARCHAR(50);
ALTER TABLE BCP_ALARM ADD COLUMN IF NOT EXISTS invoiceConsolidation VARCHAR(50);
ALTER TABLE BCP_ALARM ADD COLUMN IF NOT EXISTS invoiceTrigger VARCHAR(50);
ALTER TABLE BCP_ALARM ADD COLUMN IF NOT EXISTS filter BIGINT;

UPDATE BCP_ALARM SET category = 'FREE' WHERE category IS NULL;
UPDATE BCP_ALARM SET invoiceConsolidation = 'ONE_MAIL_PER_CLIENT' WHERE invoiceConsolidation IS NULL;
UPDATE BCP_ALARM SET invoiceTrigger = 'INVOICE_VALIDATED_AND_NOT_YET_SENT' WHERE invoiceTrigger IS NULL;
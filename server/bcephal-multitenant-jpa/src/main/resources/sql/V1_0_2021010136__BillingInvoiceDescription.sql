ALTER TABLE BCP_BILLING_INVOICE ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE BCP_BILLING_MODEL ADD COLUMN IF NOT EXISTS invoiceSequenceId BIGINT;
CREATE SEQUENCE IF NOT EXISTS billing_invoice_seq START WITH 1 INCREMENT BY 1;

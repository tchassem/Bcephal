ALTER TABLE bcp_email_filter DROP COLUMN IF EXISTS attachment;
ALTER TABLE bcp_email_filter DROP COLUMN IF EXISTS expeditorOperator;

ALTER TABLE bcp_email_filter ADD COLUMN IF NOT EXISTS attributeValue TEXT;
ALTER TABLE bcp_email_filter ADD COLUMN IF NOT EXISTS attributeOperator VARCHAR(100);
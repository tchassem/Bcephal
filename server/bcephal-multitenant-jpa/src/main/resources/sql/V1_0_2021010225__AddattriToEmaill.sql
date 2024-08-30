ALTER TABLE bcp_email_filter ADD COLUMN IF NOT EXISTS attributeFilter VARCHAR(100);
ALTER TABLE bcp_email_filter ADD COLUMN IF NOT EXISTS granularity VARCHAR(100);
ALTER TABLE BCP_TASK ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE BCP_TASK ADD COLUMN IF NOT EXISTS username VARCHAR(255);

ALTER TABLE BCP_TASK ADD COLUMN IF NOT EXISTS dateOperator VARCHAR(50);
ALTER TABLE BCP_TASK ADD COLUMN IF NOT EXISTS dateValue DATE;
ALTER TABLE BCP_TASK ADD COLUMN IF NOT EXISTS dateSign VARCHAR(10);
ALTER TABLE BCP_TASK ADD COLUMN IF NOT EXISTS dateNumber INTEGER DEFAULT 0;
ALTER TABLE BCP_TASK ADD COLUMN IF NOT EXISTS dateGranularity VARCHAR(50);

ALTER TABLE BCP_FORM_MODEL ADD COLUMN IF NOT EXISTS detailsInSeparatedTab BOOLEAN DEFAULT FALSE;
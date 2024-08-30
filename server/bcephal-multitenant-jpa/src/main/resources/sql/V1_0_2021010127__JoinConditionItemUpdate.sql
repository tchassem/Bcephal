ALTER TABLE BCP_JOIN_CONDITION_ITEM DROP COLUMN IF EXISTS dateValue;
ALTER TABLE BCP_JOIN_CONDITION_ITEM ADD COLUMN IF NOT EXISTS dateValue_dateOperator VARCHAR(25);
ALTER TABLE BCP_JOIN_CONDITION_ITEM ADD COLUMN IF NOT EXISTS dateValue_dateValue TIMESTAMP;
ALTER TABLE BCP_JOIN_CONDITION_ITEM ADD COLUMN IF NOT EXISTS dateValue_dateSign VARCHAR(25);
ALTER TABLE BCP_JOIN_CONDITION_ITEM ADD COLUMN IF NOT EXISTS dateValue_dateNumber INTEGER;
ALTER TABLE BCP_JOIN_CONDITION_ITEM ADD COLUMN IF NOT EXISTS dateValue_dateGranularity INTEGER;
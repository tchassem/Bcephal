ALTER TABLE BCP_TRANSFORMATION_ROUTINE_FIELD ADD COLUMN IF NOT EXISTS thousandSeparator VARCHAR(5);
UPDATE BCP_TRANSFORMATION_ROUTINE_FIELD SET thousandSeparator = ' ' WHERE thousandSeparator IS NULL;
UPDATE BCP_TRANSFORMATION_ROUTINE_MAPPING_CONDITION SET side1 = 'REFERENCE_GRID' WHERE side1 = 'LEFT';
UPDATE BCP_TRANSFORMATION_ROUTINE_MAPPING_CONDITION SET side1 = 'TARGET_GRID' WHERE side1 = 'RIGHT';
UPDATE BCP_TRANSFORMATION_ROUTINE_MAPPING_CONDITION SET side2 = 'REFERENCE_GRID' WHERE side2 = 'LEFT';
UPDATE BCP_TRANSFORMATION_ROUTINE_MAPPING_CONDITION SET side2 = 'TARGET_GRID' WHERE side2 = 'RIGHT';


ALTER TABLE BCP_TRANSFORMATION_ROUTINE_FIELD ADD COLUMN IF NOT EXISTS positionSourceType VARCHAR(25);
ALTER TABLE BCP_TRANSFORMATION_ROUTINE_FIELD ADD COLUMN IF NOT EXISTS positionDimensionId BIGINT;
ALTER TABLE BCP_TRANSFORMATION_ROUTINE_FIELD ADD COLUMN IF NOT EXISTS findIgnoreCase BOOLEAN DEFAULT TRUE;
ALTER TABLE BCP_TRANSFORMATION_ROUTINE_FIELD ADD COLUMN IF NOT EXISTS findStandaloneOnly BOOLEAN DEFAULT FALSE;
ALTER TABLE BCP_TRANSFORMATION_ROUTINE_FIELD ADD COLUMN IF NOT EXISTS replaceFoundCharactersOnly BOOLEAN DEFAULT TRUE;


ALTER TABLE BCP_TRANSFORMATION_ROUTINE_FIELD ADD COLUMN IF NOT EXISTS replaceStringValue TEXT;
ALTER TABLE BCP_TRANSFORMATION_ROUTINE_FIELD ADD COLUMN IF NOT EXISTS replaceDecimalValue DECIMAL(31, 14);
ALTER TABLE BCP_TRANSFORMATION_ROUTINE_FIELD ADD COLUMN IF NOT EXISTS replaceDimensionId BIGINT;
ALTER TABLE BCP_TRANSFORMATION_ROUTINE_FIELD ADD COLUMN IF NOT EXISTS replaceDateOperator VARCHAR(50);
ALTER TABLE BCP_TRANSFORMATION_ROUTINE_FIELD ADD COLUMN IF NOT EXISTS replaceDateValue DATE;
ALTER TABLE BCP_TRANSFORMATION_ROUTINE_FIELD ADD COLUMN IF NOT EXISTS replaceDateSign VARCHAR(50);
ALTER TABLE BCP_TRANSFORMATION_ROUTINE_FIELD ADD COLUMN IF NOT EXISTS replaceDateNumber INTEGER;
ALTER TABLE BCP_TRANSFORMATION_ROUTINE_FIELD ADD COLUMN IF NOT EXISTS replaceDateGranularity VARCHAR(50);
ALTER TABLE BCP_TRANSFORMATION_ROUTINE_FIELD ADD COLUMN IF NOT EXISTS findOperator VARCHAR(25);


UPDATE BCP_TRANSFORMATION_ROUTINE_FIELD SET findIgnoreCase = TRUE;
UPDATE BCP_TRANSFORMATION_ROUTINE_FIELD SET findStandaloneOnly = FALSE;
UPDATE BCP_TRANSFORMATION_ROUTINE_FIELD SET replaceFoundCharactersOnly = TRUE;

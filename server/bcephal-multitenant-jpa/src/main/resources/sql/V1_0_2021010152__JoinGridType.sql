ALTER TABLE BCP_JOIN_COLUMN ADD COLUMN IF NOT EXISTS gridType VARCHAR(50);
ALTER TABLE BCP_JOIN_GRID ADD COLUMN IF NOT EXISTS gridType VARCHAR(50);
ALTER TABLE BCP_JOIN_KEY ADD COLUMN IF NOT EXISTS gridType1 VARCHAR(50);
ALTER TABLE BCP_JOIN_KEY ADD COLUMN IF NOT EXISTS gridType2 VARCHAR(50);
ALTER TABLE BCP_JOIN_CONDITION_ITEM ADD COLUMN IF NOT EXISTS gridType VARCHAR(50);
ALTER TABLE BCP_JOIN_COLUMN_FIELD ADD COLUMN IF NOT EXISTS gridType VARCHAR(50);

UPDATE BCP_JOIN_COLUMN SET gridType ='GRID' WHERE gridType IS NULL;
UPDATE BCP_JOIN_GRID SET gridType ='GRID' WHERE gridType IS NULL;
UPDATE BCP_JOIN_KEY SET gridType1 ='GRID' WHERE gridType1 IS NULL;
UPDATE BCP_JOIN_KEY SET gridType2 ='GRID' WHERE gridType2 IS NULL;
UPDATE BCP_JOIN_CONDITION_ITEM SET gridType ='GRID' WHERE gridType IS NULL;
UPDATE BCP_JOIN_COLUMN_FIELD SET gridType ='GRID' WHERE gridType IS NULL;

ALTER TABLE BCP_JOIN ADD COLUMN IF NOT EXISTS published BOOLEAN;
UPDATE BCP_JOIN SET published = false WHERE published IS NULL;
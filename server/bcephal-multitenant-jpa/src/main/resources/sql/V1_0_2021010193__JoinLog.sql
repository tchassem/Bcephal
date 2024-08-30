ALTER TABLE BCP_JOIN_LOG ADD COLUMN IF NOT EXISTS publicationMethod VARCHAR(50);
UPDATE BCP_JOIN_LOG SET publicationMethod = 'NEW_GRID' WHERE publicationMethod IS NULL;

ALTER TABLE BCP_JOIN_LOG ADD COLUMN IF NOT EXISTS publicationGridType VARCHAR(50);
UPDATE BCP_JOIN_LOG SET publicationGridType = 'INPUT_GRID' WHERE publicationGridType IS NULL;

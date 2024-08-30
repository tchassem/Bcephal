ALTER TABLE BCP_SEC_ENTITY_COLUMNS ADD COLUMN IF NOT EXISTS copyReferenceName TEXT;
ALTER TABLE BCP_SEC_ENTITY_COLUMNS ADD COLUMN IF NOT EXISTS referenceType VARCHAR(25);
ALTER TABLE BCP_SEC_ENTITY_COLUMNS ADD COLUMN IF NOT EXISTS operationType VARCHAR(25);

UPDATE BCP_SEC_ENTITY_COLUMNS SET referenceType ='TRANSATION' WHERE referenceType is null;
UPDATE BCP_SEC_ENTITY_COLUMNS SET operationType ='COPY' WHERE operationType is null;
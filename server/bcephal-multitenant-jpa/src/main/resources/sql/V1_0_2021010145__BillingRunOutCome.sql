CREATE TABLE IF NOT EXISTS BCP_BILLING_RUN_OUTCOME (
    id BIGINT not null,
	bgroup BIGINT,	
	name TEXT,	
	visibleInShortcut BOOLEAN default false,		
	runNumber TEXT,
	status VARCHAR(100),
	mode_ VARCHAR(100),
	username VARCHAR(100),
	invoiceCount INTEGER,
	creditNoteCount INTEGER,
	invoiceAmount DECIMAL (31, 14),
	creditNoteAmount DECIMAL (31, 14),
	periodFrom TIMESTAMP,
	periodTo TIMESTAMP,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,		
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS  billing_run_outcome_seq START WITH 1 INCREMENT BY 1;
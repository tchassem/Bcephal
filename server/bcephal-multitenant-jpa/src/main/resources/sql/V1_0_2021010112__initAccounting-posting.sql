CREATE TABLE BCP_ACCOUNTING_POSTING (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,
	valueDate TIMESTAMP,	
	entryDate TIMESTAMP,	
	comment_ TEXT,
	username VARCHAR(100),
	balance DECIMAL (31, 14),	
	status VARCHAR(100),	
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,	
	PRIMARY KEY (id)
);

CREATE SEQUENCE posting_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_ACCOUNTING_POSTING_ENTRY (
    id BIGINT not null,
	posting BIGINT,
	position INTEGER,	
    accountId VARCHAR(100),
	accountName VARCHAR(255),
	comment_ TEXT,
	username VARCHAR(100),
	amount DECIMAL (31, 14),	
	status VARCHAR(100),
	sign_ VARCHAR(100),
	PRIMARY KEY (id)
);

CREATE SEQUENCE posting_ent__seq START WITH 1 INCREMENT BY 1;
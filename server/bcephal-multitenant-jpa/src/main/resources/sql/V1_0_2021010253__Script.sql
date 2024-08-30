CREATE TABLE IF NOT EXISTS BCP_SCRIPT (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
	code VARCHAR(50),
	description TEXT,
	script TEXT,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS script_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_SCRIPT_LOG (
    id BIGINT not null,
	scriptId BIGINT,	
	scriptName VARCHAR(100),
	mode VARCHAR(50),
	status VARCHAR(50),
	startDate TIMESTAMP,
	endDate TIMESTAMP,
	username VARCHAR(100),
	message TEXT,
	operationCode VARCHAR(100),
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS script_log_seq START WITH 1 INCREMENT BY 1;
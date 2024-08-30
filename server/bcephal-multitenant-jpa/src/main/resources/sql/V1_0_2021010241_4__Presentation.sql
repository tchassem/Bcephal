CREATE TABLE IF NOT EXISTS BCP_PRESENTATION_TEMPLATE (
    id BIGINT not null,
	name VARCHAR(255) not null,
	code TEXT,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
    description TEXT,
	repository TEXT,
	hasHeader BOOLEAN,
	hasFooter BOOLEAN,	
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS presentation_template_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_PRESENTATION (
    id BIGINT not null,
	name VARCHAR(255) not null,
	code TEXT,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
    description TEXT,
	repository TEXT,
	operationCode TEXT,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS presentation_seq START WITH 1 INCREMENT BY 1;
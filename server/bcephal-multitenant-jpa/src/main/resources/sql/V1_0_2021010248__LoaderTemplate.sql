
CREATE TABLE IF NOT EXISTS BCP_LOADER_TEMPLATE (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
	code VARCHAR(50),
	description TEXT,
	repository TEXT,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS loader_template_seq START WITH 1 INCREMENT BY 1;


-- ---------------------------- GROUP -----------------------------------
-- ------------------------------------------------------------------------
-- ---------------------------- GROUP -----------------------------------
-- ------------------------------------------------------------------------

CREATE TABLE BCP_GROUP (
    id BIGINT not null,
	name VARCHAR(100) not null,
	bgroup BIGINT,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	subjectType VARCHAR(255),	
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE group_seq START WITH 1 INCREMENT BY 1;
-- ---------------------------- JOINGRID -----------------------------------
-- ------------------------------------------------------------------------
-- ---------------------------- JOINGRID -----------------------------------
-- ------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS  BCP_JOIN_GRID (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS  join_grid_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS  BCP_JOIN_GRID_ITEM (
    id BIGINT not null,
	name VARCHAR(100) not null,
	gridId BIGINT,
	position INTEGER not null,
	mainGrid BOOLEAN DEFAULT FALSE,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS  join_grid_item_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS  BCP_JOIN_GRID_KEY (
    id BIGINT not null,
	position INTEGER not null,
	gridId1 BIGINT,
	columnId1 BIGINT,
	gridId2 BIGINT,
	columnId2 BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS  join_grid_key_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS  BCP_JOIN_GRID_COLUMN (
    id BIGINT not null,
	gridId BIGINT,
	columnId BIGINT,
	position INTEGER not null,
	type VARCHAR(100),
	dimensionName VARCHAR(100),
	name VARCHAR(100) not null,
	columnName VARCHAR(100),
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS  join_grid_column_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS  BCP_JOIN_GRID_COND (
    id BIGINT not null,
	position INTEGER not null,
	gridId1 BIGINT,
	columnId1 BIGINT,
	verb VARCHAR(5),
	operator VARCHAR (25),
	openBrackets VARCHAR(10),
	closeBrackets VARCHAR(10),
	periodValue DECIMAL (31, 14),
	gridId2 BIGINT,
	columnId2 BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS  join_grid_cond_seq START WITH 1 INCREMENT BY 1;
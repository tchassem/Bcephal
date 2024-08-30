CREATE TABLE BCP_SPREADSHEET (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,
	type VARCHAR(100),
	sourceType VARCHAR(100),
	sourceId BIGINT,
	useLink BOOLEAN DEFAULT TRUE,
	active BOOLEAN DEFAULT TRUE,
	template BOOLEAN DEFAULT FALSE,
	fileName TEXT,
	filter BIGINT,
	dbTableName TEXT,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE spreadsheet_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_SPREADSHEET_CELL (
    id BIGINT not null,
    spreadSheet BIGINT,
	name VARCHAR(100) not null,
	type VARCHAR(100),
	col INTEGER,
	row INTEGER,
	sheetName TEXT,
	sheetIndex INTEGER,
	cellMeasure BIGINT,
	filter BIGINT,
	refreshWhenEdit BOOLEAN DEFAULT FALSE,
	PRIMARY KEY (id)
);
CREATE SEQUENCE spreadsheet_cell_seq START WITH 1 INCREMENT BY 1;


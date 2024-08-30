CREATE TABLE IF NOT EXISTS BCP_UNION_GRID (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
    description TEXT,
	
	published BOOLEAN DEFAULT FALSE,
	showAllRowsByDefault BOOLEAN DEFAULT FALSE,
	allowLineCounting BOOLEAN DEFAULT FALSE,
	visibleColumnCount INTEGER,
		
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS union_grid_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_UNION_GRID_ITEM (
    id BIGINT not null,
	unionGrid BIGINT,
	gridId BIGINT,
	position INTEGER,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS union_grid_item_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS BCP_UNION_GRID_COLUMN (
    id BIGINT not null,
	unionGrid BIGINT,
	name VARCHAR(255),
	dimensionType VARCHAR(25),	
	position INTEGER,	
	show BOOLEAN DEFAULT TRUE,
	backgroundColor INTEGER,
	foregroundColor INTEGER,
	width INTEGER,
	fixedType VARCHAR(255),	
	nbrOfDecimal INTEGER DEFAULT 2,
	usedSeparator BOOLEAN DEFAULT TRUE,
	defaultFormat VARCHAR(100),	
	groupBy VARCHAR(100),
	columnIds TEXT,
	
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS union_grid_column_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_UNION_GRID_CONDITION(
	id BIGINT not null,
	unionGrid BIGINT,	
	position INTEGER,
	item1 BIGINT,
	item2 BIGINT,
	verb VARCHAR(25),
	openingBracket VARCHAR(25),
	closingBracket VARCHAR(25),
	comparator VARCHAR(25),
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS union_grid_condition_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_UNION_GRID_CONDITION_ITEM(
	id BIGINT not null,	
	type VARCHAR(25),
	dimensionType VARCHAR(25),
	columnId BIGINT,
	stringValue TEXT,	
	decimalValue DECIMAL(31, 14),	
	dateOperator VARCHAR(50),
	dateValue_dateOperator VARCHAR(25),
	dateValue_dateValue TIMESTAMP,
	dateValue_dateSign VARCHAR(25),
	dateValue_dateNumber INTEGER,
	dateValue_dateGranularity INTEGER,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS union_grid_condition_item_seq START WITH 1 INCREMENT BY 1;

DROP TABLE BCP_JOIN_GRID;
DROP SEQUENCE join_grid_seq;
DROP TABLE BCP_JOIN_GRID_ITEM;
DROP SEQUENCE join_grid_item_seq;
DROP TABLE BCP_JOIN_GRID_KEY;
DROP SEQUENCE join_grid_key_seq;
DROP TABLE BCP_JOIN_GRID_COLUMN;
DROP SEQUENCE join_grid_column_seq;
DROP TABLE BCP_JOIN_GRID_COND;
DROP SEQUENCE join_grid_cond_seq;


CREATE TABLE IF NOT EXISTS BCP_JOIN (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
	showAllRowsByDefault BOOLEAN DEFAULT FALSE,	
	allowLineCounting BOOLEAN DEFAULT FALSE,	
	consolidated BOOLEAN DEFAULT TRUE,	
	refreshGridsBeforePublication BOOLEAN DEFAULT FALSE,	
	addPublicationRunNbr BOOLEAN DEFAULT TRUE,	
	publicationRunAttributeId BIGINT,	
	publicationRunSequenceId BIGINT,	
	publicationMethod VARCHAR(25),
	publicationGridId BIGINT,
	publicationGridName VARCHAR(255),	
	userFilter BIGINT,	
	adminFilter BIGINT,	
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS join_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_JOIN_COLUMN (
    id BIGINT not null,
    joinId BIGINT not null,
    gridId BIGINT,
    columnId BIGINT,
	name VARCHAR(100) not null,
	columnName VARCHAR(100),
	category VARCHAR(100),	
	type VARCHAR(100),
	dimensionName VARCHAR(255),
	dimensionId BIGINT,
	dimensionFunction VARCHAR(255),
	dimensionFormat VARCHAR(255),
	show BOOLEAN DEFAULT TRUE,
	backgroundColor INTEGER,
	foregroundColor INTEGER,
	width INTEGER,
	fixedType VARCHAR(25),
	position INTEGER not null,	
	nbrOfDecimal INTEGER DEFAULT 2,
	usedSeparator BOOLEAN DEFAULT TRUE,
	defaultFormat VARCHAR(100),
	groupBy VARCHAR(100),	
	usedForPublication BOOLEAN DEFAULT FALSE,
	publicationDimensionId BIGINT,	
	publicationDimensionName VARCHAR(255),	
	properties BIGINT,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS join_column_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_JOIN_COLUMN_CALCULATE_ITEM(
	id BIGINT not null,
	propertiesId BIGINT,
	position INTEGER,
	openingBracket VARCHAR(25),
	closingBracket VARCHAR(25),
	sign VARCHAR(25),	
	field BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS join_column_calculate_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_JOIN_COLUMN_CONCATENATE_ITEM(
	id BIGINT not null,
	propertiesId BIGINT,
	position INTEGER,
	field BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS join_column_concatenate_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_JOIN_COLUMN_FIELD(
	id BIGINT not null,	
	type VARCHAR(25),	
	dimensionType VARCHAR(25),
	gridId BIGINT,		
	columnId BIGINT,		
	dimensionId BIGINT,		
	dimensionName VARCHAR(255),	
	startPosition INTEGER,
	endPosition INTEGER,		
	stringValue TEXT,	
	decimalValue DECIMAL(31, 14),	
	dateOperator VARCHAR(50),
	dateValue DATE,
	dateSign VARCHAR(50),
	dateNumber INTEGER,
	dateGranularity VARCHAR(50),
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS join_column_field_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_JOIN_COLUMN_PROPERTIES(
	id BIGINT not null,
	columnId BIGINT,	
	field BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS join_column_properties_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_JOIN_CONDITION(
	id BIGINT not null,
	joinId BIGINT,	
	position INTEGER,
	item1 BIGINT,
	item2 BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS join_condition_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_JOIN_CONDITION_ITEM(
	id BIGINT not null,	
	type VARCHAR(25),
	dimensionType VARCHAR(25),
	gridId BIGINT,		
	columnId BIGINT,		
	dimensionId BIGINT,		
	dimensionName VARCHAR(255),				
	stringValue TEXT,	
	decimalValue DECIMAL(31, 14),	
	dateOperator VARCHAR(50),
	dateValue DATE,
	dateSign VARCHAR(50),
	dateNumber INTEGER,
	dateGranularity VARCHAR(50),
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS join_condition_item_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_JOIN_GRID(
	id BIGINT not null,
	joinId BIGINT,	
	name VARCHAR(255),
	position INTEGER,
	gridId BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS join_grid_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_JOIN_KEY(
	id BIGINT not null,
	joinId BIGINT,
	position INTEGER,
	dimensionType VARCHAR(25),
	gridId1 BIGINT,
	columnId1 BIGINT,
	valueId1 BIGINT,
	gridId2 BIGINT,
	columnId2 BIGINT,
	valueId2 BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS join_key_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_JOIN_LOG(
	id BIGINT not null,
	joinId BIGINT,	
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,		
	publicationGridName VARCHAR(255),	
	publicationNumber TEXT,	
	publicationNbrAttributeId BIGINT,		
	publicationNbrAttributeName VARCHAR(255),	
	status VARCHAR(25),	
	mode VARCHAR(25),		
	username VARCHAR(255),			
	rowCount BIGINT,	
	message TEXT,
	endDate TIMESTAMP,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS join_log_seq START WITH 1 INCREMENT BY 1;



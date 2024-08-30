CREATE TABLE BCP_TRANSFORMATION_ROUTINE (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,
	active BOOLEAN,
	scheduled BOOLEAN,
	cronExpression VARCHAR(100),
	description TEXT,
	filter BIGINT,	
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE transformation_routine_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_TRANSFORMATION_ROUTINE_ITEM (
    id BIGINT not null,
	name VARCHAR(100) not null,
	routine BIGINT,	
	description TEXT,
	position INTEGER,
	type VARCHAR(50),
	targetGridId BIGINT,	
	targetDimensionId BIGINT,	
	applyOnlyIfEmpty BOOLEAN,
	sourceField BIGINT,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE transformation_routine_item_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE BCP_TRANSFORMATION_ROUTINE_CALCULATE_ITEM (
    id BIGINT not null,
	routineItem BIGINT,
	position INTEGER,
	openingBracket VARCHAR(25),
	closingBracket VARCHAR(25),
	sign VARCHAR(25),
	field BIGINT,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE transformation_routine_calculate_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE BCP_TRANSFORMATION_ROUTINE_CONCATENATE_ITEM (
    id BIGINT not null,
	routineItem BIGINT,
	position INTEGER,
	field BIGINT,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE transformation_routine_concatenate_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE BCP_TRANSFORMATION_ROUTINE_FIELD (
    id BIGINT not null,
	sourceType VARCHAR(50),
	stringValue TEXT,
	decimalValue DECIMAL(31, 14),
	dimensionId BIGINT,	
	positionStart INTEGER,
	positionEnd INTEGER,
	mapping BIGINT,		
	dateOperator VARCHAR(50),
	dateValue DATE,
	dateSign VARCHAR(50),
	dateNumber INTEGER,
	dateGranularity VARCHAR(50),	
	PRIMARY KEY (id)
);
CREATE SEQUENCE transformation_routine_field_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE BCP_TRANSFORMATION_ROUTINE_MAPPING (
    id BIGINT not null,
	gridId BIGINT,
	mappingColumnsIpml TEXT,
	valueColumnId BIGINT,		
	PRIMARY KEY (id)
);
CREATE SEQUENCE transformation_routine_mapping_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE BCP_TRANSFORMATION_ROUTINE_LOG (
    id BIGINT not null,
	routineId BIGINT,
	routineName TEXT,
	mode VARCHAR(50),
	status VARCHAR(50),	
	startDate TIMESTAMP,
	endDate TIMESTAMP,
	message TEXT,
	username VARCHAR(100),	
	count INTEGER,
	creationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE transformation_routine_log_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE BCP_TRANSFORMATION_ROUTINE_LOG_ITEM (
    id BIGINT not null,
	logId BIGINT,	
	itemName TEXT,
	username VARCHAR(100),	
	status VARCHAR(50),	
	startDate TIMESTAMP,
	endDate TIMESTAMP,
	message TEXT,
	count INTEGER,
	creationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE transformation_routine_log_item_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE BCP_CALCULATED_MEASURE (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
    description TEXT,
	dataSourceType VARCHAR(50) DEFAULT 'UNIVERSE',
	dataSourceId BIGINT,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);

CREATE SEQUENCE calculated_measure_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_CALCULATED_MEASURE_ITEM (
    id BIGINT not null,
	calculatedMeasure BIGINT,
	measureId BIGINT,
	active BOOLEAN,	
	position INTEGER,
    openingBracket VARCHAR(5),
    closingBracket VARCHAR(5),
    arithmeticOperator VARCHAR(25),
	PRIMARY KEY (id)
);

CREATE SEQUENCE calculated_measure_item_seq START WITH 1 INCREMENT BY 1;
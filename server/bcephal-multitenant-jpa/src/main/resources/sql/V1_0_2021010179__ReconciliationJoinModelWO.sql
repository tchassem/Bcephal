ALTER TABLE BCP_JOIN ADD COLUMN IF NOT EXISTS gridType VARCHAR(50);


CREATE TABLE IF NOT EXISTS BCP_WRITE_OFF_JOIN_MODEL (
    id BIGINT not null,	
	writeOffSide VARCHAR (25),
	writeOffMeasureId BIGINT,
	useGridMeasure BOOLEAN,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS writeoff_join_model_seq START WITH 1 INCREMENT BY 1;


create table IF NOT EXISTS BCP_WRITE_OFF_JOIN_FIELD (
	id BIGINT not null,
	model BIGINT,
	position INTEGER,
	defaultValueType VARCHAR (25),
	dimensionType VARCHAR (25),
	dimensionId BIGINT,	
	mandatory BOOLEAN,
	allowNewValue BOOLEAN,
	defaultValue BOOLEAN,
	stringValue TEXT,	
	decimalValue DECIMAL (31, 14),	
	dateOperator VARCHAR (25),
    dateValue DATE,
	dateSign VARCHAR (1),	
	dateNumber INTEGER,	
	dateGranularity VARCHAR (25),	
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS writeoff_join_field_seq START WITH 1 INCREMENT BY 1;


create table IF NOT EXISTS BCP_WRITE_OFF_JOIN_FIELD_VALUE (
	id BIGINT not null,
	field BIGINT,
	position INTEGER,
	defaultValue BOOLEAN,			
	stringValue TEXT,	
	decimalValue DECIMAL (31, 14),	
	dateOperator VARCHAR (25),
    dateValue DATE,
	dateSign VARCHAR (1),	
	dateNumber INTEGER,	
	dateGranularity VARCHAR (25),	
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS writeoff_join_field_value_seq START WITH 1 INCREMENT BY 1;
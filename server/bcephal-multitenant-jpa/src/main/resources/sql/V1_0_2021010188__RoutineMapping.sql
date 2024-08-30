ALTER TABLE BCP_TRANSFORMATION_ROUTINE_MAPPING ADD COLUMN IF NOT EXISTS gridType VARCHAR(50);

CREATE TABLE IF NOT EXISTS BCP_TRANSFORMATION_ROUTINE_MAPPING_CONDITION (
	id BIGINT not null,	
	mappingId BIGINT,
	dimensionType VARCHAR (25),	
	side1 VARCHAR (25),	
	side2 VARCHAR (25),	
	columnId1 BIGINT,	
	columnId2 BIGINT,
	operator VARCHAR (25),	
	verb VARCHAR (25),	
	openingBracket VARCHAR (25),	
	closingBracket VARCHAR (25),	
	position INTEGER,
	periodCondition VARCHAR (25),	
	stringValue TEXT,
	decimalValue DECIMAL(31, 14),	
	dateOperator VARCHAR(50),
	dateValue DATE,
	dateSign VARCHAR(50),
	dateNumber INTEGER,
	dateGranularity VARCHAR(50),	
	ecludeNullAndEmptyValue BOOLEAN,
	ignoreCase BOOLEAN,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS transformation_routine_mapping_cond_seq START WITH 1 INCREMENT BY 1;

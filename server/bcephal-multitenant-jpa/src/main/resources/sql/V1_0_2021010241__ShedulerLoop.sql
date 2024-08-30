CREATE TABLE IF NOT EXISTS BCP_VARIABLE_REFERENCE (
    id BIGINT not null,	
	dataSourceType VARCHAR(25),
	dataSourceId BIGINT,
	sourceId BIGINT,
	dimensionId BIGINT,
	uniqueValue BOOLEAN DEFAULT FALSE,
	formula TEXT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS var_reference_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_VARIABLE_REFERENCE_CONDITION (
    id BIGINT not null,	
	reference BIGINT,	
	parameterType VARCHAR(25),	
	position INTEGER,		
	verb VARCHAR(25),
    openingBracket VARCHAR(25),
    closingBracket VARCHAR(25),
    comparator VARCHAR(25),	
    keyId BIGINT,	
    conditionItemType VARCHAR(25),		
	stringValue TEXT,
	decimalValue DECIMAL(31, 14),	
	dateOperator VARCHAR(50),
	dateValue TIMESTAMP,
	dateSign VARCHAR(25),
	dateNumber INTEGER,
	dateGranularity VARCHAR(25),
	variableName TEXT,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS var_reference_cond_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_VARIABLE_INTERVAL (
    id BIGINT not null,
	dateOperator VARCHAR(50),
	dateValue TIMESTAMP,
	dateSign VARCHAR(25),
	dateNumber INTEGER,
	dateGranularity VARCHAR(25),
	intervalGranularity VARCHAR(25),
	intervalNbr INTEGER,
	intervalToRunCount INTEGER,
	ranking VARCHAR(25),
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS var_interval_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_SCHEDULER_PLANNER_ITEM_LOOP (
    id BIGINT not null,
	schedulerItemId BIGINT,	
	variableName Text,
	variableType VARCHAR(25),
	variableReference BIGINT,	
	variableInterval BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS scheduler_planner_item_loop_seq START WITH 1 INCREMENT BY 1;


ALTER TABLE BCP_SCHEDULER_PLANNER_ITEM ADD COLUMN IF NOT EXISTS itemLoop BIGINT;
ALTER TABLE BCP_SCHEDULER_PLANNER_ITEM ADD COLUMN IF NOT EXISTS parentId BIGINT;
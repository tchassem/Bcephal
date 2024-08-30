CREATE TABLE BCP_FORM_MODEL_SPOT (
    id BIGINT not null,
	dataSourceType VARCHAR(100),
	dataSourceId BIGINT,
	measureId BIGINT,		
	formula VARCHAR(10),
	PRIMARY KEY (id)
);
CREATE SEQUENCE form_model_spot_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_FORM_MODEL_SPOT_ITEM (
    id BIGINT not null,
	spot BIGINT,
	dimensionType VARCHAR(50),	
	dimensionId BIGINT,
	position INTEGER,
	verb VARCHAR(10),
	openingBracket VARCHAR(10),
	closingBracket VARCHAR(10),
	comparator VARCHAR(100),
	conditionItemType VARCHAR(10),
	stringValue TEXT,
	decimalValue DECIMAL(31, 14),
	dateOperator VARCHAR(100), 
	dateValue TIMESTAMP,
	dateSign VARCHAR(10),
	dateNumber INTEGER,
	dateGranularity VARCHAR(50),
	fieldId BIGINT,
	PRIMARY KEY (id)
	
);
CREATE SEQUENCE form_model_spot_item_seq START WITH 1 INCREMENT BY 1;
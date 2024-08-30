CREATE TABLE IF NOT EXISTS BCP_DASHBOARD_ITEM_VARIABLE (
	id BIGINT not null,
	itemId BIGINT,
	position INTEGER,
	reference BIGINT,
	name TEXT,
	active BOOLEAN,
	PRIMARY KEY (id)
);
CREATE SEQUENCE if not exists dashboard_item_var_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE if not exists DashboardItemVariableReference (
    id BIGINT not null,
	variable BIGINT,
	dataSourceType VARCHAR(100),
	dataSourceId BIGINT,
	sourceId BIGINT,
	dimensionId BIGINT,
	uniqueValue boolean,
	formula VARCHAR(100),
	PRIMARY KEY (id)
);
CREATE SEQUENCE if not exists dashboard_item_var_reference_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE if not exists BCP_DASHBOARD_ITEM_VARIABLE_REFERENCE_CONDITION (
    id BIGINT not null,
	reference BIGINT,
	parameterType VARCHAR(100),	
	position INTEGER,
	verb VARCHAR(100),
	openingBracket VARCHAR(100),
	closingBracket VARCHAR(100),
	comparator VARCHAR(100),
	keyId BIGINT,
	conditionItemType VARCHAR(100),
	stringValue TEXT,
	decimalValue DECIMAL(31, 14),
	dateOperator VARCHAR(100), 
	dateValue TIMESTAMP,
	dateSign VARCHAR(100),
	dateNumber INTEGER,
	dateGranularity VARCHAR(100),
	variavleName TEXT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE if not exists dashboard_item_var_reference_cond_seq START WITH 1 INCREMENT BY 1;







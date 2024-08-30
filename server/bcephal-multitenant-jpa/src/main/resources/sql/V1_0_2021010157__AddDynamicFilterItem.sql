CREATE TABLE IF NOT EXISTS BCP_UNIVERSE_DYNAMIC_FILTER_ITEM (
	id BIGINT not null,
	filter BIGINT,	
	dimensionType VARCHAR(50),
	dimensionId BIGINT,	
	dimensionName VARCHAR(255),
	label VARCHAR(255), 	
	position INTEGER,
	values TEXT,
	granularity VARCHAR(50),	
	startDateOperator VARCHAR (25),
	startDateValue Date,
	startDateSign VARCHAR(100),
	startDateNumber BIGINT,
	startDateGranularity VARCHAR (25),	
	endDateOperator VARCHAR (25),
	endDateValue Date,
	endDateSign VARCHAR(100),
	endDateNumber BIGINT,
	endDateGranularity VARCHAR (25),	
	usingDynamicFilter BOOLEAN DEFAULT TRUE,
	cronExpressionDynamicFilter  VARCHAR(100),
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS universe_dynanic_filter_item_seq START WITH 1 INCREMENT BY 1;


ALTER TABLE BCP_UNIVERSE_DYNAMIC_FILTER DROP COLUMN IF EXISTS periodFilter;
ALTER TABLE BCP_UNIVERSE_DYNAMIC_FILTER DROP COLUMN IF EXISTS attributeFilter;


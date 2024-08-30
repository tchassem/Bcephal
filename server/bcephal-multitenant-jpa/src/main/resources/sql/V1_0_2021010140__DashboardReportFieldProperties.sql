CREATE TABLE IF NOT EXISTS BCP_DASHBOARD_REPORT_FIELD_PROPERTIES (
    id BIGINT not null,	
	usedNetCreditDebit BOOLEAN,	
	nbrOfDecimal INTEGER,	
	usedSeparator BOOLEAN,	
	defaultFormat VARCHAR(255),
	groupBy VARCHAR(50),		
	fromDateOperator VARCHAR(50),
	fromDateValue DATE,
	fromDateSign VARCHAR(50),
	fromDateNumber INTEGER,	
	fromDateGranularity VARCHAR(50),	
	toDateOperator VARCHAR(50),
	toDateValue DATE,
	toDateSign VARCHAR(50),
	toDateNumber INTEGER,	
	toDateGranularity VARCHAR(50),	
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS dashboard_report_field_properties_seq START WITH 1 INCREMENT BY 1;
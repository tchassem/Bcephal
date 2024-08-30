ALTER TABLE BCP_UNION_GRID ADD COLUMN IF NOT EXISTS gridType VARCHAR(50);


CREATE TABLE IF NOT EXISTS BCP_RECONCILIATION_UNION_MODEL(
	id BIGINT not null,
	name VARCHAR(100) not null,
	description TEXT,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,		
	leftGrid BIGINT,	
	rigthGrid BIGINT,
	recoSequenceId BIGINT,		
	allowPartialReco BOOLEAN,
	partialRecoSequenceId BIGINT,
	allowFreeze BOOLEAN,
	freezeSequenceId BIGINT,	
	allowNeutralization BOOLEAN,
	neutralizationSequenceId BIGINT,	
	neutralizationRequestSelectValue BOOLEAN,
	neutralizationAllowCreateNewValue BOOLEAN,
	neutralizationInsertNote BOOLEAN,
	neutralizationMandatoryNote BOOLEAN,	
	neutralizationMessage VARCHAR(255),		
	rigthGridPosition VARCHAR(50),		
	allowWriteOff BOOLEAN,
	writeOffModel BIGINT,
	balanceFormula VARCHAR(100),
	useDebitCredit BOOLEAN,
	allowDebitCreditLineColor BOOLEAN,	
	debitLineColor INTEGER,
	creditLineColor INTEGER,	
	addRecoDate BOOLEAN,
	addUser BOOLEAN,
	addAutomaticManual BOOLEAN,
	addNote BOOLEAN,
	mandatoryNote BOOLEAN,
	method VARCHAR(100),	
	allowReconciliatedAmountLog BOOLEAN,	
	reconciliatedAmountLogGridId BIGINT,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS reconciliation_union_model_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_RECONCILIATION_UNION_MODEL_GRID(
	id BIGINT not null,	
	grid BIGINT,		
	measureColumnId BIGINT,	
	recoTypeColumnId BIGINT,
	partialRecoTypeColumnId BIGINT,	
	reconciliatedMeasureColumnId BIGINT,	
	remainningMeasureColumnId BIGINT,	
	freezeTypeColumnId BIGINT,	
	neutralizationTypeColumnId BIGINT,	
	noteTypeColumnId BIGINT,	
	recoDateColumnId BIGINT,	
	debitCreditColumnId BIGINT,		
	userColumnId BIGINT,
	modeColumnId BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS reconciliation_union_model_grid_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_RECONCILIATION_UNION_MODEL_ENRICHMENT(
	id BIGINT not null,	
	model BIGINT,
	position INTEGER,
	dimensionType VARCHAR(50),
	targetSide VARCHAR(50),
	targetColumnId BIGINT,	
	sourceSide VARCHAR(50),
	sourceColumnId BIGINT,
	stringValue TEXT,	
	decimalValue DECIMAL (31, 14),	
	dateOperator VARCHAR (25),
    dateValue DATE,
	dateSign VARCHAR (1),	
	dateNumber INTEGER,	
	dateGranularity VARCHAR (25),	
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS reconciliation_union_model_enrichment_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_RECONCILIATION_UNION_MODEL_BUTTON_COLUMN(
	id BIGINT not null,	
	model BIGINT,
	name VARCHAR(255),
	position INTEGER,
	columnId BIGINT,	
	side VARCHAR(50),
	backgroundColor INTEGER,
	foregroundColor INTEGER,	
	width INTEGER,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS reconciliation_union_model_button_col_seq START WITH 1 INCREMENT BY 1;



CREATE TABLE IF NOT EXISTS BCP_WRITE_OFF_UNION_MODEL (
    id BIGINT not null,	
	writeOffSide VARCHAR (25),
	writeOffMeasureId BIGINT,
	useGridMeasure BOOLEAN,
	writeOffTypeColumnId BIGINT,
	writeOffTypeValue TEXT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS writeoff_union_model_seq START WITH 1 INCREMENT BY 1;


create table IF NOT EXISTS BCP_WRITE_OFF_UNION_FIELD (
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
CREATE SEQUENCE IF NOT EXISTS writeoff_union_field_seq START WITH 1 INCREMENT BY 1;


create table IF NOT EXISTS BCP_WRITE_OFF_UNION_FIELD_VALUE (
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
CREATE SEQUENCE IF NOT EXISTS writeoff_union_field_value_seq START WITH 1 INCREMENT BY 1;

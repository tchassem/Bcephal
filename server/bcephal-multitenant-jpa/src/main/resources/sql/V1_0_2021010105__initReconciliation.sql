-- ---------------------------- RECONCILIATION -----------------------------------
-- ------------------------------------------------------------------------
-- ---------------------------- RECONCILIATION -----------------------------------
-- ------------------------------------------------------------------------


CREATE TABLE BCP_RECONCILIATION_LOG(
    id BIGINT not null,
    reconciliation BIGINT,	
	username VARCHAR(100),
	recoType VARCHAR(50),
	action VARCHAR(50),
	leftAmount DECIMAL (31, 14),
	rigthAmount DECIMAL (31, 14),
	balanceAmount DECIMAL (31, 14),
	writeoffAmount DECIMAL (31, 14),
	reconciliationNbr TEXT,	
	creationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE reconciliation_log_seq START WITH 1 INCREMENT BY 1;



CREATE TABLE BCP_RECONCILIATION_MODEL (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
	leftGrid BIGINT,
	rigthGrid BIGINT,
	bottomGrid BIGINT,
	recoAttributeId BIGINT,
	recoSequenceId BIGINT,
	allowPartialReco BOOLEAN,
	partialRecoAttributeId BIGINT,
	partialRecoSequenceId BIGINT,
	reconciliatedMeasureId BIGINT,
	remainningMeasureId BIGINT,
	allowFreeze BOOLEAN,
	freezeAttributeId BIGINT,
	freezeSequenceId BIGINT,
	
	allowNeutralization BOOLEAN,
	neutralizationAttributeId BIGINT,
	neutralizationSequenceId BIGINT,
	neutralizationRequestSelectValue BOOLEAN,
	neutralizationAllowCreateNewValue BOOLEAN,
	neutralizationInsertNote BOOLEAN,
	neutralizationMandatoryNote BOOLEAN,
	neutralizationMessage TEXT,
	
	noteAttributeId BIGINT,
	rigthGridPosition VARCHAR(50),
	allowWriteOff BOOLEAN,
	writeOffModel BIGINT,
	balanceFormula VARCHAR (25),
	useDebitCredit BOOLEAN,
	allowDebitCreditLineColor BOOLEAN,
	debitLineColor INTEGER,
	creditLineColor INTEGER,
	addRecoDate BOOLEAN,
	addUser BOOLEAN,
	addAutomaticManual BOOLEAN,
	addNote BOOLEAN,
	mandatoryNote BOOLEAN,
	recoPeriodId BIGINT,
	leftMeasureId BIGINT,
	rigthMeasureId BIGINT,	
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE reconciliation_model_seq START WITH 1 INCREMENT BY 1;


create table BCP_RECONCILIATION_CONDITION (
	id BIGINT not null,	
	autoRecoId BIGINT,
	recoModelId BIGINT,
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
	dateOperator VARCHAR (25),
    dateValue DATE,
	dateSign VARCHAR (1),	
	dateNumber INTEGER,	
	dateGranularity VARCHAR (25),
	ecludeNullAndEmptyValue BOOLEAN,
	PRIMARY KEY (id)
);
CREATE SEQUENCE reconciliation_condition_seq START WITH 1 INCREMENT BY 1;


create table BCP_RECONCILIATION_MODEL_ENRICHMENT (
	id BIGINT not null,
	model BIGINT,
	position INTEGER,		
	dimensionType VARCHAR (25),
	targetColumnId BIGINT,
	targetSide VARCHAR (25),
	sourceSide VARCHAR (25),
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
CREATE SEQUENCE reconciliation_model_enrichment_seq START WITH 1 INCREMENT BY 1;





CREATE TABLE BCP_WRITE_OFF_MODEL (
    id BIGINT not null,	
	writeOffMeasureSide VARCHAR (25),
	writeOffMeasureId BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE writeoff_model_seq START WITH 1 INCREMENT BY 1;


create table BCP_WRITE_OFF_FIELD (
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
CREATE SEQUENCE writeoff_field_seq START WITH 1 INCREMENT BY 1;


create table BCP_WRITE_OFF_FIELD_VALUE (
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
CREATE SEQUENCE writeoff_field_value_seq START WITH 1 INCREMENT BY 1;




CREATE TABLE BCP_AUTO_RECO (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,
	recoId BIGINT,
	filter BIGINT,
	active BOOLEAN,
	method VARCHAR (25),		
	useCombinations BOOLEAN,		
	maxDurationPerLine INTEGER,		
	condition VARCHAR (25),			
	conditionMinValue DECIMAL (31, 14),	
	conditionMaxValue DECIMAL (31, 14),	
	scheduled BOOLEAN,	
	cronExpression VARCHAR (100),
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE auto_reco_seq START WITH 1 INCREMENT BY 1;


create table BCP_AUTO_RECO_COMMON_DIMENSION (
	id BIGINT not null,	
	recoId BIGINT,
	dimensionType VARCHAR (25),	
	dimensionId BIGINT,
	dimensionName TEXT,			
	periodCondition VARCHAR (25),
	dateOperator VARCHAR (25),
    dateValue DATE,
	dateSign VARCHAR (1),	
	dateNumber INTEGER,	
	dateGranularity VARCHAR (25),
	PRIMARY KEY (id)
);
CREATE SEQUENCE auto_reco_common_dimension_seq START WITH 1 INCREMENT BY 1;






create table BCP_AUTO_RECO_LOG (
	id BIGINT not null,	
	recoId BIGINT,	
	recoName VARCHAR (100),	
	recoAttributeId BIGINT,	
	recoAttributeName TEXT,	
	endDate TIMESTAMP,
	status VARCHAR (25),
	mode VARCHAR (25),	
	username VARCHAR (100),	
	leftRowCount BIGINT,	
	rigthRowCount BIGINT,	
	reconciliatedLeftRowCount BIGINT,	
	reconciliatedRigthRowCount BIGINT,		
	PRIMARY KEY (id)
);
CREATE SEQUENCE auto_reco_log_seq START WITH 1 INCREMENT BY 1;



create table BCP_AUTO_RECO_LOG_ITEM (
	id BIGINT not null,		
	logId BIGINT,	
	username VARCHAR (100),	
	recoNumber INTEGER,
	mode VARCHAR (25),	
	leftRowCount BIGINT,
	rigthRowCount BIGINT,
	leftAmount DECIMAL (31, 14),	
	rigthAmount DECIMAL (31, 14),	
	balanceAmount DECIMAL (31, 14),	
	writeoffAmount DECIMAL (31, 14),
	PRIMARY KEY (id)
);
CREATE SEQUENCE auto_reco_log_item_seq START WITH 1 INCREMENT BY 1;
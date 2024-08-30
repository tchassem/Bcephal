ALTER TABLE BCP_JOIN_GRID ADD COLUMN IF NOT EXISTS mainGrid BOOLEAN DEFAULT FALSE;
UPDATE BCP_JOIN_GRID SET mainGrid = FALSE WHERE mainGrid IS NULL;


CREATE TABLE IF NOT EXISTS BCP_RECONCILIATION_JOIN_MODEL(
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
CREATE SEQUENCE IF NOT EXISTS reconciliation_join_model_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_RECONCILIATION_JOIN_MODEL_GRID(
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
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS reconciliation_join_model_grid_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_RECONCILIATION_JOIN_MODEL_ENRICHMENT(
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
CREATE SEQUENCE IF NOT EXISTS reconciliation_join_model_enrichment_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_RECONCILIATION_JOIN_MODEL_BUTTON_COLUMN(
	id BIGINT not null,	
	model BIGINT,
	position INTEGER,
	columnId BIGINT,	
	side VARCHAR(50),		
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS reconciliation_join_model_button_col_seq START WITH 1 INCREMENT BY 1;
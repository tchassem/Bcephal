-- ---------------------------- FORM -----------------------------------
-- ---------------------------------------------------------------------
-- ---------------------------- FORM -----------------------------------
-- ---------------------------------------------------------------------

DROP TABLE if exists BCP_FORM_MODEL;
DROP TABLE if exists BCP_SUB_FORM_MODEL;
DROP TABLE if exists BCP_FORM_MODEL_FIELD;
DROP TABLE if exists BCP_FORM_MODEL_MENU;
DROP TABLE if exists BCP_FORM_MODEL_FIELD_VALIDATOR;

-- delete sequence ---

DROP SEQUENCE if exists form_model_seq;
DROP SEQUENCE if exists sub_form_model_seq;
DROP SEQUENCE if exists form_model_field_seq;
DROP SEQUENCE if exists form_model_menu_seq;
DROP SEQUENCE if exists form_model_field_validator_seq;




CREATE TABLE if not exists BCP_FORM_MODEL (
    id BIGINT not null,    
	name VARCHAR(100) not null,
	dataSourceType VARCHAR(100),
	dataSourceId BIGINT, 
	published BOOLEAN,
	allowEditor BOOLEAN,
	editorTitle TEXT,
	allowBrowser BOOLEAN,
	browserTitle TEXT,
	showFieldLabel BOOLEAN,
	fieldLabelSize INTEGER,
	fieldEditorSize INTEGER,
	maxFieldPerRow INTEGER,
	maxFieldPerCol INTEGER,
	allowValidation BOOLEAN,
	menu BIGINT,
	userFilter BIGINT,
	adminFilter BIGINT,
	bgroup BIGINT,	
	visibleInShortcut BOOLEAN DEFAULT TRUE,	
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE if not exists form_model_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE if not exists BCP_FORM_MODEL_BUTTON (
    id BIGINT not null,
    modelId BIGINT, 
    type VARCHAR(100),       
	name VARCHAR(100) not null,
	fieldId BIGINT,	
	schedulerId BIGINT,
	icon TEXT,
	style TEXT,	
	position INTEGER,
	active BOOLEAN,
	stringValue TEXT,	
	decimalValue DECIMAL (31, 14),
	allowUserConfirmation BOOLEAN,	
	userConfirmationMessage	TEXT,
	dateOperator VARCHAR(100), 
	dateValue TIMESTAMP,
	dateSign VARCHAR(100),
	dateNumber INTEGER,
	dateGranularity VARCHAR(100),	
	PRIMARY KEY (id)
);
CREATE SEQUENCE if not exists form_model_button_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE if not exists BCP_FORM_MODEL_FIELD (
    id BIGINT not null,    
	modelId BIGINT,
	category VARCHAR(100),
	name VARCHAR(100) not null,
	type VARCHAR(50),
    nature VARCHAR(25),
	dimensionType VARCHAR(50),
	dimensionId BIGINT,
	dimensionFunction TEXT,	
	rowPosition INTEGER,
	colPosition INTEGER,
	position INTEGER,
	showLabel BOOLEAN,
	label TEXT,	
	description TEXT,	
	labelSize INTEGER,
	editorSize INTEGER,
	mandatory BOOLEAN,
	visible BOOLEAN,
	readOnly BOOLEAN,
	valueGeneratedBySystem BOOLEAN,
	key BOOLEAN,
	showColumnInBrowser BOOLEAN,
	backgroundColor INTEGER,
	foregroundColor INTEGER,
	width INTEGER,
	fixedType TEXT,
	defaultStringValue TEXT,
	defaultDecimalValue DECIMAL (31, 14),
	defaultDateValue TIMESTAMP,
	applyDefaultValueIfCellEmpty BOOLEAN,
	applyDefaultValueToFutureLine BOOLEAN,
	groupBy VARCHAR(100),
	nbrOfDecimal INTEGER,
	usedSeparator BOOLEAN,
	defaultFormat TEXT,
	reference BIGINT,	
	dateOperator VARCHAR(100), 
	dateValue TIMESTAMP,
	dateSign VARCHAR(100),
	dateNumber INTEGER,
	dateGranularity VARCHAR(100),	
	PRIMARY KEY (id)
);
CREATE SEQUENCE if not exists form_model_field_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE if not exists BCP_FORM_MODEL_FIELD_VALIDATION_ITEM (
    id BIGINT not null,
    fieldId BIGINT,
	type VARCHAR(50),
	dimensionType VARCHAR(50),
	integerValue INTEGER,	
	decimalValue DECIMAL(31, 14),
	stringValue TEXT,
	errorMessage TEXT,
	active BOOLEAN,
	dateOperator VARCHAR(100), 
	dateValue TIMESTAMP,
	dateSign VARCHAR(100),
	dateNumber INTEGER,
	dateGranularity VARCHAR(100),		
	PRIMARY KEY (id)
);
CREATE SEQUENCE if not exists form_model_field_val_item_seq START WITH 1 INCREMENT BY 1;




CREATE TABLE if not exists BCP_FORM_MODEL_MENU (
    id BIGINT not null,
	modelId BIGINT,
	name VARCHAR(256) not null,
	active BOOLEAN,	
	parent VARCHAR(256),
	icon  TEXT,	
	position INTEGER,	
	hasNewMenu BOOLEAN,
	newMenuName VARCHAR(256),	
	hasListMenu BOOLEAN,
	listMenuName VARCHAR(256),	
	PRIMARY KEY (id)
);
CREATE SEQUENCE if not exists form_model_menu_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE if not exists BCP_FORM_MODEL_FIELD_REFERENCE (
    id BIGINT not null,
	dataSourceType VARCHAR(100),
	dataSourceId BIGINT,
	sourceId BIGINT,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE if not exists form_model_filed_reference_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE if not exists BCP_FORM_MODEL_FIELD_REFERENCE_CONDITION (
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
	fieldId BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE if not exists form_model_filed_reference_cond_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS BCP_LABEL(
	id BIGINT not null,
	code VARCHAR(255),
	lang VARCHAR(25),
	value VARCHAR(255),
	category VARCHAR(100),
	position INTEGER,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS label_seq START WITH 1 INCREMENT BY 1;


	
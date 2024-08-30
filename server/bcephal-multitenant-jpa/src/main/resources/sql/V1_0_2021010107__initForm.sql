-- ---------------------------- FORM -----------------------------------
-- ---------------------------------------------------------------------
-- ---------------------------- FORM -----------------------------------
-- ---------------------------------------------------------------------

CREATE TABLE BCP_FORM_MODEL (
    id BIGINT not null,
    parentId BIGINT,  
    subGrid BOOLEAN,  
    gridId BIGINT,    
	name VARCHAR(100) not null,
	description TEXT,
	bgroup BIGINT,	
	active BOOLEAN,	
	allowEditorView BOOLEAN,
	editorViewTitle TEXT,	
	allowBrowserView BOOLEAN,
	browserViewTitle TEXT,
	allowValidation BOOLEAN,
	menu BIGINT,	
	visibleInShortcut BOOLEAN DEFAULT TRUE,	
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE form_model_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_SUB_FORM_MODEL (
    id BIGINT not null,
    gridId BIGINT, 
    formId BIGINT,       
	name VARCHAR(100) not null,
	description TEXT,
	bgroup BIGINT,	
	active BOOLEAN,	
	allowEditorView BOOLEAN,
	editorViewTitle TEXT,	
	allowBrowserView BOOLEAN,
	browserViewTitle TEXT,
	allowValidation BOOLEAN,		
	joinFormFieldId BIGINT,
	joinSubFormFieldId BIGINT,
	displayInSeparatedTab BOOLEAN,	
	subFormType VARCHAR(25),
	position INTEGER,
	visibleInShortcut BOOLEAN DEFAULT TRUE,	
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE sub_form_model_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_FORM_MODEL_FIELD (
    id BIGINT not null,    
    formId BIGINT,
	subFormId BIGINT,
	columnId BIGINT,
	parentId BIGINT,
	is_group BOOLEAN DEFAULT FALSE,
	orientation VARCHAR(15),
	labelPosition VARCHAR(15),
	dimensionId BIGINT,	
	dimensionName TEXT,	
	dimensionType VARCHAR(25),			
	label  VARCHAR(100),	
	description TEXT,	
	position INTEGER,
	linkedAttributeId BIGINT,	
	referenceFieldId BIGINT,	
	type VARCHAR(50),
	nature VARCHAR(25),				
	nbrOfDecimal INTEGER DEFAULT 2,
	usedSeparator BOOLEAN DEFAULT TRUE,
	defaultFormat VARCHAR(100),	
	is_key BOOLEAN,
	visibleInEditor BOOLEAN,
	visibleInBrowser BOOLEAN,
	allowValidation BOOLEAN,
	mandatory BOOLEAN,	
	attachmentName TEXT,
	backgroundColor INTEGER,
	foregroundColor INTEGER,	
	fontFamilly VARCHAR(100),	
	fontSize INTEGER,    
	PRIMARY KEY (id)
);
CREATE SEQUENCE form_model_field_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_FORM_MODEL_MENU (
    id BIGINT not null,
	formId BIGINT,
	caption VARCHAR(100) not null,
	active BOOLEAN,	
	parent VARCHAR(100),
	allowNewMenu BOOLEAN,
	newMenuCaption VARCHAR(100),	
	allowListMenu BOOLEAN,
	listMenuCaption VARCHAR(100),	
	position INTEGER,
	PRIMARY KEY (id)
);
CREATE SEQUENCE form_model_menu_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_FORM_MODEL_FIELD_VALIDATOR (
    id BIGINT not null,
    fieldId BIGINT,
	type VARCHAR(50),
	integerValue INTEGER,
	decimalValue DECIMAL(31, 14),
	StringValue TEXT,
	dateOperator VARCHAR (25),
    dateValue DATE,
	dateSign VARCHAR (1),	
	dateNumber INTEGER,	
	dateGranularity VARCHAR (25),
	position INTEGER,
	message TEXT,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE form_model_field_validator_seq START WITH 1 INCREMENT BY 1;

	
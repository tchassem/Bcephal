-- ---------------------------- SOURCING -----------------------------------
-- ------------------------------------------------------------------------
-- ---------------------------- SOURCING -----------------------------------
-- ------------------------------------------------------------------------

CREATE TABLE BCP_GRID (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,
	type VARCHAR(100),
	category VARCHAR(100),
	status VARCHAR(100),
	sourceType VARCHAR(100),
	sourceId BIGINT,	
	editable BOOLEAN DEFAULT FALSE,
	showAllRowsByDefault BOOLEAN DEFAULT FALSE,
	consolidated BOOLEAN DEFAULT FALSE,
	allowLineCounting BOOLEAN DEFAULT TRUE,
	useLink BOOLEAN DEFAULT FALSE,
	userFilter BIGINT,
	adminFilter BIGINT,
	debit BOOLEAN,
	credit BOOLEAN,
	rowType VARCHAR(50),
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE grid_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_GRID_COLUMN (
    id BIGINT not null,
	name VARCHAR(100) not null,
	type VARCHAR(100),
	dimensionName VARCHAR(255),
	dimensionId BIGINT,
	dimensionFunction VARCHAR(255),
	dimensionFormat VARCHAR(255),
	grid BIGINT,
	category VARCHAR(100),
	position INTEGER not null,
	mandatory BOOLEAN DEFAULT FALSE,
	editable BOOLEAN DEFAULT FALSE,
	showValuesInDropList BOOLEAN DEFAULT FALSE,
	show BOOLEAN DEFAULT FALSE,
	backgroundColor  INTEGER,
	foregroundColor  INTEGER,
	width  INTEGER,
	fixedType VARCHAR(255),
	defaultStringValue VARCHAR(255),
	defaultDecimalValue DECIMAL (31, 14),
	defaultDateValue DATE,
	applyDefaultValueIfCellEmpty BOOLEAN DEFAULT FALSE,
	applyDefaultValueToFutureLine BOOLEAN DEFAULT FALSE,
	nbrOfDecimal INTEGER DEFAULT 2,
	usedSeparator BOOLEAN DEFAULT TRUE,
	defaultFormat VARCHAR(100),
	groupBy VARCHAR(100),
	PRIMARY KEY (id)
);
CREATE SEQUENCE grid_column_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_LINK (
    id BIGINT not null,
	name VARCHAR(100) not null,
	position INTEGER not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,
	grid BIGINT,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE link_seq START WITH 1 INCREMENT BY 1;




CREATE TABLE BCP_LINKED_ATTRIBUTE (
    id BIGINT not null,
	linkType VARCHAR(100),
	position INTEGER not null,
	iskey BOOLEAN DEFAULT FALSE,
	attributeId BIGINT,
	attributeName text,
	linkId BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE linked_attribute_seq START WITH 1 INCREMENT BY 1;



CREATE TABLE BCP_FILE_LOADER (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
	active BOOLEAN,
	hasHeader BOOLEAN,
	headerRowCount INTEGER,
	file TEXT,
	sheetName TEXT,
	sheetIndex INTEGER,
	loadAllSheets BOOLEAN,
	indentifySheetByPosition BOOLEAN,
	repository TEXT,
	repositoryOnServer TEXT,
	fileSeparator VARCHAR(25),
	fileExtension VARCHAR(25),
	uploadMethod VARCHAR(50),	
	targetId BIGINT,
	targetName TEXT,
	allowBackup BOOLEAN,
	maxBackupCount INTEGER,
	scheduled BOOLEAN,
	cronExpression TEXT,	
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE file_loader_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_FILE_LOADER_COLUMN (
    id BIGINT not null,
    loader BIGINT,
    position INTEGER not null,
	type VARCHAR(25),
	dimensionId  BIGINT,   
	fileColumn VARCHAR(255),
	grilleColumn BIGINT,
	sheetPposition INTEGER,
	sheetName TEXT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE file_loader_column_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_FILE_LOADER_NAME_CONDITION (
    id BIGINT not null,
    loader BIGINT,
    position INTEGER not null,    
	filter VARCHAR(255) not null,
	condition VARCHAR(25),
	PRIMARY KEY (id)
);
CREATE SEQUENCE file_loader_name_cond_seq START WITH 1 INCREMENT BY 1;
 
CREATE TABLE BCP_FILE_LOADER_LOG (
    id BIGINT not null,
	loaderId BIGINT,
	loaderName VARCHAR(100),
	uploadMethod VARCHAR(25),
	mode VARCHAR(25),
	status VARCHAR(50),	
	username VARCHAR(100),
	fileCount INTEGER,
	emptyFileCount INTEGER,
	errorFileCount INTEGER,
	loadedFileCount INTEGER,
	error BOOLEAN,
	message TEXT,
	startDate TIMESTAMP,
	endDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE file_loader_log_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_FILE_LOADER_LOG_ITEM (
    id BIGINT not null,
	log BIGINT,
	file VARCHAR(100),
	lineCount INTEGER,
	empty BOOLEAN,
	loaded BOOLEAN,
	error BOOLEAN,
	message TEXT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE file_loader_log_item_seq START WITH 1 INCREMENT BY 1;





CREATE TABLE BCP_DYNAMIC_FORM (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
	gridId BIGINT,	
	menu BIGINT,
	active BOOLEAN,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE dynamic_form_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE BCP_DYNAMIC_SUB_FORM (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
	gridId BIGINT,	
	formId BIGINT,	
	joinFormFieldId BIGINT,
	joinSubFormFieldId BIGINT,
	displayInSeparatedTab BOOLEAN,	
	subFormType VARCHAR(25),
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE dynamic_sub_form_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE BCP_DYNAMIC_FORM_FIELD (
    id BIGINT not null,
	name VARCHAR(100) not null,
	description TEXT,
	formId BIGINT,	
	subFormId BIGINT,	
	column_id BIGINT,	
	fieldNature VARCHAR(25),
	fieldType VARCHAR(50),
	position INTEGER,
	key BOOLEAN,	
	visible BOOLEAN,
	editorVisible BOOLEAN,
	labelVisible BOOLEAN,
	showColumnInBrowser BOOLEAN,	
	readOnly BOOLEAN,
	valueGeneratedBySystem BOOLEAN,	
	defaultValue TEXT,
	allowValidation BOOLEAN,
	mandatory BOOLEAN,
	mandatoryErrorMessage VARCHAR(255),	
	allowMinLengthValidation BOOLEAN,
	minLength INTEGER,
	minLengthErrorMessage VARCHAR(255),	
	allowMaxLengthValidation BOOLEAN,
	maxLength INTEGER,
	maxLengthErrorMessage VARCHAR(255),	
	allowMinValueValidation BOOLEAN,
	minValue TEXT,
	minValueErrorMessage VARCHAR(255),	
	allowMaxValueValidation BOOLEAN,
	maxValue TEXT,
	maxValueErrorMessage VARCHAR(255),	
	subField BOOLEAN,
	linkedAttributeId BIGINT,
	referenceFieldId BIGINT,		
	specificPeriod VARCHAR(25),
	PRIMARY KEY (id)
);
CREATE SEQUENCE dynamic_form_field_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE BCP_DYNAMIC_FORM_MENU (
    id BIGINT not null,
	name VARCHAR(100) not null,
	formId BIGINT,
	active BOOLEAN,
	parent VARCHAR(100),
	position INTEGER,
	allowNewMenu BOOLEAN,
	newMenuName VARCHAR(100),
	allowListMenu BOOLEAN,
	listMenuName VARCHAR(100),	
	PRIMARY KEY (id)
);
CREATE SEQUENCE dynamic_form_menu_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_SPOT (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,
	measureFilterItem BIGINT,
	filter BIGINT,
	description TEXT,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE spot_seq START WITH 1 INCREMENT BY 1;

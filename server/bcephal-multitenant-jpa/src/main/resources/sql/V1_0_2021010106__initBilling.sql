-- ---------------------------- BILLING -----------------------------------
-- ------------------------------------------------------------------------
-- ---------------------------- BILLING -----------------------------------
-- ------------------------------------------------------------------------

CREATE TABLE BCP_BILL_TEMPLATE (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
	code VARCHAR(50),
	description TEXT,
	repository TEXT,
	mainFile TEXT,	
	systemTemplate BOOLEAN,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE bill_template_seq START WITH 1 INCREMENT BY 1;

---------------------------------INVOICE-------------------------------------------

CREATE TABLE BCP_BILLING_INVOICE (
    id BIGINT not null,
    name VARCHAR(100) not null,
    parent VARCHAR(100),
    type VARCHAR(50),
	file TEXT ,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
	status VARCHAR(100),
    reference  VARCHAR(100),
	runNumber  VARCHAR(100),
	orderReference  VARCHAR(100),
	communicationMessage  VARCHAR(100),
	departmentNumber  VARCHAR(100),
    clientContact  VARCHAR(100),
	clientNumber  VARCHAR(100),
	clientName  VARCHAR(100),
	clientLegalForm  VARCHAR(100),
	clientAdressStreet  VARCHAR(100),
	clientAdressPostalCode  VARCHAR(100),
	clientAdressCity  VARCHAR(100),
	clientAdressCountry  VARCHAR(100),
	clientVatNumber  VARCHAR(100),
	clientEmail  VARCHAR(100),
	clientPhone  VARCHAR(100),
	clientLanguage  VARCHAR(100) ,
	billingCompanyNumber  VARCHAR(100),
	billingCompanyName  VARCHAR(100),
	billingCompanyLegalForm  VARCHAR(100),
	billingCompanyAdressStreet  VARCHAR(100),
	billingCompanyAdressPostalCode  VARCHAR(100),
	billingCompanyAdressCity  VARCHAR(100),
	billingCompanyAdressCountry  VARCHAR(100),
	billingCompanyVatNumber  VARCHAR(100),
	billingCompanyEmail  VARCHAR(100),
	billingCompanyPhone  VARCHAR(100),
	amountWithoutVat BIGINT ,
	vatAmount   NUMERIC(19, 5),
	billingAmountWithoutVat  NUMERIC(19, 5),
	billingVatAmount  NUMERIC(19, 5),
	amountUnit  VARCHAR(100),
	billTemplateCode  VARCHAR(100),
	version  INTEGER,
	manuallyModified  BOOLEAN,
	subjectToVat  BOOLEAN,
	useUnitCost  BOOLEAN,
	pivotValues  VARCHAR(100),
	orderItems  BOOLEAN,
	orderItemsAsc  BOOLEAN,
	mailStatus  VARCHAR(100),
	description TEXT,
	repository TEXT,
	systemTemplate BOOLEAN,
	billingDate TIMESTAMP,
	invoiceDate TIMESTAMP,
	invoiceDate2 TIMESTAMP,
	dueDate TIMESTAMP,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE bill_invoice_seq START WITH 1 INCREMENT BY 1;



---------------------------------BILLING_RUN_OUTCOME-------------------------------------------

CREATE TABLE BCP_BILLING_RUN_OUTCOME (
    id BIGINT not null,
	runNumber  VARCHAR(100),
	status VARCHAR(100),
	mode VARCHAR(100),
	username VARCHAR(100),
	invoiceCount BIGINT ,
	invoiceAmount   NUMERIC(19, 5),
	creditNoteCount BIGINT,
	creditNoteAmount NUMERIC(19, 5),
	periodFrom TIMESTAMP,
	periodTo TIMESTAMP,
	name VARCHAR(100),
	visibleInShortcut BOOLEAN DEFAULT TRUE,	
	bgroup BIGINT,	
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
	
);
CREATE SEQUENCE bill_run_outcome_seq START WITH 1 INCREMENT BY 1;


---------------------------------BCP_BILLING_MODEL_LOG-------------------------------------------

CREATE TABLE BCP_BILLING_MODEL_LOG (
    id BIGINT not null,
    billing BIGINT ,
	billingName  VARCHAR(100),
	billingTypeId BIGINT ,
	billingTypeName VARCHAR(100),
	name VARCHAR(100),
	visibleInShortcut BOOLEAN DEFAULT TRUE,	
	bgroup BIGINT,	
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	endDate TIMESTAMP,
	status VARCHAR(100),
	mode VARCHAR(100),
	username VARCHAR(100),
    eventCount BIGINT,
    periodCount BIGINT,	
    clientCount BIGINT,
    groupCount BIGINT,
	categoryCount BIGINT,
	invoiceCount BIGINT,
	PRIMARY KEY (id)
	
);
CREATE SEQUENCE bill_model_log_seq START WITH 1 INCREMENT BY 1;

---------------------------------BCP_BILLING_MODEL-------------------------------------------

CREATE TABLE BCP_BILLING_MODEL (
	id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
	active BOOLEAN,
	scheduled BOOLEAN,
	cronExpression VARCHAR(100),
	currency VARCHAR(100),
	filter BIGINT,
	selectPeriodAtRuntime BOOLEAN,
	useUnitCostToComputeAmount BOOLEAN,
	includeUnitCost BOOLEAN,
	useVat BOOLEAN,
	orderItems BOOLEAN,
	orderItemsAsc BOOLEAN,
	separateInvoicePerPeriod BOOLEAN,
	includeZeroAmountEvents BOOLEAN,
	buildCommunicationMessage BOOLEAN,
	
	dueDateCalculation BOOLEAN,
	dueDateOperator VARCHAR (25),
	dueDateValue Date,
	dueDateSign VARCHAR(100),
	dueDateNumber BIGINT,
	dueDateGranularity VARCHAR (25),
	
	invoiceDescription TEXT,
	
	invoiceDateCalculation BOOLEAN,
	invoiceDateOperator VARCHAR (25),
	invoiceDateValue Date,
	invoiceDateSign VARCHAR(100),
	invoiceDateNumber BIGINT,
	invoiceDateGranularity VARCHAR (25),
	
	periodSide VARCHAR(100),
	
	fromDateOperator VARCHAR (25),
	fromDateValue Date,
	fromDateSign VARCHAR(100),
	fromDateNumber BIGINT,
	fromDateGranularity VARCHAR (25),
	
	toDateOperator VARCHAR (25),
	toDateValue Date,
	toDateSign VARCHAR(100),
	toDateNumber BIGINT,
	toDateGranularity VARCHAR (25),
	
	addAppendicies BOOLEAN,
	appendicyType VARCHAR(100),
	billTemplateCode VARCHAR(100),
	billingCompanyCode VARCHAR(100),
	periodGranularity VARCHAR(100),
	invoiceGranularityLevel VARCHAR(100),
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE billing_model_seq START WITH 1 INCREMENT BY 1;

---------------------------------BCP_BILLING_MODEL_ITEM-------------------------------------------

CREATE TABLE BCP_BILLING_MODEL_ITEM (
	id BIGINT not null,
	name VARCHAR(100),
	billing BIGINT,
	position BIGINT,
	itemType VARCHAR(100),
	PRIMARY KEY (id)
);
CREATE SEQUENCE billing_model_item_seq START WITH 1 INCREMENT BY 1;

---------------------------------BCP_BILLING_MODEL_PIVOT-------------------------------------------

CREATE TABLE BCP_BILLING_MODEL_PIVOT (
	id BIGINT not null,
	name VARCHAR(100),
	billing BIGINT,
	position BIGINT,
	attributeId BIGINT,
	show BOOLEAN,
	PRIMARY KEY (id)
);
CREATE SEQUENCE billing_model_pivot_seq START WITH 1 INCREMENT BY 1;

---------------------------------BCP_BILLING_MODEL_GROUPING_ITEM-------------------------------------------

CREATE TABLE BCP_BILLING_MODEL_GROUPING_ITEM (
	id BIGINT not null,
	name VARCHAR(100),
	billing BIGINT,
	position BIGINT,
	attributeId BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE billing_model_grouping_item_seq START WITH 1 INCREMENT BY 1;

---------------------------------BCP_BILLING_MODEL_PARAMETER-------------------------------------------

CREATE TABLE BCP_BILLING_MODEL_PARAMETER (
	id BIGINT not null,
	name VARCHAR(100),
	billing BIGINT,
	position BIGINT,
	dimensionType VARCHAR(100),
	dimensionId BIGINT,
	dimensionName VARCHAR(100),
	functions VARCHAR(100),
	addBillingFilters BOOLEAN,
	PRIMARY KEY (id)
);
CREATE SEQUENCE billing_model_parameter_seq START WITH 1 INCREMENT BY 1;

---------------------------------BCP_BILLING_MODEL_DRIVER_GROUP-------------------------------------------

CREATE TABLE BCP_BILLING_MODEL_DRIVER_GROUP (
	id BIGINT not null,
	billing BIGINT,
	groupId BIGINT,
	groupName VARCHAR(100),
	position BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE billing_model_driver_group_seq START WITH 1 INCREMENT BY 1;

---------------------------------BCP_BILLING_MODEL_DRIVER_GROUP_ITEM-------------------------------------------

CREATE TABLE BCP_BILLING_MODEL_DRIVER_GROUP_ITEM (
	id BIGINT not null,
	bGroup BIGINT,
	value VARCHAR(100),
	excludeDriver BOOLEAN,
	excludeUnitCost BOOLEAN,
	position BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE billing_model_driver_group_item_seq START WITH 1 INCREMENT BY 1;

---------------------------------BCP_BILLING_MODEL_ENRICHMENT_ITEM-------------------------------------------

CREATE TABLE BCP_BILLING_MODEL_ENRICHMENT_ITEM (
	id BIGINT not null,
	billing BIGINT,
	position BIGINT,
	decimalValue NUMERIC(19, 5),
	stringValue VARCHAR(100),
	dateOperator VARCHAR (25),
	dateValue Date,
	dateSign VARCHAR(100),
	dateNumber BIGINT,
	dateGranularity VARCHAR (25),
	sourceType VARCHAR(100),
	sourceId BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE billing_model_enrichment_item_seq START WITH 1 INCREMENT BY 1;
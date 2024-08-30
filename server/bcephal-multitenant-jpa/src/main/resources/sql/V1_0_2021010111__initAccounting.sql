CREATE TABLE BCP_ACCOUNTING_BOOKING (
    id BIGINT not null,
	bookingId VARCHAR(100),
	runNumber VARCHAR(100),
	entryDate TIMESTAMP,
	bookingDate1 TIMESTAMP,
	bookingDate2 TIMESTAMP,
	creditAmount DECIMAL (31, 14),
	debitAmount DECIMAL (31, 14),
	username VARCHAR(100),
	manual BOOLEAN DEFAULT FALSE,
	PRIMARY KEY (id)
);

CREATE SEQUENCE booking_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_ACCOUNTING_BOOKING_DETAIL (
    id BIGINT not null,
	bookingId BIGINT,
	entryId BIGINT,
	position INTEGER,	
    accountId VARCHAR(100),
	accountName VARCHAR(255),
	date_ TIMESTAMP,
	amount DECIMAL (31, 14),
	sign_ VARCHAR(100),
	PRIMARY KEY (id)
);

CREATE SEQUENCE posting_ent_seq START WITH 1 INCREMENT BY 1;



CREATE TABLE BCP_ACCOUNTING_BOOKING_ITEM (
    id BIGINT not null,
	booking BIGINT,
	accountId VARCHAR(100),	
	accountName VARCHAR(255),
	position INTEGER,	
	amount DECIMAL (31, 14),
	creditAmount DECIMAL (31, 14),	
	debitAmount DECIMAL (31, 14),
	pivot VARCHAR(100),	
	bookingDate1 TIMESTAMP,	
	bookingDate2 TIMESTAMP,	
	PRIMARY KEY (id)
);

CREATE SEQUENCE booking_item_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_ACCOUNTING_BOOKING_MODEL (
    id BIGINT not null,
    name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,
	active BOOLEAN DEFAULT TRUE,
	schedulerOption VARCHAR(100),
	cronExpression VARCHAR(100),
	periodSide VARCHAR(100),
	periodFrom TIMESTAMP,
	periodTo TIMESTAMP,
	periodGranularity VARCHAR(100),
	includeZeroAmountEntries BOOLEAN DEFAULT FALSE,
	selectPeriodAtRuntime BOOLEAN DEFAULT FALSE,
	filter BIGINT,
	fromDynamicPeriod VARCHAR(100),
	fromOperationNumber BIGINT,
	fromOperationGranularity VARCHAR(100),
	fromOperation VARCHAR(100),
	toDynamicPeriod VARCHAR(100),
	toOperationNumber BIGINT,
	toOperationGranularity VARCHAR(100),
	toOperation VARCHAR(100),
	minDeltaAmount DECIMAL (31, 14),
	maxDeltaAmount DECIMAL (31, 14),	
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);

CREATE SEQUENCE booking_model_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_ACCOUNTING_BOOKING_MODEL_LOG (
    id BIGINT not null,
	modelId BIGINT,
	modelName VARCHAR(100),	
	endDate TIMESTAMP,
	status VARCHAR(100),	
	mode VARCHAR(100),
	username VARCHAR(100),	
	accountCount BIGINT,
	postingEntryCount BIGINT,	
	periodCount BIGINT,	
	bookingItemCount BIGINT,	
	PRIMARY KEY (id)
);

CREATE SEQUENCE booking_model_log_seq START WITH 1 INCREMENT BY 1;




CREATE TABLE BCP_ACCOUNTING_BOOKING_MODEL_LOG_ITEM (
    id BIGINT not null,
	logId BIGINT,
	name VARCHAR(100),	
	message VARCHAR(200),
	creditAmount DECIMAL (31, 14),
	debitAmount DECIMAL (31, 14),
	balanceAmount DECIMAL (31, 14),
	status VARCHAR(100),	
	accountCount BIGINT,
	postingEntryCount BIGINT,	
	PRIMARY KEY (id)
);

CREATE SEQUENCE booking_model_item_seq START WITH 1 INCREMENT BY 1;



CREATE TABLE BCP_ACCOUNTING_BOOKING_PIVOT (
    id BIGINT not null,
	name VARCHAR(100),	
	position INTEGER,
	attributeId BIGINT,
	show BOOLEAN,
	booking BIGINT,	
	PRIMARY KEY (id)
);

CREATE SEQUENCE booking_model_pivot_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_SEC_PONTO (
    id BIGINT not null,
	name VARCHAR(100),
	clientId BIGINT,
	projectId BIGINT,
	projectCode VARCHAR(100),
	oauth2 BIGINT,
	clientSignature BIGINT,
	clientTlsCa BIGINT,
	clientTls BIGINT,	
	type VARCHAR(100),
	clientApplicationId VARCHAR(512),
	accountId TEXT,
	organisationId VARCHAR(512),
	organisationSecret VARCHAR(512),
	apiEndpoint TEXT,
	outPath TEXT,
	authorizationUrl TEXT,	
	accessToken TEXT,
	refreshToken TEXT,
	tokenType VARCHAR(100),	
	expiresIn INTEGER,
	scope VARCHAR(100),	
	active boolean,
	scheduled boolean,
	cronExpression VARCHAR(100),
	pageSize INTEGER,
	startDate TIMESTAMP,
	creationdate TIMESTAMP,
	modificationdate  TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS ponto_entity_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_SEC_ENTITY_COLUMNS (
    id BIGINT not null,
	name VARCHAR(100),	
	type VARCHAR(100),	
	ConnectEntity BIGINT,
	position INTEGER,
	nbrOfDecimal INTEGER,
	usedSeparator boolean,
	defaultFormat VARCHAR(100),
	dateOperator VARCHAR(100),
	dateValue TIMESTAMP,
	dateSign VARCHAR(10),
	dateNumber INTEGER,
	dateGranularity VARCHAR(50),
	defaultValue boolean,
	stringValue TEXT,
	decimalValue  DECIMAL (31, 14),
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS entity_columns__seq START WITH 1 INCREMENT BY 1;




CREATE TABLE IF NOT EXISTS BCP_SEC_OAUTH2_PARAMS (
    id BIGINT not null,
	name VARCHAR(100),	
	clientId VARCHAR(512),
	clientSecret VARCHAR(512),
	redirectUrlBase TEXT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS oauth2_entity_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_SEC_SSL (
    id BIGINT not null,
	name VARCHAR(100),	
	certificateId VARCHAR(512),
	entitySecret VARCHAR(512),
	certificatePath TEXT,
	privateKeyPassphrase TEXT,
	privateKeyPath TEXT,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS ssl_entity_seq START WITH 1 INCREMENT BY 1;





CREATE TABLE IF NOT EXISTS BCP_SCHEDULER_ENTITY_LOG (
    id BIGINT not null,
	objectName TEXT,	
	clientId BIGINT,
	objectId BIGINT,
	status VARCHAR(100),
	message TEXT,
	projectCode VARCHAR(100),
	details TEXT,	
	creationDate TIMESTAMP,
	endDate  TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS scheduler_entity_log_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS BCP_SEC_PONTO_SYNCHRONIZED (
    id BIGINT not null,
	ConnectEntityId BIGINT,
	resourceId VARCHAR(256),
	synchronizationId VARCHAR(256),
	createdAt TIMESTAMP,
	updateAt  TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS ponto_entity_sync_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE BCP_ARCHIVE (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,
	configurationId BIGINT,
	code VARCHAR(100),
	userName VARCHAR(100),
	description TEXT,
	tableName TEXT,
	lineCount INTEGER,
	status VARCHAR(100),
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);

CREATE SEQUENCE archive_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_ARCHIVE_CONFIGURATION (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
    archiveName VARCHAR(100),
	description TEXT,
	backupGrid BIGINT,
	replacementGrid BIGINT,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);

CREATE SEQUENCE archive_conf_seq START WITH 1 INCREMENT BY 1;



CREATE TABLE BCP_ARCHIVE_CONFIGURATION_ENRICHMENT_ITEM (
    id BIGINT not null,
	configurationId BIGINT,
	decimalValue DECIMAL (31, 14),
	stringValue VARCHAR(100),
	dateValue TIMESTAMP,	
	dateDynamicPeriod VARCHAR(100),	
	dateOperation VARCHAR(100),	
	dateOperationNumber INTEGER,
	dateOperationGranularity VARCHAR(100),	
	position INTEGER,
	type VARCHAR(100),
	sourceId BIGINT,
	PRIMARY KEY (id)
);

CREATE SEQUENCE archive_config_enrich_item_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_ARCHIVE_LOG (
    id BIGINT not null,
    archiveId BIGINT,
	name VARCHAR(100) not null,
	userName VARCHAR(100),
	message TEXT,
	lineCount INTEGER,
	action VARCHAR(100),
	status VARCHAR(100),
	PRIMARY KEY (id)
);

CREATE SEQUENCE archive_log_seq START WITH 1 INCREMENT BY 1;

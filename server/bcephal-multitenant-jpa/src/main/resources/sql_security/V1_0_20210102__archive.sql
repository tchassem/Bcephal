CREATE TABLE IF NOT EXISTS BCP_SEC_SIM_ARCHIVE (
    id BIGINT not null,
	name VARCHAR(100) not null,
	repository VARCHAR(100) not null,
	description VARCHAR(255),
	projectCode TEXT,
	userName VARCHAR(100) not null,
	archiveMaxCount INTEGER not null,
	fileName TEXT,
	projectId BIGINT not null,
    clientId BIGINT not null,
	creationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS sec_sim_archive_seq START WITH 1 INCREMENT BY 1;
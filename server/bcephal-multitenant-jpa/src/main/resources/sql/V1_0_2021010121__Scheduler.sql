CREATE TABLE IF NOT EXISTS BCP_SCHEDULER_LOG (
    id BIGINT not null,
	objectId BIGINT,
	objectName TEXT,
	objectType VARCHAR(100),
	status VARCHAR(25),
	message TEXT,
	details TEXT,
	projectCode VARCHAR(255),
	creationDate TIMESTAMP,	
	endDate TIMESTAMP,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS scheduler_log_seq START WITH 1 INCREMENT BY 1;
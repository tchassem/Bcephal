DROP TABLE IF EXISTS BCP_SEC_USER_SESSION_LOG; 
CREATE TABLE IF NOT EXISTS BCP_SEC_USER_SESSION_LOG (
    id BIGINT not null,
	userId BIGINT,
	username VARCHAR(100) not null,
	name TEXT,
	clientId BIGINT,
	clientName VARCHAR(255),
	projectId BIGINT,
	projectCode VARCHAR(255),
	projectName VARCHAR(255),	
	usersession VARCHAR(255),
	status VARCHAR(50),	
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	lastoperationDate TIMESTAMP,
	endDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS sec_usser_session_log_seq START WITH 1 INCREMENT BY 1;
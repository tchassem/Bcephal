CREATE TABLE IF NOT EXISTS BCP_TASK(
	id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
	category VARCHAR(25),
	status VARCHAR(25),
	nature VARCHAR(25),
	serieNbr INT DEFAULT 0,
	userId BIGINT,	
	sendNotice BOOLEAN,
	linkedFunctionality TEXT,
	linkedObjectId BIGINT,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	deadline TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS task_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_TASK_AUDIENCE(
	id BIGINT not null,
	taskId BIGINT,
	type VARCHAR(25),
	objectId BIGINT,
	position INT DEFAULT 0,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS task_audience_seq START WITH 1 INCREMENT BY 1;



CREATE TABLE IF NOT EXISTS BCP_DOCUMENT(
	id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
	code TEXT,
	extension VARCHAR(25),
	category TEXT,
	subjectType TEXT,
	subjectName TEXT,
	subjectId BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS document_seq START WITH 1 INCREMENT BY 1;

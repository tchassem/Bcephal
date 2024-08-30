CREATE TABLE IF NOT EXISTS BCP_TASK_LOG(
	id BIGINT not null,
	name VARCHAR(100) not null,
	description TEXT,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
	taskId BIGINT,	
	type VARCHAR(50),
	username VARCHAR(255),
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS task_log_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_TASK_LOG_ITEM(
	id BIGINT not null,
	name VARCHAR(100) not null,
	description TEXT,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
	taskId BIGINT,	
	logId BIGINT,	
	type VARCHAR(50),
	username VARCHAR(255),
	fileName TEXT,
	oldValue TEXT,
	newValue TEXT,	
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS task_log_item_seq START WITH 1 INCREMENT BY 1;
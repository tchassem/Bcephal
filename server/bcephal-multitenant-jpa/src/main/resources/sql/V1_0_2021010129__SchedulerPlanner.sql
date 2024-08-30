CREATE TABLE IF NOT EXISTS BCP_SCHEDULER_PLANNER (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,
	cronExpression VARCHAR(255),
	scheduled BOOLEAN DEFAULT FALSE,
	active BOOLEAN DEFAULT FALSE,	
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS scheduler_planner_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_SCHEDULER_PLANNER_ITEM (
    id BIGINT not null,
	schedulerId BIGINT not null,	
	position INTEGER,	
	type VARCHAR(25),	
	objectId BIGINT,	
	objectName VARCHAR(255),
	comparator VARCHAR(25),	
	decimalValue DECIMAL(31, 14),		
	temporisationValue INTEGER,		
	temporisationUnit VARCHAR(25),	
	decision VARCHAR(25),	
	alarmId BIGINT,	
	active BOOLEAN DEFAULT TRUE,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS scheduler_planner_item_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS BCP_SCHEDULER_PLANNER_LOG (
    id BIGINT not null,	
	schedulerId BIGINT not null,	
	schedulerName VARCHAR(255),		
	message TEXT,	
	status VARCHAR(25),	
	mode VARCHAR(25),
	username VARCHAR(255),		
	setps INTEGER,		
	runnedSetps INTEGER,		
	currentItemId BIGINT,	
	projectCode VARCHAR(255),		
	startDate TIMESTAMP,
	endDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS scheduler_planner_log_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS BCP_SCHEDULER_PLANNER_LOG_ITEM (
    id BIGINT not null,	
	logId BIGINT not null,	
	schedulerItemId BIGINT not null,	
	position INTEGER,	
	type VARCHAR(25),
	objectId BIGINT,	
	objectName VARCHAR(255),		
	message TEXT,	
	status VARCHAR(25),		
	schedulerName VARCHAR(255),	
	startDate TIMESTAMP,
	endDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS scheduler_planner_log_item_seq START WITH 1 INCREMENT BY 1;
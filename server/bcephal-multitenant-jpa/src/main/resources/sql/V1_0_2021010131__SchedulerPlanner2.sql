DROP TABLE IF EXISTS BCP_SCHEDULER_PLANNER_ITEM ;

CREATE TABLE IF NOT EXISTS BCP_SCHEDULER_PLANNER_ITEM (
    id BIGINT not null,
	schedulerId BIGINT not null,	
	position INTEGER,
	code VARCHAR(100),	
	type VARCHAR(25),	
	objectId BIGINT,	
	objectName VARCHAR(255),
	comparator VARCHAR(25),	
	decimalValue DECIMAL(31, 14),		
	temporisationValue INTEGER,		
	temporisationUnit VARCHAR(25),	
	decision1 VARCHAR(25),	
	alarmId1 BIGINT,
	gotoCode1 VARCHAR(100),	
	decision2 VARCHAR(25),	
	alarmId2 BIGINT,
	gotoCode2 VARCHAR(100),		
	active BOOLEAN DEFAULT TRUE,	
	PRIMARY KEY (id)
);
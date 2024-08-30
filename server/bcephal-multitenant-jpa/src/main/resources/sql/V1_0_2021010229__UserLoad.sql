CREATE TABLE IF NOT EXISTS BCP_USER_LOADER (
	id BIGINT not null,
	name VARCHAR (100) not null,	
	description TEXT,	
	treatment VARCHAR (20),	
	repository TEXT,
	fileLoaderId BIGINT,
	menu BIGINT,	
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,	
	PRIMARY KEY (id)		
);
CREATE SEQUENCE IF NOT EXISTS user_loader_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE if not exists BCP_USER_LOADER_MENU (
    id BIGINT not null,
	loaderId BIGINT,
	name VARCHAR(256) not null,
	active BOOLEAN,	
	parent VARCHAR(256),
	icon  TEXT,	
	position INTEGER,	
	hasNewMenu BOOLEAN,
	newMenuName VARCHAR(256),	
	hasListMenu BOOLEAN,
	listMenuName VARCHAR(256),	
	parentIcon TEXT,	
	newMenuIcon TEXT,
	listMenuIcon TEXT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE if not exists user_loader_menu_seq START WITH 1 INCREMENT BY 1;



CREATE TABLE if not exists BCP_USER_LOADER_SCHEDULER (
    id BIGINT not null,
	loaderId BIGINT,
	schedulerId BIGINT,
	active BOOLEAN,	
	position INTEGER,
	type VARCHAR(25),	
	PRIMARY KEY (id)
);
CREATE SEQUENCE if not exists user_loader_scheduler_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE if not exists BCP_USER_LOADER_CONDITION (
    id BIGINT not null,
	loaderId BIGINT,	
	active BOOLEAN,	
	position INTEGER,	
	type VARCHAR(25),
	verb VARCHAR(10),
    openingBracket VARCHAR(5),
    closingBracket VARCHAR(5),
    comparator VARCHAR(25),    
    errorMessage TEXT,
    longValue BIGINT,
    stringValue TEXT,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE if not exists user_loader_condition_seq START WITH 1 INCREMENT BY 1;




CREATE TABLE IF NOT EXISTS BCP_USER_LOAD (
	id BIGINT not null,
	name VARCHAR (100) not null,	
	description TEXT,
	username TEXT,
	loaderId BIGINT,
	treatment VARCHAR (20),	
	mode VARCHAR (10),
	status VARCHAR (20),
	fileCount INTEGER,
	emptyFileCount INTEGER,
	errorFileCount INTEGER,
	loadedFileCount INTEGER,
	error BOOLEAN,
	message TEXT,
	startDate TIMESTAMP,
	endDate TIMESTAMP,	
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,	
	PRIMARY KEY (id)		
);
CREATE SEQUENCE IF NOT EXISTS user_load_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_USER_LOAD_LOG (
	id BIGINT not null,
	file TEXT,
	loadId BIGINT,
	message TEXT,
	lineCount INTEGER,
	empty BOOLEAN,
	error BOOLEAN,
	loaded BOOLEAN,
	PRIMARY KEY (id)		
);
CREATE SEQUENCE IF NOT EXISTS user_load_log_seq START WITH 1 INCREMENT BY 1;
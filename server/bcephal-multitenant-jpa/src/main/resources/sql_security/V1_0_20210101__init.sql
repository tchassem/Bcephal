CREATE TABLE BCP_SEC_SUBSCRIPTION (
    id BIGINT not null,
	name VARCHAR(100) not null,	
	maxuser INTEGER,
	owneruser VARCHAR(50),
	status VARCHAR(20),	
	defaultsubscription BOOLEAN DEFAULT FALSE,
	PRIMARY KEY (id)
);
CREATE SEQUENCE sec_subscription_seq START WITH 2 INCREMENT BY 1;


CREATE TABLE BCP_SEC_PROJECT (
    id BIGINT not null,
    subscriptionid BIGINT not null,
	client VARCHAR(100),
	code TEXT not null,
	name VARCHAR(100) not null,
	description VARCHAR(255),
	defaultProject boolean DEFAULT false,	
	path VARCHAR(255),
	dbname VARCHAR(100),
	username VARCHAR(50),
	creationdate TIMESTAMP,
	modificationdate  TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE sec_project_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_SEC_PROJECT_BLOCK (
    id BIGINT not null,
    subcriptionid BIGINT not null,
    projectid BIGINT not null,
	code TEXT not null,
	name VARCHAR(100) not null,
	username VARCHAR(50) not null,
	position INTEGER DEFAULT 0,
	background INTEGER,
	foreground INTEGER,
	flowBreak BOOLEAN,
	PRIMARY KEY (id)
);
CREATE SEQUENCE sec_project_block_seq START WITH 1 INCREMENT BY 1;

INSERT INTO BCP_SEC_SUBSCRIPTION (id,name,maxuser,owneruser,status,defaultsubscription) VALUES (1, 'Default', 100, null, 'ACTIVE', true);


CREATE TABLE BCP_SEC_FUNCTIONALITY_BLOCK_GROUP (
    id BIGINT not null,
    projectid BIGINT not null,
	name VARCHAR(256) not null,
	username VARCHAR(50) not null,
	position INTEGER DEFAULT 0,
	background INTEGER,
	foreground INTEGER,
	defaultGroup BOOLEAN,
	PRIMARY KEY (id)
);
CREATE SEQUENCE sec_functionality_block_group_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_SEC_FUNCTIONALITY_BLOCK (
    id BIGINT not null,
    projectid BIGINT not null,
    groupId BIGINT not null,
	code VARCHAR(256) not null,
	name VARCHAR(256) not null,
	username VARCHAR(50) not null,
	position INTEGER DEFAULT 0,
	background INTEGER,
	foreground INTEGER,
	flowBreak BOOLEAN,
	PRIMARY KEY (id)
);
CREATE SEQUENCE sec_functionality_block_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_SEC_CLIENT (
    id BIGINT not null,
	name VARCHAR(100) not null,	
	clientId VARCHAR(255),
	code VARCHAR(100),
	description TEXT,
	enabled BOOLEAN DEFAULT FALSE,
	secret TEXT,
	defaultLanguage VARCHAR(100),
	defaultClient BOOLEAN DEFAULT FALSE,
	nature VARCHAR(100),
    type_	 VARCHAR(100),
	status VARCHAR(50),	
	maxUser INTEGER,	
	email VARCHAR(100),
	phone VARCHAR(100),	
	street VARCHAR(100),
	postalCode VARCHAR(100),
	city VARCHAR(100),
	country VARCHAR(100),
	creationdate TIMESTAMP,
	modificationdate  TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE client_seq START WITH 2 INCREMENT BY 1;


CREATE TABLE BCP_SEC_USER (
    id BIGINT not null,
    userId VARCHAR(255),
	client VARCHAR(100),
	clientId BIGINT,
	type VARCHAR(25),
	username VARCHAR(100) not null,
	name VARCHAR(100) not null,
	enabled boolean DEFAULT false,	
	emailVerified boolean DEFAULT false,	
	firstName VARCHAR(100),
	lastName VARCHAR(100),
	email VARCHAR(100),
	defaultLanguage VARCHAR(50),
	creationdate TIMESTAMP,
	modificationdate  TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_SEC_PROFIL (
    id BIGINT not null,
	code VARCHAR(100),
	name VARCHAR(100) not null,
	client VARCHAR(100),
	clientId BIGINT,
	type_ VARCHAR(100),
	description TEXT,
	creationdate TIMESTAMP,
	modificationdate  TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE profil_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE BCP_SEC_RIGHT (
    id BIGINT not null,
	functionality TEXT,	
	level VARCHAR(25),	
	userId BIGINT,
	profileId BIGINT,
    position INTEGER,
    actionsAsString TEXT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE right_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE BCP_SEC_CLIENT_FUNCTIONALITY (
    id BIGINT not null,
	client VARCHAR(100),	
	clientId BIGINT,
	code TEXT,
	active BOOLEAN,
    position INTEGER,
    actionsAsString TEXT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE client_functionality_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE BCP_SEC_PROFIL_USER (
    id BIGINT not null,
	profileId BIGINT,
	userId BIGINT,
	clientId BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE profile_user_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE BCP_SEC_PROFIL_PROJECT (
    id BIGINT not null,
	profileId BIGINT,
	projectId BIGINT,
	projectCode TEXT,
	clientId BIGINT,
	position INTEGER,
	PRIMARY KEY (id)
);
CREATE SEQUENCE profile_project_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE BCP_ALARM (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,
	active BOOLEAN,
	scheduled BOOLEAN,
	cronExpression VARCHAR(100),
	sendEmail BOOLEAN,
    emailTitle TEXT,
	email TEXT,
	sendSms BOOLEAN,
	sms TEXT,
	sendChat BOOLEAN,
	chat TEXT,
	condition BIGINT,	
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE alarm_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_ALARM_AUDIENCE (
    id BIGINT not null,
    alarm BIGINT,
    position INTEGER,
    active BOOLEAN,
    audienceType VARCHAR(25),    
    userOrProfilId BIGINT,	
	name VARCHAR(100),
	email VARCHAR(100),
	phone VARCHAR(100),
    sendEmail BOOLEAN,
    sendSms BOOLEAN,
    sendChat BOOLEAN,
	PRIMARY KEY (id)
);
CREATE SEQUENCE alarm_aud_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_ALARM_ATTACHMENT (
    id BIGINT not null,
    alarm BIGINT,
    name TEXT,
    position INTEGER,
    templateId BIGINT,
    attachmentType VARCHAR(25), 
	PRIMARY KEY (id)
);
CREATE SEQUENCE alarm_atch_seq START WITH 1 INCREMENT BY 1;
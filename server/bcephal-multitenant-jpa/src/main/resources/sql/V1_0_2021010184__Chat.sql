ALTER TABLE BCP_DASHBOARD_ITEM ADD COLUMN IF NOT EXISTS groupIdsImpl TEXT;

CREATE TABLE IF NOT EXISTS BCP_CHAT(
	id BIGINT not null,
	name VARCHAR(100) not null,
	description TEXT,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
	owner TEXT,
	status VARCHAR(50),
	subjectType TEXT,
	subjectName TEXT,
	subjectId BIGINT,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS chat_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS BCP_CHAT_ITEM(
	id BIGINT not null,
	chatId BIGINT,
	type VARCHAR(50),
	document BIGINT,
	message TEXT,
	position INTEGER,
	senderId BIGINT,
	senderName TEXT,
	receiverIdsImpl TEXT,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS chat_item_seq START WITH 1 INCREMENT BY 1;



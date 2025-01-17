ALTER TABLE BCP_TASK ADD COLUMN IF NOT EXISTS sequenceId BIGINT;
ALTER TABLE BCP_TASK ADD COLUMN IF NOT EXISTS code VARCHAR(100);
ALTER TABLE BCP_TASK ADD COLUMN IF NOT EXISTS templateId VARCHAR(100);

CREATE TABLE IF NOT EXISTS BCP_DASHBOARD_ITEM_FILTER(
	id BIGINT not null,
	itemId BIGINT,	
	owner TEXT,
	type VARCHAR(50),
	operator VARCHAR(50),
	value TEXT,
	position INTEGER,
	filterVerb VARCHAR(50),
	openBrackets VARCHAR(10),
	closeBrackets VARCHAR(10),
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS dashboard_item_filter_seq START WITH 1 INCREMENT BY 1;

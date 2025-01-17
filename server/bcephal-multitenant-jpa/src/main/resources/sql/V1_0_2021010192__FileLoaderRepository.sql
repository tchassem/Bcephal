ALTER TABLE BCP_FILE_LOADER ADD COLUMN IF NOT EXISTS source VARCHAR(50);
UPDATE BCP_FILE_LOADER SET source = 'SERVER' WHERE source IS NULL;

CREATE TABLE IF NOT EXISTS BCP_FILE_LOADER_REPOSITORY(
	id BIGINT not null,
	loaderId BIGINT,
	position INTEGER not null,
	repository TEXT,
	repositoryOnServer TEXT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS file_loader_repository_seq START WITH 1 INCREMENT BY 1;


ALTER TABLE BCP_SCHEDULER_PLANNER_ITEM ADD COLUMN IF NOT EXISTS stopIfError BOOLEAN;
UPDATE BCP_SCHEDULER_PLANNER_ITEM SET stopIfError = false WHERE stopIfError IS NULL;

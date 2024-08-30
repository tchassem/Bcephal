
CREATE TABLE IF NOT EXISTS BCP_DASHBOARD_ITEM_USER_LOADER(
	id BIGINT not null,	
	itemId BIGINT,		
	description TEXT,	
	position integer,
	userLoaderId BIGINT,	
	icon TEXT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS dashboard_item_user_loader_seq START WITH 1 INCREMENT BY 1;

ALTER TABLE BCP_DASHBOARD_ITEM ADD COLUMN IF NOT EXISTS userloaderid BIGINT;

INSERT INTO BCP_DASHBOARD_ITEM_USER_LOADER (id, userLoaderId, itemId) 
	SELECT nextval('dashboard_item_user_loader_seq'), userLoaderId,id 
	FROM BCP_DASHBOARD_ITEM WHERE 
	 exists (SELECT column_name FROM information_schema.columns WHERE table_name='bcp_dashboard_item' and column_name='userloaderid')
	 AND userLoaderId in 
	(SELECT userLoaderId FROM BCP_DASHBOARD_ITEM WHERE userLoaderId is not null);

ALTER TABLE BCP_DASHBOARD_ITEM DROP COLUMN IF EXISTS userLoaderId;

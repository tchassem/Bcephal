-- ---------------------------- PROJECT -----------------------------------
-- ------------------------------------------------------------------------
-- ---------------------------- PROJECT -----------------------------------
-- ------------------------------------------------------------------------

CREATE TABLE bcp_project (
    id BIGINT not null,
	code VARCHAR(50) not null,
	name VARCHAR(100) not null,
	description VARCHAR(255),
	creation_date TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE project_seq START WITH 1 INCREMENT BY 1;
	
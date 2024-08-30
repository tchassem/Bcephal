CREATE TABLE if not exists BCP_USER_LOADER_CONTROLLER (
    id BIGINT not null,
	loaderId BIGINT,
	active BOOLEAN,
	checked BOOLEAN,
	position INTEGER,
	type VARCHAR(25),
	verb VARCHAR(10),
    comparator VARCHAR(25),
    errorMessage TEXT,
    longValue BIGINT,
    stringValue TEXT,
	decimalValue NUMERIC(19, 5),
	PRIMARY KEY (id)
);
CREATE SEQUENCE if not exists user_loader_controller_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS BCP_PRODUCT (
	id BIGINT not null,
	code TEXT not null,
	name TEXT not null,
	description TEXT, 
	version TEXT, 
	public_key TEXT,
	private_key TEXT, 
	creation_date TIMESTAMP,
	modification_date TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS product_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS BCP_PRODUCT_PARAMETER (
	id BIGINT not null,
	product BIGINT,
	code TEXT not null,
	description TEXT, 	
	mandatory BOOLEAN DEFAULT FALSE,
	type VARCHAR(25), 	
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS product_parameter_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS BCP_PRODUCT_FUNCTIONALITY (
	id BIGINT not null,
	product BIGINT,
	code TEXT not null,
	description TEXT, 	
	active BOOLEAN DEFAULT TRUE,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS product_functionality_seq START WITH 1 INCREMENT BY 1;






CREATE TABLE IF NOT EXISTS BCP_LICENSE (
	id BIGINT not null,
	product BIGINT,
	code TEXT not null,
	name TEXT not null,
	description TEXT, 
	public_key TEXT,
	private_key TEXT, 
	validity_type VARCHAR(25), 
	days INTEGER DEFAULT 0,
	creation_date TIMESTAMP,
	modification_date TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS license_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS BCP_LICENSE_PARAMETER (
	id BIGINT not null,
	license BIGINT,
	code TEXT not null,
	description TEXT, 	
	mandatory BOOLEAN DEFAULT FALSE,
	type VARCHAR(25), 
	value TEXT,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS license_parameter_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS BCP_LICENSE_FUNCTIONALITY (
	id BIGINT not null,
	license BIGINT,
	code TEXT not null,
	description TEXT, 	
	active BOOLEAN DEFAULT TRUE,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS license_functionality_seq START WITH 1 INCREMENT BY 1;
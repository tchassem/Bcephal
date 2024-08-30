ALTER TABLE BCP_BILLING_MODEL ADD COLUMN IF NOT EXISTS refreshRepositoriesBeforeRun BOOLEAN;
ALTER TABLE BCP_BILLING_MODEL ADD COLUMN IF NOT EXISTS refreshRepositoriesAfterRun BOOLEAN;
UPDATE BCP_BILLING_MODEL SET refreshRepositoriesBeforeRun = true, refreshRepositoriesAfterRun = true;

CREATE TABLE IF NOT EXISTS BCP_BILLING_MODEL_LABEL (
    id BIGINT not null,
	billing BIGINT not null,
	filter BIGINT,
	code VARCHAR(255),	
    position INTEGER,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS billing_model_label_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_BILLING_MODEL_LABEL_VALUE (
    id BIGINT not null,
	label BIGINT not null,	
    locale VARCHAR(25),	
    value TEXT,	
    position INTEGER,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS billing_model_label_value_seq START WITH 1 INCREMENT BY 1;

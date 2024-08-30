
CREATE TABLE BCP_BILLING_INVOICE_LOG (
    id BIGINT not null,
	invoice BIGINT not null,
	username VARCHAR(255),	
	status VARCHAR(100),
	file TEXT ,	
	version INTEGER,
	amountWithoutVatBefore DECIMAL (31, 14),	
	vatAmountBefore DECIMAL (31, 14),	
	totalAmountBefore DECIMAL (31, 14),
	amountWithoutVatAfter DECIMAL (31, 14),	
	vatAmountAfter DECIMAL (31, 14),	
	totalAmountAfter DECIMAL (31, 14),
	date TIMESTAMP,
	oldValue TEXT,
	newValue TEXT,
	mode VARCHAR(10),
	PRIMARY KEY (id)
);
CREATE SEQUENCE billing_invoice_log_seq START WITH 1 INCREMENT BY 1;
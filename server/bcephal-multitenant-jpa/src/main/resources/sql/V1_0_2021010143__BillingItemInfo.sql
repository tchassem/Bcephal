CREATE TABLE IF NOT EXISTS BCP_BILLING_INVOICE_ITEM_INFO (
    id BIGINT not null,
	item BIGINT not null,	
	name TEXT,	
	dimensionType VARCHAR(50),		
	position INTEGER,
	tringValue TEXT,
	decimalValue DECIMAL (31, 14),	
	dateValue1 DATE,	
	dateValue2 DATE,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS  billing_invoice_item_info_seq START WITH 1 INCREMENT BY 1;
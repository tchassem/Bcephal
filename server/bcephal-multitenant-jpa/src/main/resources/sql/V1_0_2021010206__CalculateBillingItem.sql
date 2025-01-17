
CREATE TABLE IF NOT EXISTS BCP_CALCULATE_BILLING_ITEM(
	id BIGINT NOT NULL,
	modelId BIGINT,
	type VARCHAR(100),
	driverFunction VARCHAR(100),
	unitCostFunction VARCHAR(100),
	billingAmountFunction VARCHAR(100),
	position BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS calc_bill_item__seq START WITH 1 INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS BCP_CALCULATE_BILLING_FILTER_ITEM(
	id BIGINT NOT NULL,
	calculateEltId BIGINT,
	groupId BIGINT,
	position BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS calc_bill_filter_item__seq START WITH 1 INCREMENT BY 1;
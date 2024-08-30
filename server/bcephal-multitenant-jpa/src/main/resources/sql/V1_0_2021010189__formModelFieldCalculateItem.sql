
CREATE TABLE IF NOT EXISTS BCP_FORM_MODEL_FIELD_CALCULATE_ITEM(
	id BIGINT not null,
	parentId BIGINT,
	position INTEGER not null,
	openingBracket VARCHAR(25),
	closingBracket VARCHAR(25),
	sign VARCHAR(25),
	type VARCHAR(50),
	decimalValue DECIMAL(31, 14),
	fieldId BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS form_model_field_calculate_item_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS BCP_FORM_MODEL_FIELD_CONCATENATE_ITEM(
	id BIGINT not null,
	parentId BIGINT,
	position INTEGER not null,
	type VARCHAR(50),
	stringValue VARCHAR(255),
	fieldId BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS form_model_field_concatenate_item_seq START WITH 1 INCREMENT BY 1;

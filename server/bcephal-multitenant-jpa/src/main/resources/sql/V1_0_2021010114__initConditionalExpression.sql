CREATE TABLE BCP_CONDITIONAL_EXPRESSION (
    id BIGINT not null,	
	description TEXT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE conditional_expression_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE BCP_CONDITIONAL_EXPRESSION_ITEM (
    id BIGINT not null,	
    expression BIGINT,
    filterVerb VARCHAR (5),	
	openBrackets VARCHAR (10),
	closeBrackets VARCHAR (10),
	position INTEGER,	
	operator VARCHAR (25),
    value DECIMAL (31, 14),
    spotId1 BIGINT,
    spotId2 BIGINT,
    value2Type VARCHAR (25),	
	PRIMARY KEY (id)
);
CREATE SEQUENCE conditional_expression_item_seq START WITH 1 INCREMENT BY 1;


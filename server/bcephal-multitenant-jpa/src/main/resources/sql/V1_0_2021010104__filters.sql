-- ---------------------------- FILTERS -----------------------------------
-- ------------------------------------------------------------------------
-- ---------------------------- FILTERS -----------------------------------
-- ------------------------------------------------------------------------

CREATE TABLE BCP_UNIVERSE_FILTER (
    id BIGINT not null,
	measureFilter BIGINT,
	attributeFilter BIGINT,
	periodFilter BIGINT,
	spotFilter BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE universe_filter_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_ATTRIBUTE_FILTER (
    id BIGINT not null,
	PRIMARY KEY (id)
);
CREATE SEQUENCE attribute_filter_seq START WITH 1 INCREMENT BY 1;


create table BCP_ATTRIBUTE_FILTER_ITEM (
	id BIGINT not null,
	filter BIGINT,	
	dimensionType VARCHAR (25),
	dimensionId BIGINT,
	dimensionName TEXT,
	filterVerb VARCHAR (5),	
	openBrackets VARCHAR (10),
	closeBrackets VARCHAR (10),
	formula TEXT,
	position INTEGER,	
	operator VARCHAR (25),
    value TEXT,
    useLink BOOLEAN,
	PRIMARY KEY (id)
);
CREATE SEQUENCE attribute_filter_item_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_MEASURE_FILTER (
    id BIGINT not null,
	PRIMARY KEY (id)
);
CREATE SEQUENCE measure_filter_seq START WITH 1 INCREMENT BY 1;


create table BCP_MEASURE_FILTER_ITEM (
	id BIGINT not null,
	filter BIGINT,	
	dimensionType VARCHAR (25),
	dimensionId BIGINT,
	dimensionName TEXT,
	filterVerb VARCHAR (5),	
	openBrackets VARCHAR (10),
	closeBrackets VARCHAR (10),
	formula TEXT,
	position INTEGER,	
	operator VARCHAR (25),
    value DECIMAL (31, 14),
	PRIMARY KEY (id)
);
CREATE SEQUENCE measure_filter_item_seq START WITH 1 INCREMENT BY 1;



CREATE TABLE BCP_PERIOD_FILTER (
    id BIGINT not null,
	PRIMARY KEY (id)
);
CREATE SEQUENCE period_filter_seq START WITH 1 INCREMENT BY 1;


create table BCP_PERIOD_FILTER_ITEM (
	id BIGINT not null,
	filter BIGINT,	
	dimensionType VARCHAR (25),
	dimensionId BIGINT,
	dimensionName TEXT,
	calendarId BIGINT,
	filterVerb VARCHAR (5),	
	openBrackets VARCHAR (10),
	closeBrackets VARCHAR (10),
	formula TEXT,
	position INTEGER,	
	operator VARCHAR (25),
	comparator VARCHAR (25),
    value DATE,
	sign VARCHAR (1),	
	number INTEGER,	
	granularity VARCHAR (25),
	PRIMARY KEY (id)
);
CREATE SEQUENCE period_filter_item_seq START WITH 1 INCREMENT BY 1;


create table BCP_PERIOD_FILTER_ITEM_CALENDAR (
	id BIGINT not null,
	category BOOLEAN,
	categoryId BIGINT,	
	categoryName TEXT,
	dayId BIGINT,
	PRIMARY KEY (id)
);
CREATE SEQUENCE period_filter_item_calendar_seq START WITH 1 INCREMENT BY 1;




CREATE TABLE BCP_SPOT_FILTER (
    id BIGINT not null,
	PRIMARY KEY (id)
);
CREATE SEQUENCE spot_filter_seq START WITH 1 INCREMENT BY 1;


create table BCP_SPOT_FILTER_ITEM (
	id BIGINT not null,
	filter BIGINT,	
	dimensionType VARCHAR (25),
	dimensionId BIGINT,
	dimensionName TEXT,
	filterVerb VARCHAR (5),	
	openBrackets VARCHAR (10),
	closeBrackets VARCHAR (10),
	formula TEXT,
	position INTEGER,	
	operator VARCHAR (25),
    value DECIMAL (31, 14),
	PRIMARY KEY (id)
);
CREATE SEQUENCE spot_filter_item_seq START WITH 1 INCREMENT BY 1;

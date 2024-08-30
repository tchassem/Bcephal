CREATE TABLE BCP_CALCULATED_MEASURE_EXCLUDE_FILTER (
    id BIGINT not null,
	item BIGINT,
	dimensionId BIGINT,
	active BOOLEAN,	
	position INTEGER,    
	PRIMARY KEY (id)
);
CREATE SEQUENCE calculated_measure_ex_fil_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE BCP_DASHBOARD (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,
	ProfilId VARCHAR(100),
	layout VARCHAR(100),
	published BOOLEAN DEFAULT FALSE,
	AllowRefreshFrequency BOOLEAN DEFAULT FALSE,
	RefreshFrequency INTEGER,
	RefreshFrequencyUnit VARCHAR(100),
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE dashboard_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_DASHBOARD_ITEM (
    id BIGINT not null,
    dashboard BIGINT,
	name VARCHAR(100) not null,
	description TEXT,
	itemType VARCHAR(100),
	itemId BIGINT,
	itemName VARCHAR(100),
	position INTEGER,
	background VARCHAR(100),
	foreground VARCHAR(100),
	height double precision,
	width double precision,
	visible BOOLEAN DEFAULT FALSE,
	showTitleBar BOOLEAN DEFAULT FALSE,
	showBorder boolean DEFAULT false,
	backgroundTitle VARCHAR(100),
	foregroundTitle VARCHAR(100),
	PRIMARY KEY (id)
);

CREATE SEQUENCE dashboard_item_seq START WITH 1 INCREMENT BY 1;



CREATE TABLE BCP_DASHBOARD_REPORT (
    id BIGINT not null,
	name VARCHAR(100) not null,
	visibleInShortcut BOOLEAN DEFAULT TRUE,
	bgroup BIGINT,	
    description TEXT,
	reportType VARCHAR(100),
	tableName VARCHAR(100),
	sql TEXT,
	published BOOLEAN DEFAULT FALSE,
	userFilter BIGINT,
	adminFilter BIGINT,
	chartProperties BIGINT,
	pivotTableProperties BIGINT,
	creationDate TIMESTAMP,
	modificationDate TIMESTAMP,
	PRIMARY KEY (id)
);

CREATE SEQUENCE dashboard_report_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_DASHBOARD_REPORT_FIELD (
    id BIGINT not null,
    dashboardReport BIGINT,
	name VARCHAR(100) not null,
	type VARCHAR(100),
	DimensionId BIGINT,
	DimensionName VARCHAR(100),
	position INTEGER,
	tableName VARCHAR(100),
	sql TEXT,
	PRIMARY KEY (id)
);

CREATE SEQUENCE dashboard_report_fiel_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE BCP_CHART_PROPERTIES (
    id BIGINT not null,
	chartType VARCHAR(100) not null,
	chartDispositionType VARCHAR(100) not null,
	webLayoutData TEXT,
	title VARCHAR(100),
	PRIMARY KEY (id)
);

CREATE SEQUENCE dashboard_chart_prop_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE BCP_PIVOTTABLE_PROPERTIES (
    id BIGINT not null,
	webLayoutData TEXT,
	title VARCHAR(100),
	PRIMARY KEY (id)
);

CREATE SEQUENCE dashboard_pivot_prop_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_PROFILE_DASHBOARD (
    id BIGINT not null,
    profileId BIGINT,
	dashboardId BIGINT,
	position INTEGER,
	defaultDashboard BOOLEAN,	
	PRIMARY KEY (id)
);
CREATE SEQUENCE prifile_dashboard_seq START WITH 1 INCREMENT BY 1;
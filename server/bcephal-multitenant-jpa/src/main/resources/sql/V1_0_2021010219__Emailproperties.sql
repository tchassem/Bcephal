
CREATE TABLE IF NOT EXISTS bcp_email_account (
       id bigint not null,
        user_email varchar(255),
        password varchar(255),
        serve_host varchar(255),
        server_port varchar(255),
        propertiesImpl TEXT,
        primary key (id)
    );
CREATE SEQUENCE IF NOT EXISTS email_account_seq START WITH 1 INCREMENT BY 1;
 

CREATE TABLE IF NOT EXISTS bcp_email_filter (
       id bigint not null,
        expeditor varchar(255),
        subject varchar(255),
        emailAccount bigint,
        send_Date TIMESTAMP,
        attachment TEXT,
        expeditorOperator varchar(50),
        subjectOperator varchar(50),
        sendDateOperator varchar(50),
        attachmentOperator varchar(50),
        primary key (id)
    );
CREATE SEQUENCE IF NOT EXISTS email_filter_seq START WITH 1 INCREMENT BY 1;

    
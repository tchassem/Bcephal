
CREATE TABLE BCP_ALARM_MESSAGE_LOG_TO_SEND (
	id BIGINT not null,	
	message_Type VARCHAR(50),   
	title TEXT, 
	content TEXT,
	contacts_Impl TEXT,	
	files_Impl TEXT,	
	message_Id TEXT,
	client_Code TEXT,
	project_Code TEXT,
	username VARCHAR(255),	
	mode VARCHAR(50),   
	status VARCHAR(50),
	error_Message TEXT,
	error_Code INTEGER,	
	max_Send_Count INTEGER,	 		
	send_Count INTEGER,	
	creation_Date TIMESTAMP,	
	first_Send_Date TIMESTAMP,
	last_Send_Date TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE SEQUENCE alarm_message_log_seq START WITH 1 INCREMENT BY 1;


CREATE TABLE BCP_ALARM_MESSAGE_LOG_SUCCESS (
	id BIGINT not null,	
	message_Type VARCHAR(50),   
	title TEXT, 
	content TEXT,
	contacts_Impl TEXT,	
	files_Impl TEXT,	
	message_Id TEXT,
	client_Code TEXT,
	project_Code TEXT,
	username VARCHAR(255),		
	mode VARCHAR(50),   
	status VARCHAR(50),
	error_Message TEXT,
	error_Code INTEGER,	
	max_Send_Count INTEGER,	 		
	send_Count INTEGER,	
	creation_Date TIMESTAMP,	
	first_Send_Date TIMESTAMP,
	last_Send_Date TIMESTAMP,
	PRIMARY KEY (id)
);

CREATE TABLE BCP_ALARM_MESSAGE_LOG_FAIL (
    id BIGINT not null,	
	message_Type VARCHAR(50),   
	title TEXT, 
	content TEXT,
	contacts_Impl TEXT,	
	files_Impl TEXT,	
	message_Id TEXT,
	client_Code TEXT,
	project_Code TEXT,
	username VARCHAR(255),		
	mode VARCHAR(50),   
	status VARCHAR(50),
	error_Message TEXT,
	error_Code INTEGER,	
	max_Send_Count INTEGER,	 		
	send_Count INTEGER,	
	creation_Date TIMESTAMP,	
	first_Send_Date TIMESTAMP,
	last_Send_Date TIMESTAMP,
	PRIMARY KEY (id)
);



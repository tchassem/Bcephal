package com.moriset.bcephal.gateway.domain;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserSessionLog {
	
	private Long userId;
	private String username;
	private Long clientId;
	private String clientName;
	private Long projectId;
	private String projectCode;	
	private String projectName;
	private String usersession;
	private String status;
	private Timestamp lastoperationDate;
	private Timestamp endDate;
}

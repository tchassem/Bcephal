package com.moriset.bcephal.security.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "UserSessionLog")
@Table(name = "BCP_SEC_USER_SESSION_LOG")
@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class UserSessionLog extends MainObject {

	private static final long serialVersionUID = 2140868145851761146L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sec_usser_session_log_seq")
	@SequenceGenerator(name = "sec_usser_session_log_seq", sequenceName = "sec_usser_session_log_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private Long userId;
	private String username;
	
	private Long clientId;
	private String clientName;
	
	private Long projectId;
	private String projectCode;	
	private String projectName;
	
	private String usersession;
	
	private String status;
	
	private String functionality;
	
	private Long objectId;
	
	private String profile;
	
	@Enumerated(EnumType.STRING)
	private ProfileType userType;
	
	@Enumerated(EnumType.STRING) 
	private RightLevel rightLevel;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp lastoperationDate;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp endDate;

	
	
	
	@Override
	public Persistent copy() {
		return null;
	}
	
	
}

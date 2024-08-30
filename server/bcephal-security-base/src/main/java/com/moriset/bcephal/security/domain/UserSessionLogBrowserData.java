/**
 * 
 */
package com.moriset.bcephal.security.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class UserSessionLogBrowserData extends BrowserData{

	private String username;
	
	private String clientName;
	
	private String projectName;
	
	private String usersession;
	
	private String status;
	
	private String functionality;
	
	private RightLevel rightLevel;
	
	private String profile;
	
	private Long sourceId;
	
	@Enumerated(EnumType.STRING)
	private ProfileType userType;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp lastoperationDate;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp endDate;
	
	public UserSessionLogBrowserData(UserSessionLog item){
		super(item.getId(), item.getName(), true, item.getCreationDate(), item.getModificationDate());
		setUsername(item.getUsername());
		setClientName(item.getClientName());
		setProjectName(item.getProjectName());
		setUsersession(item.getUsersession());
		setStatus(item.getStatus());
		setLastoperationDate(item.getLastoperationDate());
		setEndDate(item.getEndDate());
		setFunctionality(item.getFunctionality());
		setRightLevel(item.getRightLevel());
		setProfile(item.getProfile());
		setUserType(item.getUserType());
		setSourceId(item.getObjectId());
	}

}

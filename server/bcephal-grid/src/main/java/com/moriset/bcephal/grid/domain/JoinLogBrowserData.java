/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Joseph Wambo
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JoinLogBrowserData extends BrowserData {

	private Long joinId;
	
	@Enumerated(EnumType.STRING) 
	private JoinPublicationMethod publicationMethod;
	
	@Enumerated(EnumType.STRING)
	private DataSourceType publicationGridType;
	
	private String publicationGridName;
	
	private String publicationNumber;
	
	private Long publicationNbrAttributeId;	
	
	private String publicationNbrAttributeName;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp endDate;
	
	@Enumerated(EnumType.STRING) 
	private RunStatus status;
	
	@Enumerated(EnumType.STRING) 
	private RunModes mode;
		
	private String user;
		
	private long rowCount;
	
	private String message;
	
	
	public JoinLogBrowserData(JoinLog log) {
		super(log.getId(), log.getName(), log.getCreationDate(), log.getModificationDate());		
		this.joinId = log.getJoinId();	
		publicationMethod = log.getPublicationMethod();
		publicationGridType = log.getPublicationGridType();
		publicationGridName = log.getPublicationGridName();		
		publicationNumber = log.getPublicationNumber();		
		endDate = log.getEndDate();		
		status = log.getStatus();		
		mode = log.getMode();			
		user = log.getUser();			
		publicationNbrAttributeId = log.getPublicationNbrAttributeId();		
		publicationNbrAttributeName = log.getPublicationNbrAttributeName();		
		rowCount = log.getRowCount();
		message = log.getMessage();		
	}
	
}

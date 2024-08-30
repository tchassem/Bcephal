/**
 * 
 */
package com.moriset.bcephal.scheduler.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SchedulerLogBrowserData extends BrowserData {
	
		
	private SchedulerTypes objectType;
		
	private RunStatus status;
			
	private String message;
	
	private String details;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp endDate;
	
	public SchedulerLogBrowserData(SchedulerLog log){
		super(log.getId(), log.getObjectName(), log.getCreationDate(), log.getEndDate());
		this.objectType = log.getObjectType();
		this.status = log.getStatus();
		this.message = log.getMessage();
		this.details = log.getDetails();
		this.endDate = log.getEndDate();
	}
	
}

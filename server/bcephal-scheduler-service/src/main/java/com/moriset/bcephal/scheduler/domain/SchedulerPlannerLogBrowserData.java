/**
 * 
 */
package com.moriset.bcephal.scheduler.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.RunModes;
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
public class SchedulerPlannerLogBrowserData extends BrowserData {
	
			
	private RunModes mode;
		
	private RunStatus status;
			
	private String message;
	
	private String username;
	
	private int steps;
	
	private String operationCode;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp endDate;
	
	public SchedulerPlannerLogBrowserData(SchedulerPlannerLog log){
		super(log.getId(), log.getSchedulerName(), log.getStartDate(), log.getEndDate());		
		this.mode = log.getMode();
		this.status = log.getStatus();
		this.message = log.getMessage();
		this.endDate = log.getEndDate();
		this.username = log.getUsername();
		this.steps = log.getSetps();
		this.operationCode = log.getOperationCode();
	}
	
}

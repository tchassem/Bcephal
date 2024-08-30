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
public class SchedulerPlannerLogItemBrowserData extends BrowserData {
	
	private int position;
			
	private SchedulerPlannerItemType type;
		
	private RunStatus status;
			
	private String message;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp endDate;
	
	public SchedulerPlannerLogItemBrowserData(SchedulerPlannerLogItem item){
		super(item.getId(), item.getObjectName(), item.getStartDate(), item.getEndDate());	
		this.type = item.getType();
		this.position = item.getPosition();
		this.status = item.getStatus();
		this.message = item.getMessage();
		this.endDate = item.getEndDate();
	}
	
}

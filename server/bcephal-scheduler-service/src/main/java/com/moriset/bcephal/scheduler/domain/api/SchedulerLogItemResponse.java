package com.moriset.bcephal.scheduler.domain.api;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerItemType;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "SchedulerLogItemResponse")
@Table(name = "BCP_SCHEDULER_PLANNER_LOG_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class SchedulerLogItemResponse {

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scheduler_planner_log_item_seq")
	@SequenceGenerator(name = "scheduler_planner_log_item_seq", sequenceName = "scheduler_planner_log_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne
	@JoinColumn(name = "logId")
	private SchedulerLogResponse log;
		
	@JsonIgnore
	private int position;
	
	@Enumerated(EnumType.STRING) 
	private SchedulerPlannerItemType type;
		
	private String objectName;
	
	private String message;
	
	@Enumerated(EnumType.STRING) 
	private RunStatus status;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp startDate;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp endDate;
	
}

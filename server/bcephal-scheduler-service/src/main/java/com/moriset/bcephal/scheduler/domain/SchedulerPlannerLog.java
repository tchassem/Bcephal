package com.moriset.bcephal.scheduler.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

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

@Entity(name = "SchedulerPlannerLog")
@Table(name = "BCP_SCHEDULER_PLANNER_LOG")
@Data
@EqualsAndHashCode(callSuper = false)
public class SchedulerPlannerLog extends Persistent {
	
	private static final long serialVersionUID = -806783812667261753L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scheduler_planner_log_seq")
	@SequenceGenerator(name = "scheduler_planner_log_seq", sequenceName = "scheduler_planner_log_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	@JsonIgnore
	private Long schedulerId;
	
	private String schedulerName;	
	
	private String message;
	
	@Enumerated(EnumType.STRING) 
	private RunStatus status;
		
	@Enumerated(EnumType.STRING) 
	public RunModes mode;
		
	public String username;
	
	private int setps;
	
	private int runnedSetps;
	
	private Long currentItemId;
	
	private String projectCode;
	
	private String operationCode;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp startDate;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp endDate;
	
	
	public SchedulerPlannerLog() {
		this.status = RunStatus.IN_PROGRESS;
		this.startDate = new Timestamp(System.currentTimeMillis());
	}
	
	
	@JsonIgnore
	@Override
	public Persistent copy() {
		return null;
	}
	
}

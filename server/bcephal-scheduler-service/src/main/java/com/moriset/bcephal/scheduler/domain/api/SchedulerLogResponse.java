package com.moriset.bcephal.scheduler.domain.api;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "SchedulerLogResponse")
@Table(name = "BCP_SCHEDULER_PLANNER_LOG")
@Data
@EqualsAndHashCode(callSuper = false)
public class SchedulerLogResponse {

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scheduler_planner_log_seq")
	@SequenceGenerator(name = "scheduler_planner_log_seq", sequenceName = "scheduler_planner_log_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private String schedulerName;	
	
	private String message;
	
	@Enumerated(EnumType.STRING) 
	private RunStatus status;
		
	@Enumerated(EnumType.STRING) 
	public RunModes mode;
		
	public String username;
	
	private int setps;
	
	private int runnedSetps;
			
	@JsonIgnore
	private String operationCode;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp startDate;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp endDate;
	
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "log")
	private List<SchedulerLogItemResponse> items;
	
	@PostLoad
	public void initListChangeHandler() {		
		items.forEach(x->{});
	}
	
}

package com.moriset.bcephal.integration.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.RunStatus;
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

@Entity(name = "SchedulerEntityLog")
@Table(name = "BCP_SCHEDULER_ENTITY_LOG")
@Data
@EqualsAndHashCode(callSuper = false)
public class SchedulerConnectEntityLog extends Persistent {

	private static final long serialVersionUID = -3078336503659701900L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scheduler_entity_log_seq")
	@SequenceGenerator(name = "scheduler_entity_log_seq", sequenceName = "scheduler_entity_log_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private Long clientId;
	
	private Long objectId;
	
	private String objectName;
	
	@Enumerated(EnumType.STRING) 
	private RunStatus status;
	
	private String message;
	
	private String projectCode;
	
	private String details;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp creationDate;	
		
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp endDate;
		
	
	
	public SchedulerConnectEntityLog() {
		this.status = RunStatus.IN_PROGRESS;
		this.creationDate = new Timestamp(System.currentTimeMillis());
	}
	

	@JsonIgnore
	@Override
	public Persistent copy() {
		return null;
	}
}
	
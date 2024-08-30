package com.moriset.bcephal.task.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.Persistent;

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

@Entity(name = "TaskLog")
@Table(name = "BCP_TASK_LOG")
@Data
@EqualsAndHashCode(callSuper = false)
public class TaskLog extends MainObject {
	
	private static final long serialVersionUID = 2337855391399214437L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_log_seq")
	@SequenceGenerator(name = "task_log_seq", sequenceName = "task_log_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	private Long taskId;
	
	@Enumerated(EnumType.STRING) 
	private TaskLogType type;
	
	private String username;

	@Override
	public Persistent copy() {
		return null;
	}
	
}

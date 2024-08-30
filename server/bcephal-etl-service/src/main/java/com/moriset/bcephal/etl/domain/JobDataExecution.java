package com.moriset.bcephal.etl.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "JobDataExecution")
@Table(name = "batch_job_execution")
@Data
public class JobDataExecution {
	
	@ToString.Include
	@Id
	@Column(name = "job_execution_id")
	Long id; 
	
	@Column(name = "version")
	Long version; 
	
	@EqualsAndHashCode.Exclude
	//@ToString.Exclude
	@ManyToOne()
	@JoinColumn(name = "job_instance_id")
	JobDataInstance instance;
	
	@ToString.Include
	@Column(name = "status")
	String status;
	
	@Column(name = "exit_code")
	String exitCode;
	
	@ToString.Include
	@Column(name = "exit_message")
	String message;
	
//	@ToString.Include
	@Column(name = "create_time")
	Date creationDate; 
	
	@ToString.Include
	@Column(name = "start_time")
	Date startDate; 
	
	@ToString.Include
	@Column(name = "end_time")
	Date endDate; 
	
	@Column(name = "last_updated")
	Date modificationDate;
	
	@Transient
	String name;

	
	
}

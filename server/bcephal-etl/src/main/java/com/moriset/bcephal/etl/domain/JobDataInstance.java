package com.moriset.bcephal.etl.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "JobDataInstance")
@Table(name = "batch_job_instance")
@Data
public class JobDataInstance {
	
	@Id 
	@Column(name="job_instance_id")
	long id;
	
	@Column(name="job_name")
	String name;
	@Column(name="version")
	Long version;
	@Column(name="job_key")
	String key;

}

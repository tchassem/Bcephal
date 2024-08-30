package com.moriset.bcephal.security.domain;

import java.sql.Timestamp;
import java.util.Locale;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity(name = "SimpleArchive")
@Table(name = "BCP_SEC_SIM_ARCHIVE")
public class SimpleArchive {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sec_sim_archive_seq")
	@SequenceGenerator(name = "sec_sim_archive_seq", sequenceName = "sec_sim_archive_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	private String name;

	@JsonIgnore
	private String repository;

	private String description;
	
	private String projectCode;
	
	private String userName;

	private Integer archiveMaxCount;

	private String fileName;

	@Transient
	private Locale locale;

	private Long projectId;

	private Long clientId;	

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@CreationTimestamp
	@Column(nullable = false)
	private Timestamp creationDate;
	
	@Transient
	private String remotePath;
	
	@Transient
	private boolean manualImport;
}

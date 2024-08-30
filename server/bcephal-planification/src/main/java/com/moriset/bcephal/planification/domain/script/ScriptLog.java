package com.moriset.bcephal.planification.domain.script;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.RunModes;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@Entity(name = "ScriptLog")
@Table(name = "BCP_SCRIPT_LOG")
public class ScriptLog extends Persistent {

	private static final long serialVersionUID = -5379057897532471804L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "script_log_seq")
	@SequenceGenerator(name = "script_log_seq", sequenceName = "script_log_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private Long scriptId;
	
	private String scriptName;
	
	@Enumerated(EnumType.STRING)
	private RunModes mode;
	
	@Enumerated(EnumType.STRING)
	private RunStatus status;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp startDate;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)	
	private Timestamp endDate;
	
	private String username;
		
	private String message;

	private String operationCode;

	@Override
	public Persistent copy() {
		return null;
	}
	
}

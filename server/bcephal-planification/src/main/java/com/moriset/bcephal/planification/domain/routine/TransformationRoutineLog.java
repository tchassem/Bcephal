/**
 * 
 */
package com.moriset.bcephal.planification.domain.routine;

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
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Entity(name = "TransformationRoutineLog")
@Table(name = "BCP_TRANSFORMATION_ROUTINE_LOG")
@Data
@EqualsAndHashCode(callSuper = false)
public class TransformationRoutineLog extends Persistent {

	private static final long serialVersionUID = 2827685055475882752L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transformation_routine_log_seq")
	@SequenceGenerator(name = "transformation_routine_log_seq", sequenceName = "transformation_routine_log_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private Long routineId;
	
	private String routineName;
	
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
	
	private int count;
	
	private String message;

	private String operationCode;
	
	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

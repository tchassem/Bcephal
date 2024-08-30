/**
 * 
 */
package com.moriset.bcephal.planification.domain.routine;

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

/**
 * @author Moriset
 *
 */
@Entity(name = "TransformationRoutineLogItem")
@Table(name = "BCP_TRANSFORMATION_ROUTINE_LOG_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class TransformationRoutineLogItem extends Persistent {

	private static final long serialVersionUID = 7934881276916088792L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transformation_routine_log_item_seq")
	@SequenceGenerator(name = "transformation_routine_log_item_seq", sequenceName = "transformation_routine_log_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	private Long logId;
	
	private String itemName;
	
	private String username;
	
	private int count;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp startDate;
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp endDate;
	
	@Enumerated(EnumType.STRING)
	private RunStatus status;
	
	private String message;

	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

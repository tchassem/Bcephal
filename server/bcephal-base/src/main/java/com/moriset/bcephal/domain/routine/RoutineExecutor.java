/**
 * 
 */
package com.moriset.bcephal.domain.routine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

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
 * @author Joseph Wambo
 *
 */
@jakarta.persistence.Entity(name = "RoutineExecutor")
@Table(name = "BCP_ROUTINE_EXECUTOR")
@Data
@EqualsAndHashCode(callSuper = false)
public class RoutineExecutor extends Persistent {

	private static final long serialVersionUID = 1447704974798786972L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "routine_executor_seq")
	@SequenceGenerator(name = "routine_executor_seq", sequenceName = "routine_executor_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private Long routineId;
	
	private int position;
	
	private Boolean active;
	
	@JsonIgnore
	private Long objectId;
	
	@JsonIgnore
	private String objectType;
	
	@Enumerated(EnumType.STRING)
	private RoutineExecutorType type;
	
	public Boolean getActive() {
		return isActive();
	}
	
	public boolean isActive() {
		if(active == null){
			active = true;
		}
		return active;
	}

	@Override
	public RoutineExecutor copy() {	
		RoutineExecutor copy = new RoutineExecutor();
		copy.setObjectId(objectId);
		copy.setObjectType(objectType);
		copy.setPosition(position);
		copy.setRoutineId(routineId);
		copy.setType(type);
		copy.setActive(active);
		return copy;
	}
	
}

/**
 * 
 */
package com.moriset.bcephal.planification.domain.routine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString.Exclude;

/**
 * @author Moriset
 *
 */
@Entity(name = "TransformationRoutineConcatenateItem")
@Table(name = "BCP_TRANSFORMATION_ROUTINE_CONCATENATE_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class TransformationRoutineConcatenateItem extends Persistent {

	private static final long serialVersionUID = -6007273743688642301L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transformation_routine_concatenate_seq")
	@SequenceGenerator(name = "transformation_routine_concatenate_seq", sequenceName = "transformation_routine_concatenate_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	@Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "routineItem")
	private TransformationRoutineItem routineItem;
	
	private int position;

	@Exclude
	@ManyToOne @JoinColumn(name = "field")
	private TransformationRoutineField field;
	
	
	@Override
	public TransformationRoutineConcatenateItem copy() {
		TransformationRoutineConcatenateItem copy = new TransformationRoutineConcatenateItem();
		copy.setRoutineItem(getRoutineItem());
		copy.setPosition(getPosition());
		copy.setField(getField().copy());
		return copy;
	}
	
}

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
import lombok.ToString;

/**
 * @author Moriset
 *
 */
@Entity(name = "TransformationRoutineCalculateItem")
@Table(name = "BCP_TRANSFORMATION_ROUTINE_CALCULATE_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class TransformationRoutineCalculateItem extends Persistent {

	private static final long serialVersionUID = 7662225018831316220L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transformation_routine_calculate_seq")
	@SequenceGenerator(name = "transformation_routine_calculate_seq", sequenceName = "transformation_routine_calculate_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "routineItem")
	private TransformationRoutineItem routineItem;
	
	private int position;
	
	private String openingBracket;
	
	private String closingBracket;
	
	private String sign;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne @JoinColumn(name = "field")
	private TransformationRoutineField field;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name = "spot")
	private TransformationRoutineSpot spot;
	
	
	@Override
	public TransformationRoutineCalculateItem copy() {
		TransformationRoutineCalculateItem copy = new TransformationRoutineCalculateItem();
		copy.setPosition(getPosition());
		copy.setOpeningBracket(getOpeningBracket());
		copy.setClosingBracket(getClosingBracket());
		copy.setSign(getSign());
		copy.setField(getField() != null ? getField().copy(): null);
		copy.setSpot(getSpot() != null ? getSpot().copy(): null);
		return copy;
	}
	
}

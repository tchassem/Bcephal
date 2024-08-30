/**
 * 
 */
package com.moriset.bcephal.grid.domain;

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
 * @author Joseph Wambo
 *
 */
@Entity(name = "JoinColumnConcatenateItem")
@Table(name = "BCP_JOIN_COLUMN_CONCATENATE_ITEM")
@Data 
@EqualsAndHashCode(callSuper = false)
public class JoinColumnConcatenateItem extends Persistent {
	
	private static final long serialVersionUID = -6090766300268499241L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "join_column_concatenate_seq")
	@SequenceGenerator(name = "join_column_concatenate_seq", sequenceName = "join_column_concatenate_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "propertiesId")
	private JoinColumnProperties propertiesId;
	
	private int position;
		
	@ManyToOne @jakarta.persistence.JoinColumn(name = "field")
	private JoinColumnField field;
	

	@Override
	public Persistent copy() {
		JoinColumnConcatenateItem copy = new JoinColumnConcatenateItem();
		copy.setPosition(getPosition());
		//copy.setPropertiesId(getPropertiesId());
		if(getField() != null) {
			JoinColumnField joinColumnField = (JoinColumnField)field.copy();
			copy.setField(joinColumnField);
		}
		
		return copy;
	}
	

}

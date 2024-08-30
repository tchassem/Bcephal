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
@Entity(name = "JoinColumnCalculateItem")
@Table(name = "BCP_JOIN_COLUMN_CALCULATE_ITEM")
@Data 
@EqualsAndHashCode(callSuper = false)
public class JoinColumnCalculateItem extends Persistent {
	
	private static final long serialVersionUID = -9221170045701843853L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "join_column_calculate_seq")
	@SequenceGenerator(name = "join_column_calculate_seq", sequenceName = "join_column_calculate_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "propertiesId")
	private JoinColumnProperties propertiesId;
	
	private int position;
	
	private String openingBracket;
	
	private String closingBracket;
	
	private String sign;
		
	@ManyToOne @jakarta.persistence.JoinColumn(name = "field")
	private JoinColumnField field;

	@Override
	public Persistent copy() {
		JoinColumnCalculateItem copy = new JoinColumnCalculateItem();
		//copy.setPropertiesId(getPropertiesId());
		copy.setPosition(getPosition());
		copy.setOpeningBracket(getOpeningBracket());
		copy.setClosingBracket(getClosingBracket());
		copy.setSign(getSign());
		
		if(getField() != null) {
			JoinColumnField  joinColumnField = (JoinColumnField)getField().copy();
			copy.setField(joinColumnField);	
		}
		
		return copy;
	}
	
}

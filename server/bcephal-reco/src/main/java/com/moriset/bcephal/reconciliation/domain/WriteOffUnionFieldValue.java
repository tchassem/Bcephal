/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.filters.PeriodValue;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "WriteOffUnionFieldValue")
@Table(name = "BCP_WRITE_OFF_UNION_FIELD_VALUE")
@Data
@EqualsAndHashCode(callSuper = false)
public class WriteOffUnionFieldValue extends Persistent {

	private static final long serialVersionUID = 3237993022227596122L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "writeoff_union_field_value_seq")
	@SequenceGenerator(name = "writeoff_union_field_value_seq", sequenceName = "writeoff_union_field_value_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@jakarta.persistence.JoinColumn(name = "field")
	private WriteOffUnionField field;
	
	private int position;
	
	private boolean defaultValue;
		
	private String stringValue;	
	private BigDecimal decimalValue;	
	@Embedded	
	private PeriodValue dateValue;
	
	public WriteOffUnionFieldValue() {
		dateValue = new PeriodValue();
	}
	
	
	public void setDateValue(PeriodValue dateValue) {
		if(dateValue == null) {
			this.dateValue = new PeriodValue();
		}else {
			this.dateValue = dateValue;
		}
	}
	
	@PostLoad
	public void initListChangeHandler() {
		if(dateValue == null) {
			 dateValue = new PeriodValue();
		}
	}
	
	@JsonIgnore
	@Override
	public WriteOffUnionFieldValue copy() {
		WriteOffUnionFieldValue copy = new WriteOffUnionFieldValue();
		copy.setPosition(position);
		copy.setDefaultValue(defaultValue);
		copy.setStringValue(stringValue);
		copy.setDecimalValue(decimalValue);
		copy.setDateValue(dateValue.copy());
		return copy;
	}
	
	

}

/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.PeriodValue;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "WriteOffUnionField")
@Table(name = "BCP_WRITE_OFF_UNION_FIELD")
@Data
@EqualsAndHashCode(callSuper = false)
public class WriteOffUnionField extends Persistent {

	private static final long serialVersionUID = 5601887429814900356L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "writeoff_union_field_seq")
	@SequenceGenerator(name = "writeoff_union_field_seq", sequenceName = "writeoff_union_field_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@jakarta.persistence.JoinColumn(name = "model")
	private WriteOffUnionModel model;
	
	private int position;

	@Enumerated(EnumType.STRING) 
	private WriteOffFieldValueType defaultValueType;
		
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;
	
	private Long dimensionId;	

	private Long secondDimensionId;
	
	private boolean mandatory;
	
	private boolean allowNewValue;
	
	private boolean defaultValue;
	
	private String stringValue;	
	private BigDecimal decimalValue;	
	@Embedded
	private PeriodValue dateValue;
			
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "field")
	private List<WriteOffUnionFieldValue> values;

	@Transient
	private ListChangeHandler<WriteOffUnionFieldValue> valueListChangeHandler;
	
	
	
	/**
	 * Default constructor
	 */
	public WriteOffUnionField() {
		this.allowNewValue = false;
		valueListChangeHandler = new ListChangeHandler<>();
		dateValue = new PeriodValue();
	}
	
	public void setFields(List<WriteOffUnionFieldValue> values) {
		this.values = values;
		valueListChangeHandler.setOriginalList(values);
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
		values.size();
		this.valueListChangeHandler.setOriginalList(values);
		if(dateValue == null) {
			 dateValue = new PeriodValue();
		}
	}

	@JsonIgnore
	public List<WriteOffUnionFieldValue> getSortedValues() {
		List<WriteOffUnionFieldValue> conditions = getValueListChangeHandler().getItems();
		Collections.sort(conditions, new Comparator<WriteOffUnionFieldValue>() {
			@Override
			public int compare(WriteOffUnionFieldValue item1, WriteOffUnionFieldValue item2) {
				return item1.getPosition() - item2.getPosition();
			}
		});
		return conditions;
	}
	
	@JsonIgnore
	@Override
	public WriteOffUnionField copy() {
		WriteOffUnionField copy = new WriteOffUnionField();
		copy.setPosition(position);
		copy.setDefaultValueType(defaultValueType);
		copy.setDimensionType(dimensionType);
		copy.setDimensionId(dimensionId);	
		copy.setSecondDimensionId(secondDimensionId);
		copy.setMandatory(mandatory);
		copy.setAllowNewValue(allowNewValue);
		copy.setDefaultValue(defaultValue);
		copy.setStringValue(stringValue);	
		copy.setDecimalValue(decimalValue);	
		copy.setDateValue(dateValue.copy());
		for(WriteOffUnionFieldValue value : getValueListChangeHandler().getItems()) {
			copy.getValueListChangeHandler().addNew(value.copy());
		}
		return copy;
	}
	
}

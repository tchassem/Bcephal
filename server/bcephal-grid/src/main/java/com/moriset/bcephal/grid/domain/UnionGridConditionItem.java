package com.moriset.bcephal.grid.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.PeriodValue;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "UnionGridConditionItem")
@Table(name = "BCP_UNION_GRID_CONDITION_ITEM")
@Data 
@EqualsAndHashCode(callSuper = false)
public class UnionGridConditionItem extends Persistent {

	private static final long serialVersionUID = 4753232713627983105L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "union_grid_condition_item_seq")
	@SequenceGenerator(name = "union_grid_condition_item_seq", sequenceName = "union_grid_condition_item_seq", initialValue = 1, allocationSize = 1)
	private Long id;
			
	@Enumerated(EnumType.STRING)
    private JoinConditionItemType type;
	
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;
	
	private Long columnId;
		
	private String stringValue;	
	
	private BigDecimal decimalValue;	
		
	@Embedded
	@AttributeOverrides({
		  @AttributeOverride( name = "dateOperator", column = @jakarta.persistence.Column(name = "dateValue_dateOperator")),
		  @AttributeOverride( name = "dateValue", column = @jakarta.persistence.Column(name = "dateValue_dateValue")),
		  @AttributeOverride(name="variableName", 	column = @Column(name="dateValue_variableName")),
		  @AttributeOverride( name = "dateSign", column = @jakarta.persistence.Column(name = "dateValue_dateSign")),
		  @AttributeOverride( name = "dateNumber", column = @jakarta.persistence.Column(name = "dateValue_dateNumber")),
		  @AttributeOverride( name = "dateGranularity", column = @jakarta.persistence.Column(name = "dateValue_dateGranularity"))
		})
	private PeriodValue periodValue;
	
		
	@JsonIgnore @Transient
	private UnionGridColumn column;
	
	@JsonIgnore
	public boolean isAttribute() {
		return dimensionType == DimensionType.ATTRIBUTE;
	}
	
	@JsonIgnore
	public boolean isMeasure() {
		return dimensionType == DimensionType.MEASURE;
	}
	
	@JsonIgnore
	public boolean isPeriod() {
		return dimensionType == DimensionType.PERIOD;
	}
	
	
	@JsonIgnore
	public boolean isParameter() {
		return type == JoinConditionItemType.PARAMETER;
	}
	
	@JsonIgnore
	public boolean isColumn() {
		return type == JoinConditionItemType.COLUMN;
	}
	
	@JsonIgnore
	public boolean isSpot() {
		return type == JoinConditionItemType.SPOT;
	}
	
	@JsonIgnore
	public String getDbColName(Long gridId) {	
		if(column != null) {
			SmartMaterializedGridColumn col = column.getColumnByGridId(gridId);
			if(col != null) {
				return col.getDbColumnName();
			}
		}
		return null;
	}
	
	@JsonIgnore
	public Object getDbParameterValue() {
		if(isParameter()) {
			if (isMeasure()) {
				return decimalValue;
			}
			if (isAttribute()) {
				return stringValue;
			}
			if (isPeriod()) {			
				return buildDateValue();
			}
		}		
		return null;
	}
	
	private Date buildDateValue() {	
		Date date = periodValue != null ? periodValue.buildDynamicDate() : null;
		return date;
	}
	
	@Override
	public UnionGridConditionItem copy() {
		UnionGridConditionItem copy = new UnionGridConditionItem();
		copy.setColumnId(getColumnId());
		copy.setDecimalValue(getDecimalValue());
		copy.setDimensionType(getDimensionType());
		copy.setPeriodValue(getPeriodValue());
		copy.setStringValue(getStringValue());
		copy.setType(getType());
		return copy;
	}
	
	
}

/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
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
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "JoinConditionItem")
@Table(name = "BCP_JOIN_CONDITION_ITEM")
@Data 
@EqualsAndHashCode(callSuper = false)
public class JoinConditionItem extends Persistent {
	
	private static final long serialVersionUID = 4415651205514128051L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "join_condition_item_seq")
	@SequenceGenerator(name = "join_condition_item_seq", sequenceName = "join_condition_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@Enumerated(EnumType.STRING)
    private JoinConditionItemType type;
	
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;
	@Enumerated(EnumType.STRING)
	private JoinGridType gridType;
	private Long gridId;
	private Long columnId;
	private Long dimensionId;
	private String dimensionName;
		
	private String stringValue;	
	
	private BigDecimal decimalValue;	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@Transient
	private DataSourceType dataSourceType;
	
	@Embedded
	@AttributeOverrides({
		  @AttributeOverride( name = "dateOperator", column = @jakarta.persistence.Column(name = "dateValue_dateOperator")),
		  @AttributeOverride( name = "dateValue", column = @jakarta.persistence.Column(name = "dateValue_dateValue")),
		  @AttributeOverride(name="variableName", 	column = @Column(name="dateValue_variableName")),
		  @AttributeOverride( name = "dateSign", column = @jakarta.persistence.Column(name = "dateValue_dateSign")),
		  @AttributeOverride( name = "dateNumber", column = @jakarta.persistence.Column(name = "dateValue_dateNumber")),
		  @AttributeOverride( name = "dateGranularity", column = @jakarta.persistence.Column(name = "dateValue_dateGranularity_"))
		})
	private PeriodValue periodValue;
	
	
	
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
	public String getDbColName(boolean withGridVarName, boolean withalias) {	
		JoinColumn column = new JoinColumn(getGridType(), getGridId(), getColumnId(), 
				getDimensionId(), getDimensionName(), getDimensionType(), getDataSourceType());		
		return column.getDbColName(withGridVarName, withalias, false);
	}
	

	@JsonIgnore
	public String getDbColumnValue(Join join) {
		if(isColumn()) {
			
		}		
		return null;
	}
	
	@JsonIgnore
	public Object getDbParameterValue() {			
		return getDbParameterValue(new HashMap<>());
	}
	
	@JsonIgnore
	public Object getDbParameterValue(Map<String, Object> variableValues) {
		if(isParameter()) {
			if (isMeasure()) {
				return decimalValue;
			}
			if (isAttribute()) {
				return stringValue;
			}
			if (isPeriod()) {			
				return buildDateValue(variableValues);
			}
		}		
		return null;
	}
	
	private Object buildDateValue(Map<String, Object> variableValues) {	
		Object date = periodValue != null ? periodValue.buildDynamicDate(variableValues) : null;
		return date;
	}
	

	@Override
	public JoinConditionItem copy() {
		JoinConditionItem copy = new JoinConditionItem();
		copy.setColumnId(getColumnId());
		copy.setDecimalValue(getDecimalValue());
		copy.setDimensionId(getDimensionId());
		copy.setDimensionName(getDimensionName());
		copy.setDimensionType(getDimensionType());
		copy.setGridId(getGridId());
		copy.setGridType(getGridType());
		copy.setPeriodValue(getPeriodValue());
		copy.setStringValue(getStringValue());
		copy.setType(getType());
		
		return copy;
	}
	
}

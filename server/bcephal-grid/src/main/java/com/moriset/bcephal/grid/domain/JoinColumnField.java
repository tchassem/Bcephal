/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Spot;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.service.filters.ISpotService;

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
@Entity(name = "JoinColumnField")
@Table(name = "BCP_JOIN_COLUMN_FIELD")
@Data 
@EqualsAndHashCode(callSuper = false)
public class JoinColumnField extends Persistent {
	
	private static final long serialVersionUID = 4603319274468599383L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "join_column_field_seq")
	@SequenceGenerator(name = "join_column_field_seq", sequenceName = "join_column_field_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	
	@Enumerated(EnumType.STRING)
    private JoinColumnType type;
	
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;	
	@Enumerated(EnumType.STRING)
	private JoinGridType gridType;
	private Long gridId;
	private Long columnId;
	private Long dimensionId;
	private String dimensionName;		
	private Integer startPosition;
	private Integer endPosition;
		
	private String stringValue;	
	
	private BigDecimal decimalValue;	
	
	@Embedded	
	private PeriodValue dateValue;
	
	@JsonIgnore @Transient
	private Spot spot;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@Transient
	private DataSourceType dataSourceType;
	
	
	public JoinColumnField() {
		this.dateValue = new PeriodValue();
	}

	
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
	public boolean isFree() {
		return type == JoinColumnType.FREE;
	}
	
	@JsonIgnore
	public boolean isColumn() {
		return type == JoinColumnType.COLUMN;
	}
	
	@JsonIgnore
	public boolean isConcatenate() {
		return type == JoinColumnType.CONCATENATE;
	}
	
	@JsonIgnore
	public boolean isCalculate() {
		return type == JoinColumnType.CALCULATE;
	}
	
	@JsonIgnore
	public boolean isPosition() {
		return type == JoinColumnType.POSITION;
	}
	
	@JsonIgnore
	public boolean isCopy() {
		return type == JoinColumnType.COPY;
	}
	
	@JsonIgnore
	public boolean isSequence() {
		return type == JoinColumnType.SEQUENCE;
	}
	
	@JsonIgnore
	public boolean isCondition() {
		return type == JoinColumnType.CONDITION;
	}
	
	@JsonIgnore
	public boolean isSpot() {
		return type == JoinColumnType.SPOT;
	}
	
	@JsonIgnore
	public boolean isVariable() {
		return type == JoinColumnType.VARIABLE 
				|| (isPeriod() && dateValue != null && dateValue.isVariable());
	}
	
	
	@JsonIgnore
	public String getCustomColumnCol(String measureFunction, boolean withMeasureFunction) {
		String col = null;
		if(isColumn()) {
			if(getGridId() != null && getColumnId() != null ) {
				if(isAttribute()) {
					JoinColumn column = new JoinColumn(getGridType(), getGridId(), getColumnId(), 
							getDimensionId(), getDimensionName(), DimensionType.ATTRIBUTE, getDataSourceType());
					col = column.getDbColName(true, false);
				}
				else if(isMeasure()) {
					JoinColumn column = new JoinColumn(getGridType(), getGridId(), getColumnId(), 
							getDimensionId(), getDimensionName(), DimensionType.MEASURE, getDataSourceType());
					column.setDimensionFunction(measureFunction);
					col = column.getDbColName(true, false, withMeasureFunction);
				}
				else if(isPeriod()) {
					JoinColumn column = new JoinColumn(getGridType(), getGridId(), getColumnId(),
							getDimensionId(), getDimensionName(), DimensionType.PERIOD, getDataSourceType());
					col = column.getDbColName(true, false);
					if(dateValue != null && dateValue.getDateNumber() != 0 && dateValue.getDateGranularity() != null) {
						col = col + " " + dateValue.getDateSign() + " INTERVAL '" + dateValue.getDateNumber() + " " + dateValue.getDateGranularity().name() + "'";
					}
				}
			}
		}
		return col;
	}
	
	@JsonIgnore
	public String getCustomCopyCol(Join join, Map<String, Object> parameters, ISpotService spotService, Map<String, Object> variableValues) {
		String col = null;
		if(isCopy() && getColumnId() != null) {
			JoinColumn column = join.getColumnByOid(getColumnId());	
			if(column != null && column.isCustom()) {
				col = column.getCustomCol(false, parameters, join, spotService, variableValues);
			}
			else if(column != null) {
				col = column.getDbColName(true, false, true);
			}
			col = StringUtils.hasText(col) ? "(" + col + ")" : col;
		}
		return col;
	}
	
	@JsonIgnore
	public String getVariableName() {		
		if (isPeriod() && dateValue != null ) {			
			return dateValue.getVariableName();
		}
		return stringValue;
	}
	
	@JsonIgnore
	public Object getFreeValue() {
		if (isMeasure()) {
			return decimalValue;
		}
		if (isAttribute()) {
			return stringValue;
		}
		if (isPeriod()) {			
			return buildDateValue();
		}
		return null;
	}
	
	@JsonIgnore
	public Object getFreeValue(DimensionType type) {
		if (type == DimensionType.MEASURE) {
			return decimalValue;
		}
		if (type == DimensionType.ATTRIBUTE) {
			return stringValue;
		}
		if (type == DimensionType.PERIOD) {			
			return buildDateValue();
		}
		return null;
	}
	
	@JsonIgnore
	public Object getVariableValue(Map<String, Object> variableValues) {
		String variableName = getVariableName();
		if (isPeriod() && dateValue != null) {			
			return dateValue.buildDynamicDate(variableValues);
		}
		return variableValues.get(variableName);
	}
		
	private Date buildDateValue() {	
		Date date= null;
		if (dateValue != null) date = dateValue.buildDynamicDate();
		return date;
	}
	
	
	@JsonIgnore
	public String getGroupByCols(Join join) {
		String col = null;
		if(isCopy() && getColumnId() != null) {
			JoinColumn column = join.getColumnByOid(getColumnId());	
			if(column != null) {
				col = column.getGroupByCols(join);
			}
		}
		else if(isColumn()) {
			if(getGridId() != null && getColumnId() != null && getDimensionId() != null) {
				if(isAttribute()) {
					JoinColumn column = new JoinColumn(getGridType(), getGridId(), getColumnId(), 
							getDimensionId(), getDimensionName(), DimensionType.ATTRIBUTE, getDataSourceType());
					col = column.getGroupByCols(join);
				}
				else if(isPeriod()) {
					JoinColumn column = new JoinColumn(getGridType(), getGridId(), getColumnId(),
							getDimensionId(), getDimensionName(), DimensionType.PERIOD, getDataSourceType());
					col = column.getGroupByCols(join);
				}
			}
		}
		return col;
	}
	
	@Override
	public Persistent copy() {
		JoinColumnField copy = new JoinColumnField();
		copy.setColumnId(getColumnId());
		copy.setDateValue(getDateValue());
		copy.setDecimalValue(getDecimalValue());
		copy.setDimensionId(getDimensionId());
		copy.setDimensionName(getDimensionName());
		copy.setDimensionType(getDimensionType());
		copy.setEndPosition(getEndPosition());
		copy.setGridId(getGridId());
		copy.setGridType(getGridType());
		copy.setSpot(getSpot());
		copy.setStartPosition(getStartPosition());
		copy.setStringValue(getStringValue());
		copy.setType(getType());
		
		return copy;
	}
	
	public boolean updateCopy(Join copy, HashMap<Long, JoinColumn> columns) {
		boolean updated = false;
		if(isCopy()) {				
			JoinColumn col = columns.get(getColumnId());
			setColumnId(col != null ? col.getId() : null);
			updated = true;
		}
		return updated;
	}
	

}

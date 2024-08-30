/**
 * 
 */
package com.moriset.bcephal.planification.domain.routine;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Entity(name = "TransformationRoutineField")
@Table(name = "BCP_TRANSFORMATION_ROUTINE_FIELD")
@Data
@EqualsAndHashCode(callSuper = false)
public class TransformationRoutineField extends Persistent {

	private static final long serialVersionUID = 2166017346297288379L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transformation_routine_field_seq")
	@SequenceGenerator(name = "transformation_routine_field_seq", sequenceName = "transformation_routine_field_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	@Enumerated(EnumType.STRING)
	private TransformationRoutineSourceType sourceType;
	
	private String stringValue;
	
	private BigDecimal decimalValue;
	
	@Embedded
	private PeriodValue periodValue;
	
	private Long dimensionId;
	
	@Enumerated(EnumType.STRING)
	private PositionSourceType positionSourceType;
	
	private Long positionDimensionId;
	@Enumerated(EnumType.STRING)
	private DimensionType positionDimensionType;
	
	private Integer positionStart;
	
	private Integer positionEnd;
	
	@ManyToOne @JoinColumn(name = "mapping")
	private TransformationRoutineMapping mapping;
	
	private boolean findIgnoreCase;
	
	private String findOperator;
	
	private boolean findStandaloneOnly;
	
	private boolean replaceFoundCharactersOnly;
	
	private String replaceStringValue;
	
	private BigDecimal replaceDecimalValue;
	
	private String dateFormat;
	private String decimalSeparator;
	private String thousandSeparator;
	
	@AttributeOverrides({
	    @AttributeOverride(name="dateOperator", 	column = @Column(name="replaceDateOperator")),
	    @AttributeOverride(name="dateValue", 		column = @Column(name="replaceDateValue")),
	    @AttributeOverride(name="variableName", 	column = @Column(name="replaceDateVariableName")),
	    @AttributeOverride(name="dateSign", 		column = @Column(name="replaceDateSign")),
	    @AttributeOverride(name="dateNumber", 		column = @Column(name="replaceDateNumber")),
	    @AttributeOverride(name="dateGranularity",	column = @Column(name="replaceDateGranularity"))
	})
	private PeriodValue replacePeriodValue;
	
	private Long replaceDimensionId;
	@Enumerated(EnumType.STRING)
	private DimensionType replaceDimensionType;
	
	
	
	@Transient @JsonIgnore 
	private DataSourceType dataSourceType;
	
	@Transient @JsonIgnore 
	private Long dataSourceId;


	@Override
	public TransformationRoutineField copy() {
		TransformationRoutineField copy = new TransformationRoutineField();
		copy.setSourceType(getSourceType());
		copy.setStringValue(getStringValue());
		copy.setDecimalValue(getDecimalValue());
		copy.setPeriodValue(getPeriodValue().copy());
		copy.setDimensionId(getDimensionId());
		copy.setPositionStart(getPositionStart());
		copy.setPositionEnd(getPositionEnd());
		copy.setPositionDimensionId(positionDimensionId);
		copy.setPositionSourceType(positionSourceType);
		copy.setPositionDimensionType(positionDimensionType);
		copy.setFindIgnoreCase(findIgnoreCase);
		copy.setFindStandaloneOnly(findStandaloneOnly);
		copy.setReplaceDecimalValue(replaceDecimalValue);
		copy.setReplaceDimensionId(replaceDimensionId);
		copy.setReplaceFoundCharactersOnly(replaceFoundCharactersOnly);
		copy.setReplacePeriodValue(replacePeriodValue != null ? replacePeriodValue.copy() : new PeriodValue());
		copy.setReplaceStringValue(replaceStringValue);
		copy.setReplaceDimensionType(replaceDimensionType);
		copy.setDateFormat(dateFormat);
		copy.setDecimalSeparator(getDecimalSeparator());
		copy.setThousandSeparator(getThousandSeparator());
		copy.setMapping(getMapping() != null ? getMapping().copy() : null);
		return copy;
	}
	
	@JsonIgnore
	public String getUniverseColumnName(DimensionType type) {
		if (type.isMeasure()) {
			return dimensionId != null ? new Measure(dimensionId, "", dataSourceType, dataSourceId).getUniverseTableColumnName() : null;
		}
		if (type.isAttribute()) {
			return dimensionId != null ? new Attribute(dimensionId, "", dataSourceType, dataSourceId).getUniverseTableColumnName() : null;
		}
		if (type.isPeriod()) {			
			return dimensionId != null ? new Period(dimensionId, "", dataSourceType, dataSourceId).getUniverseTableColumnName() : null;
		}
		return null;
	}
	
	@JsonIgnore
	public String getPositionUniverseColumnName() {
		if (getPositionDimensionType().isMeasure()) {
			return positionDimensionId != null ? new Measure(positionDimensionId, "", dataSourceType, dataSourceId).getUniverseTableColumnName() : null;
		}
		if (getPositionDimensionType().isAttribute()) {
			return positionDimensionId != null ? new Attribute(positionDimensionId, "", dataSourceType, dataSourceId).getUniverseTableColumnName() : null;
		}
		if (getPositionDimensionType().isPeriod()) {			
			return positionDimensionId != null ? new Period(positionDimensionId, "", dataSourceType, dataSourceId).getUniverseTableColumnName() : null;
		}
		return null;
	}
	
	@JsonIgnore
	public String getReplaceUniverseColumnName() {
		if (getReplaceDimensionType().isMeasure()) {
			return replaceDimensionId != null ? new Measure(replaceDimensionId, "", dataSourceType, dataSourceId).getUniverseTableColumnName() : null;
		}
		if (getReplaceDimensionType().isAttribute()) {
			return replaceDimensionId != null ? new Attribute(replaceDimensionId, "", dataSourceType, dataSourceId).getUniverseTableColumnName() : null;
		}
		if (getReplaceDimensionType().isPeriod()) {			
			return replaceDimensionId != null ? new Period(replaceDimensionId, "", dataSourceType, dataSourceId).getUniverseTableColumnName() : null;
		}
		return null;
	}
	
	@JsonIgnore
	public Object getFreeValue(DimensionType type) {
		if (type.isMeasure()) {
			return decimalValue;
		}
		if (type.isAttribute()) {
			return stringValue;
		}
		if (type.isPeriod()) {			
			return buildDateValue();
		}
		return null;
	}
		
	private Date buildDateValue() {	
		Date date = null;
		if(periodValue != null) {
			return periodValue.buildDynamicDate();
		}
		return date;
	}
}
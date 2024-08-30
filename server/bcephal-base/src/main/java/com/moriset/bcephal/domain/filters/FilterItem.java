/**
 * 
 */
package com.moriset.bcephal.domain.filters;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.FilterItemType;
import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.domain.VariableValue;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@MappedSuperclass
@Data
public abstract class FilterItem implements IPersistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	@JsonIgnore
//	@ManyToOne
//	private Long filter;
	
	@Enumerated(EnumType.STRING)
	private DataSourceType dataSourceType;
	
	private Long dataSourceId;

	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;

	private Long dimensionId;

	private String dimensionName;

	@Enumerated(EnumType.STRING)
	private FilterVerb filterVerb;

	private String openBrackets;

	private String closeBrackets;

	private int position;

	private String formula;
	
	@Column(name = "variable")
	private String variables;
	
	@Enumerated(EnumType.STRING)
	private FilterItemType itemType;
	
	public FilterItem() {
		this.dimensionType = DimensionType.PERIOD;
		this.filterVerb = FilterVerb.AND;
		this.dataSourceType = DataSourceType.UNIVERSE;
	}
	
	public FilterItemType getItemType() {
		if(itemType == null) {
			itemType = FilterItemType.FREE;
		}
		return itemType;
	}

	@JsonIgnore
	public boolean isAttribute() {
		return getDimensionType() != null && getDimensionType().isAttribute();
	}

	@JsonIgnore
	public boolean isMeasure() {
		return getDimensionType() != null && getDimensionType().isMeasure();
	}

	@JsonIgnore
	public boolean isPeriod() {
		return getDimensionType() != null && getDimensionType().isPeriod();
	}
	
	@JsonIgnore
	public boolean isFree() {
		return this.getItemType() == null || this.getItemType().isFree();
	}

	@JsonIgnore
	public boolean isVariable() {
		return this.getItemType() != null && this.getItemType().isVariable();
	}

	@JsonIgnore
	public String getUniverseTableColumnName() {
		if (isAttribute()) {
			return new Attribute(getDimensionId(), getDimensionName(), getDataSourceType(), getDataSourceId()).getUniverseTableColumnName();
		} else if (isMeasure()) {
			return new Measure(getDimensionId(), getDimensionName(), getDataSourceType(), getDataSourceId()).getUniverseTableColumnName();
		} else if (isPeriod()) {
			return new Period(getDimensionId(), getDimensionName(), getDataSourceType(), getDataSourceId()).getUniverseTableColumnName();
		}
		return null;
	}

	public void synchronize(FilterItem filterItem, String formula) {
		setDimensionType(filterItem.getDimensionType());
		setDimensionId(filterItem.getDimensionId());
		setDimensionName(filterItem.getDimensionName());
		setFilterVerb(filterItem.getFilterVerb());
		setOpenBrackets(filterItem.getOpenBrackets());
		setCloseBrackets(filterItem.getCloseBrackets());
		setPosition(filterItem.getPosition());
		setFormula(formula);
	}
	
	protected VariableValue getValue(List<VariableValue> variableValues) {
		if(org.springframework.util.StringUtils.hasText(this.getVariables())) {
			return getValue(variableValues, this.getVariables());
		}
		return null;
	}
	
	protected VariableValue getValue(List<VariableValue> variableValues, String variable) {
		for(VariableValue value : variableValues) {
			if(value.isIgnoreCase() && variable.equalsIgnoreCase(value.getName())) {
				return value;
			}
			else if(variable.equals(value.getName())) {
				return value;
			}
		}
		return null;
	}

}

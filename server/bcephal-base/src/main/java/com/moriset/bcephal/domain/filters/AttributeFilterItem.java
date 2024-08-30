/**
 * 
 */
package com.moriset.bcephal.domain.filters;

import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.VariableValue;
import com.moriset.bcephal.domain.dimension.CalculatedMeasureExcludeFilter;
import com.moriset.bcephal.domain.dimension.DimensionType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@jakarta.persistence.Entity(name = "AttributeFilterItem")
@Table(name = "BCP_ATTRIBUTE_FILTER_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class AttributeFilterItem extends FilterItem {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attribute_filter_item_seq")
	@SequenceGenerator(name = "attribute_filter_item_seq", sequenceName = "attribute_filter_item_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "filter")
	private AttributeFilter filter;

	@Enumerated(EnumType.STRING)
	private AttributeOperator operator;

	private String value;

	private boolean useLink;
	
	private Boolean ignoreCase;

	
	public AttributeFilterItem() {
		super();
		this.ignoreCase = true;
	}
		
	public boolean getIgnoreCase()
	{
		if(ignoreCase == null) {
			ignoreCase = false;
		}
		return ignoreCase;
	}
	
	public void synchronize(AttributeFilterItem filterItem, String formula) {
		super.synchronize(filterItem, formula);
		setOperator(filterItem.getOperator());
		setValue(filterItem.getValue());
		setUseLink(filterItem.isUseLink());
	}

	@JsonIgnore
	public String toSql(List<VariableValue> variableValues, List<CalculatedMeasureExcludeFilter> excludeFilters) {
		String sql = null;
		if (getDimensionId() != null) {
			String open = !StringUtils.hasText(getOpenBrackets()) ? "" : getOpenBrackets();
			String close = !StringUtils.hasText(getCloseBrackets()) ? "" : getCloseBrackets();
			for(CalculatedMeasureExcludeFilter filter : excludeFilters) {
				if(filter.getType() == DimensionType.ATTRIBUTE  && filter.getDimensionId() != null && filter.getDimensionId().equals(getDimensionId())) {
					return open.concat(" true ").concat(close);
				}
			}
			
			String col = getUniverseTableColumnName();
			AttributeOperator operation = getOperator();
			String val = value;
			if(isVariable()) {
				VariableValue value = getValue(variableValues);
				if(value != null) {
					val = value.getStringValue();
				}
				else {
					return null;
				}
			}
			
			if (operation == null) {
				operation = !StringUtils.hasText(val) ? AttributeOperator.NOT_NULL : AttributeOperator.EQUALS;
			}
			String s = operation.buidSql(val);
			sql = col.concat(" ").concat(s);
			if(operation.isNull()) {
				sql = "(".concat(col).concat(" ").concat(s).concat(" OR ").concat(col).concat(" = '')");
			}
			else if(operation.isNotNull()) {
				sql = "(".concat(col).concat(" ").concat(s).concat(" AND ").concat(col).concat(" != '')");
			}
			sql = open.concat(sql).concat(close);
		}
		return sql;
	}
	
	public boolean containsExcludedDimensions(List<CalculatedMeasureExcludeFilter> excludeFilters, List<VariableValue> variableValues) {
		for(CalculatedMeasureExcludeFilter filter : excludeFilters) {
			if(filter.getType() == DimensionType.ATTRIBUTE  && filter.getDimensionId() != null && filter.getDimensionId().equals(getDimensionId())) {
				if(isVariable()) {
					VariableValue value = getValue(variableValues);
					if(value != null) {
						return true;
					}
				}
				else {
					return true;
				}
			}
		}
		return false;
	}

	@JsonIgnore
	public AttributeFilterItem copy() {
		AttributeFilterItem copy = new AttributeFilterItem();
		copy.setOperator(operator);
		copy.setValue(value);
		copy.setItemType(getItemType());
		copy.setVariables(getVariables());
		copy.setUseLink(useLink);	
		copy.setDataSourceId(getDataSourceId());
		copy.setDataSourceType(getDataSourceType());
		copy.setDimensionType(getDimensionType());		
		copy.setDimensionId(getDimensionId());		
		copy.setDimensionName(getDimensionName());		
		copy.setFilterVerb(getFilterVerb());		
		copy.setOpenBrackets(getOpenBrackets());		
		copy.setCloseBrackets(getCloseBrackets());		
		copy.setPosition(getPosition());		
		copy.setFormula(getFormula());	
		copy.setItemType(getItemType());
		return copy;
	}

}

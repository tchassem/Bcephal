/**
 * 
 */
package com.moriset.bcephal.domain.filters;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.VariableValue;
import com.moriset.bcephal.domain.dimension.CalculatedMeasureExcludeFilter;
import com.moriset.bcephal.domain.dimension.DimensionType;

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
@jakarta.persistence.Entity(name = "MeasureFilterItem")
@Table(name = "BCP_MEASURE_FILTER_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class MeasureFilterItem extends FilterItem {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "measure_filter_item_seq")
	@SequenceGenerator(name = "measure_filter_item_seq", sequenceName = "measure_filter_item_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "filter")
	private MeasureFilter filter;

	private String operator;

	private BigDecimal value;

	
	public MeasureFilterItem() {
		super();
	}

	public void synchronize(MeasureFilterItem filterItem, String formula) {
		super.synchronize(filterItem, formula);
		setOperator(filterItem.getOperator());
		setValue(filterItem.getValue());
	}

	@JsonIgnore
	public String toSql(List<VariableValue> variableValues, List<CalculatedMeasureExcludeFilter> excludeFilters) {
		String sql = null;		
		if (getDimensionId() != null && StringUtils.hasText(getOperator())) {		
			String open = !StringUtils.hasText(getOpenBrackets()) ? "" : getOpenBrackets();
			String close = !StringUtils.hasText(getCloseBrackets()) ? "" : getCloseBrackets();
			for(CalculatedMeasureExcludeFilter filter : excludeFilters) {
				if(filter.getType() == DimensionType.MEASURE  && filter.getDimensionId() != null && filter.getDimensionId().equals(getDimensionId())) {
					return open.concat(" true ").concat(close);
				}
			}
			
			BigDecimal amount = value != null ? value : BigDecimal.ZERO;
			if(isVariable()) {
				VariableValue value = getValue(variableValues);
				if(value != null) {
					amount = value.getDecimalValue();
				}
				else {
					return null;
				}
			}
			String col = getUniverseTableColumnName();			
			sql = col.concat(" ").concat(getOperator()).concat(" ").concat(amount.toString());
			if (amount.compareTo(BigDecimal.ZERO) == 0 && (getOperator().equals(MeasureOperator.EQUALS)
					|| getOperator().equals(MeasureOperator.GRETTER_OR_EQUALS)
					|| getOperator().equals(MeasureOperator.LESS_OR_EQUALS))) {
				sql = "(".concat(sql).concat(" OR ").concat(col).concat(" IS NULL)");
			}
			sql = open.concat(sql).concat(close);
		}
		return sql;
	}
	
	public boolean containsExcludedDimensions(List<CalculatedMeasureExcludeFilter> excludeFilters, List<VariableValue> variableValues) {
		for(CalculatedMeasureExcludeFilter filter : excludeFilters) {
			if(filter.getType() == DimensionType.MEASURE  && filter.getDimensionId() != null && filter.getDimensionId().equals(getDimensionId())) {
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
	public MeasureFilterItem copy() {
		MeasureFilterItem copy = new MeasureFilterItem();
		copy.setOperator(operator);
		copy.setValue(value);	
		copy.setItemType(getItemType());
		copy.setVariables(getVariables());	
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

/**
 * 
 */
package com.moriset.bcephal.domain.filters;

import java.math.BigDecimal;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

/**
 * @author Joseph Wambo
 *
 */
@jakarta.persistence.Entity(name = "SpotFilterItem")
@Table(name = "BCP_SPOT_FILTER_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class SpotFilterItem extends FilterItem {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "spot_filter_item_seq")
	@SequenceGenerator(name = "spot_filter_item_seq", sequenceName = "spot_filter_item_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "filter")
	private SpotFilter filter;

	private String operator;

	private BigDecimal value;

	public SpotFilterItem() {
		super();
	}

	public void synchronize(SpotFilterItem filterItem, String formula) {
		super.synchronize(filterItem, formula);
		setOperator(filterItem.getOperator());
		setValue(filterItem.getValue());
	}

	@JsonIgnore
	public String toSql() {
		String sql = null;
		if (getDimensionId() != null && StringUtils.hasText(getOperator())) {
			BigDecimal amount = value != null ? value : BigDecimal.ZERO;
			String col = getUniverseTableColumnName();
			String open = !StringUtils.hasText(getOpenBrackets()) ? "" : getOpenBrackets();
			String close = !StringUtils.hasText(getCloseBrackets()) ? "" : getCloseBrackets();
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

	public SpotFilterItem copy() {
		SpotFilterItem copy = new SpotFilterItem();
		copy.setOperator(operator);
		copy.setValue(value);	
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
		return copy;
	}

}

/**
 * 
 */
package com.moriset.bcephal.domain.filters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
@jakarta.persistence.Entity(name = "PeriodFilterItem")
@Table(name = "BCP_PERIOD_FILTER_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class PeriodFilterItem extends FilterItem {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "period_filter_item_seq")
	@SequenceGenerator(name = "period_filter_item_seq", sequenceName = "period_filter_item_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "filter")
	private PeriodFilter filter;

	@Enumerated(EnumType.STRING)
	private PeriodOperator operator;

	private String comparator;

	private Date value;

	private String sign;

	private int number;

	@Enumerated(EnumType.STRING)
	private PeriodGranularity granularity;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JoinColumn(name = "calendarId")
	private PeriodFilterItemCalendar calendar;

	
	public PeriodFilterItem() {
		super();
		this.comparator = MeasureOperator.EQUALS;
		this.sign = "+";
		this.operator = PeriodOperator.SPECIFIC;
		this.granularity = PeriodGranularity.DAY;
	}

	public void synchronize(PeriodFilterItem filterItem, String formula) {
		super.synchronize(filterItem, formula);
		setOperator(filterItem.getOperator());
		setValue(filterItem.getValue());
		setComparator(filterItem.getComparator());
		setSign(filterItem.getSign());
		setNumber(filterItem.getNumber());
	}

	@JsonIgnore
	public String toSql(List<VariableValue> variableValues, List<CalculatedMeasureExcludeFilter> excludeFilters) {
		String sql = null;

		if (getDimensionId() != null && StringUtils.hasText(getComparator())) {
			String open = !StringUtils.hasText(getOpenBrackets()) ? "" : getOpenBrackets();
			String close = !StringUtils.hasText(getCloseBrackets()) ? "" : getCloseBrackets();
			for(CalculatedMeasureExcludeFilter filter : excludeFilters) {
				if(filter.getType() == DimensionType.PERIOD  && filter.getDimensionId() != null && filter.getDimensionId().equals(getDimensionId())) {
					return open.concat(" true ").concat(close);
				}
			}
			
			String col = getUniverseTableColumnName();
			

			if (this.isCalendar() && this.calendar != null) {
				sql = this.calendar.asSql(col, getComparator());
				return sql.trim().isEmpty() ? null : sql;
			}
			if (getComparator().equalsIgnoreCase("NULL")) {
				sql = col.concat(" IS NULL");
				sql = open.concat(sql).concat(close);
				return sql;
			}
			String period = builDateAsString(variableValues);
			if (StringUtils.hasText(period)) {
				sql = col.concat(" ").concat(getComparator()).concat(" '").concat(period).concat("'");
				// if(amount.compareTo(BigDecimal.ZERO) == 0
				// && (getOperator().equals(MeasureOperator.EQUALS)
				// || getOperator().equals(MeasureOperator.GRETTER_OR_EQUALS)
				// || getOperator().equals(MeasureOperator.LESS_OR_EQUALS))){
				// sql = "(".concat(sql).concat(" OR ").concat(col).concat(" IN NULL)");
				// }
				sql = open.concat(sql).concat(close);
			}
		}
		return sql;
	}

	@JsonIgnore
	public String builDateAsString(List<VariableValue> variableValues) {
		Date date = value;
		if(isVariable()) {
			VariableValue value = getValue(variableValues);
			if(value != null) {
				date = value.getPeriodValue();
			}
			else {
				return null;
			}
		}
		PeriodOperator operator = getOperator() != null ? getOperator() : PeriodOperator.SPECIFIC;
		if (!operator.isSpecific()) {
			date = builDynamicDate(operator);
		}
		date = builDynamicDate(date, getSign(), getNumber(), getGranularity());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return date != null ? formatter.format(date) : null;
	}

	@JsonIgnore
	private Date builDynamicDate(PeriodOperator operator) {
		Date date = null;
		Calendar calendar = Calendar.getInstance();
		if (operator.isToday()) {
			date = calendar.getTime();
		} else if (operator.isBeginOfWeek()) {
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			date = calendar.getTime();
		} else if (operator.isEndOfWeek()) {
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			date = calendar.getTime();
		}

		else if (operator.isBeginOfMonth()) {
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			date = calendar.getTime();
		} else if (operator.isEndOfMonth()) {
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			date = calendar.getTime();
		}

		else if (operator.isBeginOfYear()) {
			calendar.set(Calendar.DAY_OF_YEAR, 1);
			date = calendar.getTime();
		} else if (operator.isEndOfYear()) {
			calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
			date = calendar.getTime();
		}
		return date;
	}

	@JsonIgnore
	public Date builDynamicDate(Date date, String sign, int number, PeriodGranularity granularity) {
		if (date != null && StringUtils.hasText(sign) && number != 0 && granularity != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			boolean isAdd = sign.trim().equals("+");
			boolean isSubstract = sign.trim().equals("-");
			int value = isAdd ? number : isSubstract ? -number : 0;
			if (granularity.isDay()) {
				calendar.add(Calendar.DAY_OF_MONTH, value);
			} else if (granularity.isWeek()) {
				calendar.add(Calendar.WEEK_OF_MONTH, value);
			} else if (granularity.isMonth()) {
				calendar.add(Calendar.MONTH, value);
			} else if (granularity.isYear()) {
				calendar.add(Calendar.YEAR, value);
			}
			return calendar.getTime();
		}
		return date;
	}

	@JsonIgnore
	public boolean isToday() {
		return operator != null && operator.isToday();
	}

	@JsonIgnore
	public boolean isBeginWeek() {
		return operator != null && operator.isBeginOfWeek();
	}

	@JsonIgnore
	public boolean isBeginMonth() {
		return operator != null && operator.isBeginOfMonth();
	}

	@JsonIgnore
	public boolean isBeginYear() {
		return operator != null && operator.isBeginOfYear();
	}

	@JsonIgnore
	public boolean isEndWeek() {
		return operator != null && operator.isEndOfWeek();
	}

	@JsonIgnore
	public boolean isEndMonth() {
		return operator != null && operator.isEndOfMonth();
	}

	@JsonIgnore
	public boolean isEndYear() {
		return operator != null && operator.isEndOfYear();
	}

	@JsonIgnore
	public boolean isSpecific() {
		return operator != null && operator.isSpecific();
	}

	@JsonIgnore
	public boolean isCalendar() {
		return operator != null && operator.isCalendar();
	}
	
	public boolean containsExcludedDimensions(List<CalculatedMeasureExcludeFilter> excludeFilters, List<VariableValue> variableValues) {
		for(CalculatedMeasureExcludeFilter filter : excludeFilters) {
			if(filter.getType() == DimensionType.PERIOD  && filter.getDimensionId() != null && filter.getDimensionId().equals(getDimensionId())) {
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
	public PeriodFilterItem copy() {
		PeriodFilterItem copy = new PeriodFilterItem();
		copy.setOperator(operator);
		copy.setValue(value);
		copy.setItemType(getItemType());
		copy.setVariables(getVariables());	
		copy.setComparator(comparator);
		copy.setSign(sign);
		copy.setNumber(number);
		copy.setGranularity(granularity);	
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
		copy.setCalendar(calendar != null ? calendar.copy() : null);	
		copy.setItemType(getItemType());
		return copy;
	}

}

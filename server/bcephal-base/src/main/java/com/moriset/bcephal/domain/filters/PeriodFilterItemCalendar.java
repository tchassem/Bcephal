/**
 * 
 */
package com.moriset.bcephal.domain.filters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.domain.dimension.CalendarCategory;
import com.moriset.bcephal.domain.dimension.CalendarDay;

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
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@SuppressWarnings("serial")
@jakarta.persistence.Entity(name = "PeriodFilterItemCalendar")
@Table(name = "BCP_PERIOD_FILTER_ITEM_CALENDAR")
@Data
@EqualsAndHashCode(callSuper = false)
public class PeriodFilterItemCalendar implements IPersistent {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "period_filter_item_calendar_seq")
	@SequenceGenerator(name = "period_filter_item_calendar_seq", sequenceName = "period_filter_item_calendar_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private boolean category;

	private Long categoryId;

	private String categoryName;

	@Transient
	@JsonIgnore
	private CalendarCategory calendar;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JoinColumn(name = "dayId")
	private CalendarDay day;

	public String asSql(String col, String operation) {
		String sql = "";
		if (isCategory() && calendar != null) {
			sql = calendar.asSql(col, operation);
		} else if (!isCategory() && day != null) {
			sql = day.asSql(col, operation);
		}
		return sql;
	}

	public PeriodFilterItemCalendar copy() {
		PeriodFilterItemCalendar copy = new PeriodFilterItemCalendar();
		copy.setCategory(category);
		copy.setCategoryId(categoryId);
		copy.setCategoryName(categoryName);
		copy.setDay(day);
		return null;
	}

}

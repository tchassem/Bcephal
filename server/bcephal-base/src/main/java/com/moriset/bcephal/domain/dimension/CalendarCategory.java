/**
 * 
 */
package com.moriset.bcephal.domain.dimension;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.domain.ListChangeHandler;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "SlimCalendarCategory")
@Table(name = "BCP_CALENDAR_CATEGORY")
@Data
@EqualsAndHashCode(callSuper = false)
public class CalendarCategory implements IPersistent {

	private static final long serialVersionUID = -5263331164484291343L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calendar_category_seq")
	@SequenceGenerator(name = "calendar_category_seq", sequenceName = "calendar_category_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	public String name;

	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "category")
	private List<CalendarDay> days;

	@Transient
	private ListChangeHandler<CalendarDay> dayListChangeHandler;

	@Transient
	private ListChangeHandler<CalendarCategory> children;

	public CalendarCategory() {
		days = new ArrayList<CalendarDay>();
		dayListChangeHandler = new ListChangeHandler<CalendarDay>();
		children = new ListChangeHandler<CalendarCategory>();
	}

	public void setDays(List<CalendarDay> days) {
		this.days = days;
		dayListChangeHandler.setOriginalList(days);
	}

	@PostLoad
	public void initListChangeHandler() {
		dayListChangeHandler.setOriginalList(days);
	}

	@JsonIgnore
	public String asSql(String column, String operation) {
		String sql = "";
		String or = "";
		for (CalendarDay day : dayListChangeHandler.getItems()) {
			String itemSql = day.asSql(column, operation);
			if (itemSql != null && !itemSql.isEmpty()) {
				sql += or + itemSql;
				or = " OR ";
			}
		}
		return sql.isEmpty() ? sql : "(" + sql + ")";
	}

}

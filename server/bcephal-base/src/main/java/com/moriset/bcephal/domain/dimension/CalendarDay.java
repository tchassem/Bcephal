/**
 * 
 */
package com.moriset.bcephal.domain.dimension;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.IPersistent;

import jakarta.persistence.Entity;
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
@Entity(name = "Calendar_Day")
@Table(name = "BCP_CALENDAR_DAY")
@Data
@EqualsAndHashCode(callSuper = false)
public class CalendarDay implements IPersistent {

	private static final long serialVersionUID = -4619962413556563205L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calendar_day_seq")
	@SequenceGenerator(name = "calendar_day_seq", sequenceName = "calendar_day_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category")
	private CalendarCategory category;

	@Enumerated(EnumType.STRING)
	private CalendarDayType dayType;

	private Integer day;

	private Integer month;

	private Integer year;

	private int position;

	@JsonIgnore
	public String asSql(String column, String operation) {
		String sql = "";
		if (dayType == null) {
			return sql;
		}

		if (dayType == CalendarDayType.FIXED_DATE) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			Date result = calendar.getTime();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			String sDate = format.format(result);
			return "(" + column + " " + operation + " '" + Long.valueOf(sDate) + "')";
		}

		String and = "";
		if (year != null) {
			String op = "=";
			if (dayType == CalendarDayType.YEAR) {
				op = operation;
			}
			sql += and + "EXTRACT (YEAR FROM " + column + ") " + op + " " + year;
			and = " AND ";
		}
		if (month != null) {
			String op = "=";
			if (dayType == CalendarDayType.MONTH) {
				op = operation;
			}
			sql += and + "EXTRACT (YEAR MONTH " + column + ") " + op + " " + month;
			and = " AND ";
		}
		if (day != null) {
			if (dayType == CalendarDayType.DAY_OF_WEEK) {
				sql += and + "EXTRACT (ISODOW FROM " + column + ") " + operation + " " + day;
				and = " AND ";
			} else {
				sql += and + "EXTRACT (DAY FROM " + column + ") " + operation + " " + day;
				and = " AND ";
			}
		}

		return sql.isEmpty() ? sql : "(" + sql + ")";
	}

}

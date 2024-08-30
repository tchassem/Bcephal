/**
 * 
 */
package com.moriset.bcephal.domain.filters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.utils.JsonDateDeserializer;
import com.moriset.bcephal.utils.JsonDateSerializer;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author Moriset
 *
 */
@Data
@Builder
@AllArgsConstructor
@Embeddable
public class PeriodValue {

	@Enumerated(EnumType.STRING)
	private PeriodOperator dateOperator;

	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateValue;

	private String dateSign;

	private int dateNumber;

	@Enumerated(EnumType.STRING)
	private PeriodGranularity dateGranularity;
	
	private String variableName;

	public PeriodValue() {
		this.dateOperator = PeriodOperator.SPECIFIC;
		this.dateGranularity = PeriodGranularity.DAY;
	}

	@JsonIgnore
	public boolean isToday() {
		return dateOperator.isToday();
	}

	@JsonIgnore
	public boolean isBeginOfWeek() {
		return dateOperator.isBeginOfWeek();
	}

	@JsonIgnore
	public boolean isBeginOfMonth() {
		return dateOperator.isBeginOfMonth();
	}

	@JsonIgnore
	public boolean isBeginOfYear() {
		return dateOperator.isBeginOfYear();
	}

	@JsonIgnore
	public boolean isEndOfWeek() {
		return dateOperator.isEndOfWeek();
	}

	@JsonIgnore
	public boolean isEndOfMonth() {
		return dateOperator.isEndOfMonth();
	}

	@JsonIgnore
	public boolean isEndOfYear() {
		return dateOperator.isEndOfYear();
	}

	@JsonIgnore
	public boolean isSpecific() {
		return dateOperator.isSpecific();
	}
	
	@JsonIgnore
	public boolean isVariable() {
		return dateOperator.isVariable();
	}

	@JsonIgnore
	public boolean isCalendar() {
		return dateOperator.isCalendar();
	}
	
	@JsonIgnore
	public boolean isCopyDate() {
		return dateOperator.isCopyDate();
	}
	
	public Date buildDynamicDate(Map<String, Object> variableValues) {
		Calendar calendar = Calendar.getInstance();
		Date date = dateValue;
		if (this.isToday()) {
			date = calendar.getTime();
		} else if (this.isBeginOfWeek()) {
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			date = calendar.getTime();
		} else if (this.isBeginOfMonth()) {
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			date = calendar.getTime();
		} else if (this.isBeginOfYear()) {
			calendar.set(Calendar.DAY_OF_YEAR, 1);
			date = calendar.getTime();
		} else if (this.isEndOfWeek()) {
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			calendar.add(Calendar.DAY_OF_WEEK, 6);
			date = calendar.getTime();
		} else if (this.isEndOfMonth()) {
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			date = calendar.getTime();
		} else if (this.isEndOfYear()) {
			calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
			date = calendar.getTime();
		} else if (this.isCalendar()) {
			
		} else if (this.isVariable() && StringUtils.hasText(variableName)){
			if(variableValues != null && variableValues.containsKey(variableName)) {
				Object value = variableValues.get(variableName);
				if(value != null) {
					if(value instanceof Date) {
						date = (Date)value;
					}
					else if(value instanceof VariableIntervalPeriod) {
						date = ((VariableIntervalPeriod)value).getStart();
					}	
				}
			}
			else return null;
		} 
		
		if (this.dateGranularity != null && date != null) {
			calendar.setTime(date);
			boolean isAdd = PeriodSign.isAdd(dateSign);
			boolean isSub = PeriodSign.isSubstract(dateSign);
			if (this.dateGranularity.isMonth()) {
				if (isAdd) {
					calendar.add(Calendar.MONTH, this.dateNumber);
				}
				if (isSub) {
					calendar.add(Calendar.MONTH, -this.dateNumber);
				}
				date = calendar.getTime();
			} else if (this.dateGranularity.isWeek()) {
				if (isAdd) {
					calendar.add(Calendar.WEEK_OF_MONTH, this.dateNumber);
				}
				if (isSub) {
					calendar.add(Calendar.WEEK_OF_MONTH, -this.dateNumber);
				}
				date = calendar.getTime();
			} else if (this.dateGranularity.isYear()) {
				if (isAdd) {
					calendar.add(Calendar.YEAR, this.dateNumber);
				}
				if (isSub) {
					calendar.add(Calendar.YEAR, -this.dateNumber);
				}
				date = calendar.getTime();
			} else if (this.dateGranularity.isDay()) {
				if (isAdd) {
					calendar.add(Calendar.DAY_OF_WEEK, this.dateNumber);
				}
				if (isSub) {
					calendar.add(Calendar.DAY_OF_WEEK, -this.dateNumber);
				}
				date = calendar.getTime();
			}
		}
		return date;
	}

	@JsonIgnore
	public Date buildDynamicDate() {		
		return buildDynamicDate(new HashMap<>());
	}
	
	public String buildDynamicDate(String col) {
		String date = col;		
		if (this.dateGranularity != null && date != null) {
			date += " " + dateSign + " INTERVAL '" + dateNumber + " " + this.dateGranularity.name() + "'";
			date = "to_date(to_char(" + date + ", 'YYYY-MM-DD'), 'YYYY-MM-DD')";
		}
		return date;
	}

	public PeriodValue copy() {
		PeriodValue copy = new PeriodValue();
		copy.setDateOperator(getDateOperator());
		copy.setDateGranularity(getDateGranularity());
		copy.setDateNumber(getDateNumber());
		copy.setDateSign(getDateSign());
		copy.setDateValue(getDateValue());
		copy.setVariableName(variableName);
		return copy;
	}

	public String asDbString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String sDate = format.format(date);
		return sDate;
	}
}

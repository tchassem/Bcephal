package com.moriset.bcephal.dashboard.domain;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.PeriodGranularity;
import com.moriset.bcephal.domain.filters.PeriodOperator;
import com.moriset.bcephal.domain.filters.PeriodValue;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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

@jakarta.persistence.Entity(name = "UniverseDynamicFilterItem")
@Table(name = "BCP_UNIVERSE_DYNAMIC_FILTER_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class UniverseDynamicFilterItem extends Persistent {

	private static final long serialVersionUID = -3079719553173376765L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "universe_dynanic_filter_item_seq")
	@SequenceGenerator(name = "universe_dynanic_filter_item_seq", sequenceName = "universe_dynanic_filter_item_seq	", initialValue = 1, allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "filter")
	private UniverseDynamicFilter filter;
	
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;

	private Long dimensionId;
	
	private String dimensionName;

	private String label;
	
	private int position;
	
	private String values;
	
	@Enumerated(EnumType.STRING)
	private PeriodGranularity granularity;
	
	
	@AttributeOverrides({
	    @AttributeOverride(name="dateOperator", 	column = @Column(name="startDateOperator")),
	    @AttributeOverride(name="dateValue", 		column = @Column(name="startDateValue")),
	    @AttributeOverride(name="variableName", 	column = @Column(name="startDateVariableName")),
	    @AttributeOverride(name="dateSign", 		column = @Column(name="startDateSign")),
	    @AttributeOverride(name="dateNumber", 		column = @Column(name="startDateNumber")),
	    @AttributeOverride(name="dateGranularity",	column = @Column(name="startDateGranularity"))
	})
	@Embedded	
	private PeriodValue startDateValue;
	
	@AttributeOverrides({
	    @AttributeOverride(name="dateOperator", 	column = @Column(name="endDateOperator")),
	    @AttributeOverride(name="dateValue", 		column = @Column(name="endDateValue")),
	    @AttributeOverride(name="variableName", 	column = @Column(name="endDateVariableName")),
	    @AttributeOverride(name="dateSign", 		column = @Column(name="endDateSign")),
	    @AttributeOverride(name="dateNumber", 		column = @Column(name="endDateNumber")),
	    @AttributeOverride(name="dateGranularity",	column = @Column(name="endDateGranularity"))
	})
	@Embedded	
	private PeriodValue endDateValue;
	
	
	
	public UniverseDynamicFilterItem() {
		this.dimensionType = DimensionType.ATTRIBUTE;
		this.granularity = PeriodGranularity.DAY;
		this.startDateValue = new PeriodValue();
		this.endDateValue = new PeriodValue();
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
	
	
	public LinkedHashMap<String, List<Date>> getDateValues(){
		LinkedHashMap<String, List<Date>> values = new LinkedHashMap<>();	
		if(isPeriod()) {
		try {
		    Date startDate = startDateValue.buildDynamicDate();
		    Date endDate = endDateValue.buildDynamicDate();
		    Date currentDate = startDate;
		    int offset = 0;
			while(currentDate != null && endDate != null && currentDate.compareTo(endDate) <= 0) {
				if(granularity != null && granularity.isDay()) {
					values.put(label + "\n DAY " + offset, Arrays.asList(currentDate, currentDate));
					currentDate = builNextDay(currentDate, 1);
				}else 
				  if(granularity != null && granularity.isWeek()) {
					  Date stDate = builDynamicDate(currentDate, PeriodOperator.BEGIN_WEEK);
					  Date enDate = builDynamicDate(currentDate, PeriodOperator.END_WEEK);
					  values.put(label + "\n WEEK " + offset, Arrays.asList(stDate, enDate));
					  currentDate = builNextDay(enDate, 1);
				}else 
				  if(granularity != null && granularity.isMonth()) {
					  Date stDate = builDynamicDate(currentDate, PeriodOperator.BEGIN_MONTH);
					  Date enDate = builDynamicDate(currentDate, PeriodOperator.END_MONTH);
					  values.put(label + "\n MONTH " + offset, Arrays.asList(stDate, enDate));
					  currentDate = builNextDay(enDate, 1);
				} else 
				  if(granularity != null && granularity.isQuarter()) {
					  Date stDate = builDynamicDate(currentDate, PeriodOperator.BEGIN_MONTH);
					  Date enDate = builNextMonth(stDate, 4);
					   enDate = builDynamicDate(enDate, PeriodOperator.END_MONTH);
					  values.put(label + "\n QUATER " + offset, Arrays.asList(stDate, enDate));
					  currentDate = builNextDay(enDate, 1);
				}else 
				  if(granularity != null && granularity.isYear()) {
					  Date stDate = builDynamicDate(currentDate, PeriodOperator.BEGIN_YEAR);
					  Date enDate = builDynamicDate(currentDate, PeriodOperator.END_YEAR);
					  values.put(label + "\n YEAR " + offset, Arrays.asList(stDate, enDate));
					  currentDate = builNextDay(enDate, 1);
				}
				offset++;
			}
		}catch (Exception e) {
		}
		}
		return values;
	}
	
	@JsonIgnore
	private Date builDynamicDate(Date date_, PeriodOperator operator) {
		Date date = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date_);
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
	
	private Date builNextDay(Date date_, int day) {
		Date date = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date_);
		calendar.add(Calendar.DAY_OF_YEAR, day);
		date = calendar.getTime();
		return date;
	}
	
	private Date builNextMonth(Date date_, int month) {
		Date date = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date_);
		calendar.add(Calendar.MONTH, month);
		date = calendar.getTime();
		return date;
	}
	
	@Override
	public UniverseDynamicFilterItem copy() {
		UniverseDynamicFilterItem copy = new UniverseDynamicFilterItem();		
		copy.setDimensionType(dimensionType);
		copy.setDimensionId(dimensionId);
		copy.setDimensionName(dimensionName);
		copy.setLabel(label);
		copy.setPosition(position);
		copy.setValues(values);
		copy.setGranularity(granularity);
		copy.setStartDateValue(startDateValue != null ? startDateValue.copy() : new PeriodValue());
		copy.setEndDateValue(endDateValue != null ? endDateValue.copy() : new PeriodValue());		
		return copy;
	}
	
}

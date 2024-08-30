package com.moriset.bcephal.dashboard.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.domain.filters.PeriodFilterItem;
import com.moriset.bcephal.domain.filters.PeriodGranularity;
import com.moriset.bcephal.domain.filters.PeriodOperator;

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

@SuppressWarnings("serial")
@jakarta.persistence.Entity(name = "DynamicPeriodFilterItem")
@Table(name = "BCP_DYNAMIC_PERIOD_FILTER_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class DynamicPeriodFilterItem implements IPersistent {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dync_period_filter_item_seq")
	@SequenceGenerator(name = "dync_period_filter_item_seq", sequenceName = "dync_period_filter_item_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "filter")
	private DynamicPeriodFilter filter;
	
	@Enumerated(EnumType.STRING)
	private PeriodGranularity granularity;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JoinColumn(name = "startPeriodFilter")
	private PeriodFilterItem startPeriodFilter;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JoinColumn(name = "endPeriodFilter")
	private PeriodFilterItem endPeriodFilter;
	
	
	public DynamicPeriodFilterItem(){
		granularity = PeriodGranularity.MONTH;
	}

	public PeriodGranularity getGranularity() {
		if(granularity == null) {
			return PeriodGranularity.MONTH;
		}
		return granularity;
	}
	
	public void setGranularity(PeriodGranularity granularity) {
		if(granularity != null) {
			this.granularity = granularity;
		}
	}
	
	public LinkedHashMap<String, List<Date>> getDateValues(){
		LinkedHashMap<String, List<Date>> values = new LinkedHashMap<>();		
		try {
		    Date startDate = getOrigineDate(startPeriodFilter);
		    Date endDate = getOrigineDate(endPeriodFilter);
		    Date currentDate = startDate;
		    int offset = 0;
			while(currentDate != null && endDate != null && currentDate.compareTo(endDate) <= 0) {
				if(granularity != null && granularity.isDay()) {
					values.put("DAY " + offset, Arrays.asList(currentDate, currentDate));
					currentDate = builNextDay(currentDate, 1);
				}else 
				  if(granularity != null && granularity.isWeek()) {
					  Date stDate = builDynamicDate(currentDate, PeriodOperator.BEGIN_WEEK);
					  Date enDate = builDynamicDate(currentDate, PeriodOperator.END_WEEK);
					  values.put("WEEK " + offset, Arrays.asList(stDate, enDate));
					  currentDate = builNextDay(enDate, 1);
				}else 
				  if(granularity != null && granularity.isMonth()) {
					  Date stDate = builDynamicDate(currentDate, PeriodOperator.BEGIN_MONTH);
					  Date enDate = builDynamicDate(currentDate, PeriodOperator.END_MONTH);
					  values.put("MONTH " + offset, Arrays.asList(stDate, enDate));
					  currentDate = builNextDay(enDate, 1);
				} else 
				  if(granularity != null && granularity.isQuarter()) {
					  Date stDate = builDynamicDate(currentDate, PeriodOperator.BEGIN_MONTH);
					  Date enDate = builNextMonth(stDate, 4);
					   enDate = builDynamicDate(enDate, PeriodOperator.END_MONTH);
					  values.put("QUATER " + offset, Arrays.asList(stDate, enDate));
					  currentDate = builNextDay(enDate, 1);
				}else 
				  if(granularity != null && granularity.isYear()) {
					  Date stDate = builDynamicDate(currentDate, PeriodOperator.BEGIN_YEAR);
					  Date enDate = builDynamicDate(currentDate, PeriodOperator.END_YEAR);
					  values.put("YEAR " + offset, Arrays.asList(stDate, enDate));
					  currentDate = builNextDay(enDate, 1);
				}
				offset++;
			}
		}catch (Exception e) {
		}
		return values;
	}



	@JsonIgnore
	private Date getOrigineDate(PeriodFilterItem startPeriod) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null;
		if(startPeriod != null && startPeriod.getValue() != null) {
			String value = startPeriod.builDateAsString(new ArrayList<>());
			if(StringUtils.hasText(value)) {
				startDate = formatter.parse(value);
			}
		}
		return startDate;
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
	
}

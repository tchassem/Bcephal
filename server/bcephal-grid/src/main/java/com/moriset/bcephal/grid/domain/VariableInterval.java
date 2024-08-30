package com.moriset.bcephal.grid.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.filters.PeriodGranularity;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.domain.filters.VariableIntervalPeriod;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name =  "VariableInterval")
@Table(name = "BCP_VARIABLE_INTERVAL")
@Data
@EqualsAndHashCode(callSuper=false)
public class VariableInterval extends Persistent {

	private static final long serialVersionUID = 2293401653516291565L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "var_interval_seq")
	@SequenceGenerator(name = "var_interval_seq", sequenceName = "var_interval_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@Embedded	
	private PeriodValue startDate;
	
	@Enumerated(EnumType.STRING)
	private PeriodGranularity intervalGranularity;
	
	private int intervalNbr;
	
	private int intervalToRunCount;
	
	@Enumerated(EnumType.STRING)
	private VariableIntervalRanking ranking;
	
	
	public VariableInterval() {
		this.startDate = new PeriodValue();
	}

	@JsonIgnore
	public boolean isOldestToSoonest() {
		return ranking.isOldestToSoonest();
	}

	@JsonIgnore
	public boolean isSoonestToOldest() {
		return ranking.isSoonestToOldest();
	}
	
	public List<VariableIntervalPeriod> buildVariableValues(Map<String, Object> variableValues) {
		List<VariableIntervalPeriod> periods = new ArrayList<>();		
		Date start = startDate.buildDynamicDate(variableValues);
		
		for(int i = 1; i <= intervalToRunCount; i++) {
			Date end = buidEndDate(start);
			periods.add(VariableIntervalPeriod.builder().start(start).end(end).build());
			start = buidNextStartDate(end);
		}
		if(ranking != null && ranking == VariableIntervalRanking.SOONEST_TO_OLDEST) {
			Collections.sort(periods, new Comparator<VariableIntervalPeriod>() {
				@Override
				public int compare(VariableIntervalPeriod o1, VariableIntervalPeriod o2) {
					return o2.getStart().compareTo(o1.getStart());
				}
			});
		}
		return periods;
	}
	
	private Date buidEndDate(Date start) {
		if (this.intervalGranularity != null && start != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(start);
			if (this.intervalGranularity.isMonth()) {
				calendar.add(Calendar.MONTH, this.intervalNbr);
			} else if (this.intervalGranularity.isWeek()) {
				calendar.add(Calendar.WEEK_OF_MONTH, this.intervalNbr);
			} else if (this.intervalGranularity.isYear()) {
				calendar.add(Calendar.YEAR, this.intervalNbr);
			} else if (this.intervalGranularity.isDay()) {
				calendar.add(Calendar.DAY_OF_WEEK, this.intervalNbr);
			}
			calendar.add(Calendar.DAY_OF_WEEK, -1);
			return calendar.getTime();
		}
		return start;
	}
	
	private Date buidNextStartDate(Date end) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(end);
		calendar.add(Calendar.DAY_OF_WEEK, 1);
		return calendar.getTime();
	}
	
	@Override
	public VariableInterval copy() {
		VariableInterval copy = new VariableInterval();
		copy.setStartDate(startDate != null ? startDate.copy() : null);
		copy.setIntervalGranularity(intervalGranularity);
		copy.setIntervalNbr(intervalNbr);
		copy.setIntervalToRunCount(intervalToRunCount);
		copy.setRanking(ranking);
		return copy;
	}

	
}

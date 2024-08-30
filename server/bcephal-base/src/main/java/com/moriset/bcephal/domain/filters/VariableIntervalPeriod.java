package com.moriset.bcephal.domain.filters;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariableIntervalPeriod {

	private Date start;
	
	private Date end;
	
	public boolean isOneDay() {
		return this.start != null && this.start.equals(end);
	}
	
	
}

/**
 * 
 */
package com.moriset.bcephal.scheduler.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Embeddable
public class SchedulerPlannerItemAction {

	@Enumerated(EnumType.STRING) 
	private SchedulerPlannerItemDecision decision;
	
	private Long alarmId;
	
	private String gotoCode;
	
	public SchedulerPlannerItemAction copy() {
		SchedulerPlannerItemAction copy = new SchedulerPlannerItemAction();
		copy.setDecision(getDecision());
		copy.setAlarmId(getAlarmId());
		copy.setGotoCode(getGotoCode());
		return copy;
	}
	
}

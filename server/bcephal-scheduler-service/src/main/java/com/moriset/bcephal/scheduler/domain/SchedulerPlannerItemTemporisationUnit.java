package com.moriset.bcephal.scheduler.domain;

public enum SchedulerPlannerItemTemporisationUnit {

	SECONDE,
	MINUTE,
	HOUR,
	DAY;
	
	SchedulerPlannerItemTemporisationUnit() {
		
	}
	
	public long buildDuration(Integer tempo) {
		long duration = tempo != null ? tempo : 0;
		int time = this == SECONDE ? 1000 
				: this == MINUTE ? 60000 
				: this == HOUR ? 3600000
				: this == DAY ? 3600000 * 24 : 0;
						
		return duration * time;
	}
	
}

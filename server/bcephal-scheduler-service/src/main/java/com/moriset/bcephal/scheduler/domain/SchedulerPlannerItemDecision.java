package com.moriset.bcephal.scheduler.domain;

public enum SchedulerPlannerItemDecision {

	CONTINUE,
	GOTO,
	MESSAGE_WITH_CONFIRM,
	MESSAGE_WITHOUT_CONFIRM,
	RESTART,
	SKIP_NEXT,
	STOP,
	GENERATE_TASK;
	
}

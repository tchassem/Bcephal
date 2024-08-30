/**
 * 
 */
package com.moriset.bcephal.scheduler.service;

import com.moriset.bcephal.scheduler.domain.SchedulerPlannerItemDecision;

/**
 * @author Joseph Wambo
 *
 */
public class ScdulerPlannerRunnerItemAction {

	public SchedulerPlannerItemDecision decision;
	
	public Long objectId;
	
	public String gotoCode;
	
	public ScdulerPlannerRunnerItemAction() {
		
	}
	
	public ScdulerPlannerRunnerItemAction(SchedulerPlannerItemDecision decision) {
		super();
		this.decision = decision;
	}

	public ScdulerPlannerRunnerItemAction(SchedulerPlannerItemDecision decision, Long objectId, String gotoCode) {
		super();
		this.decision = decision;
		this.objectId = objectId;
		this.gotoCode = gotoCode;
	}
	
	
}

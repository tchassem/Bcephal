/**
 * 
 */
package com.moriset.bcephal.scheduler.domain;

import java.time.LocalDateTime;

import org.springframework.scheduling.support.CronExpression;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SchedulerPlannerBrowserData extends BrowserData {
	
	private boolean active;
	
	private boolean scheduled;

	private String cronExpression;
	
	private LocalDateTime nextExecutionTime;
	
	private String state;
	
	public SchedulerPlannerBrowserData(SchedulerPlanner schedulerPlanner) {
		super(schedulerPlanner);
		setGroup(schedulerPlanner.getGroup() != null ? schedulerPlanner.getGroup().getName() : null );
		setActive(schedulerPlanner.isActive());
		setScheduled(schedulerPlanner.isScheduled());
		setConfirmAction(schedulerPlanner.isConfirmAction());
		setCronExpression(schedulerPlanner.getCronExpression());
		buildNextExecutionTime(schedulerPlanner);
	}

	private void buildNextExecutionTime(SchedulerPlanner schedulerPlanner) {
		if(schedulerPlanner.isActive() && schedulerPlanner.isScheduled() && StringUtils.hasText(schedulerPlanner.getCronExpression())) {
			if(CronExpression.isValidExpression(schedulerPlanner.getCronExpression())) {
				CronExpression cronTrigger = CronExpression.parse(schedulerPlanner.getCronExpression());
				nextExecutionTime = cronTrigger.next(LocalDateTime.now());
			}
		}
	}
	
}

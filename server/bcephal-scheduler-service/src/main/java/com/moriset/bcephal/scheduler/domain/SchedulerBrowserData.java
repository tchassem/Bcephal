/**
 * 
 */
package com.moriset.bcephal.scheduler.domain;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.support.CronExpression;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.BrowserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SchedulerBrowserData extends BrowserData {

	private String cron;
	
	private boolean currentlyExecuting;
	
	private String projectCode;	
	
	private String projectName;	
	
	private Long objectId;	
	
	@JsonIgnore
	private String code;	
	
	@JsonIgnore
	private ScheduledFuture<?> future;
	
	public String getState(){
		return future != null ? future.state().name() : null;
	}
	
	public Long getTimeLeftBeforeNextExecution(){
		return future != null ? future.getDelay(TimeUnit.MINUTES) : null;
	}
	
	public LocalDateTime getNextExecutionTime() {        
        CronExpression cronTrigger = CronExpression.parse(cron);
        LocalDateTime next = cronTrigger.next(LocalDateTime.now());
        return next;
    }
	
}

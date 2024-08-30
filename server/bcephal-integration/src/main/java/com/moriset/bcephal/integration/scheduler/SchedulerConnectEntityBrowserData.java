/**
 * 
 */
package com.moriset.bcephal.integration.scheduler;

import java.util.concurrent.ScheduledFuture;

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
public class SchedulerConnectEntityBrowserData extends BrowserData {

	private String cron;
	
	private boolean currentlyExecuting;
	
	private String projectCode;	
	
	private String entityName;	
	
	private Long objectId;	
	
	@JsonIgnore
	private String code;	
	
	@JsonIgnore
	private ScheduledFuture<?> future;
	
}

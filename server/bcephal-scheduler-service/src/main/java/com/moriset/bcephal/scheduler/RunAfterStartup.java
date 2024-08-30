/**
 * 
 */
package com.moriset.bcephal.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.moriset.bcephal.scheduler.service.SchedulerManager;

/**
 * @author Moriset
 *
 */
@Component
public class RunAfterStartup {
	
	@Autowired SchedulerManager manager;
	
	@EventListener(ApplicationReadyEvent.class)
	public void runAfterStartup() {
		manager.startSchedulers();;
	}

}

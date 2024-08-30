package com.moriset.bcephal.scheduler.service.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.scheduler.domain.SchedulerPlanner;
import com.moriset.bcephal.scheduler.service.SchedulerPlannerRunner;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SchedulerApiService {

	@Autowired
	SchedulerPlannerRunner runner;

	
	public void RunSchedulerPlanner(SchedulerPlanner schedulerPlanner,
			String operationCode,
			String username,
			Long subscriptionId,
			String projectCode,
			HttpSession session, 
			String tenantId, 
			TaskProgressListener listener) {		
		Thread thread = new Thread() {
			public void run() {
				try {
					TenantContext.setCurrentTenant(tenantId);
					performAction(schedulerPlanner, operationCode, username, subscriptionId, projectCode, session, listener);
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					
				}
			}
		};
		thread.start();
	}
	
	private void performAction(
			SchedulerPlanner schedulerPlanner,
			String operationCode,
			String username, 
			Long subscriptionId,
			String projectCode,
			HttpSession session, 
			TaskProgressListener listener 
			) {
		
		try {	
			if (schedulerPlanner != null) {
				log.debug("SchedulerPlanner : {}", schedulerPlanner.getName());
				listener.createInfo(schedulerPlanner.getId(), "Running scheduler " + schedulerPlanner.getName());
				runner.setUsername(username);
				runner.setClientId(subscriptionId);
				runner.setSessionId(session.getId());
				runner.setSchedulerPlanner(schedulerPlanner);
				runner.setMode(RunModes.M);
				runner.setProjectCode(projectCode);
				runner.setProjectName(projectCode);
				runner.setSession(session);
				runner.setOperationCode(operationCode);
				//runner.setHttpHeaders(session.g);
				runner.setListener(listener);
				runner.setStop(false);
				runner.run();
			} else {
				log.error("SchedulerPlanner is NULL!");
				listener.error("Unknown scheduler planner.", false);
			}
			if (listener != null) {
				listener.end();
			}
		} catch (Exception e) {
			log.error("unexpected error while running scheduler planner : {}",
					schedulerPlanner != null && schedulerPlanner != null ? schedulerPlanner.getName() : "", e);
			if (listener != null) {
				listener.error(e.getMessage(), true);
			}
		}
	}


}

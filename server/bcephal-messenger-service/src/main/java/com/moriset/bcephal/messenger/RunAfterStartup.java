/**
 * 
 */
package com.moriset.bcephal.messenger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Moriset
 *
 */
@Component
@Slf4j
public class RunAfterStartup {
	
	@Autowired
    private JobExplorer jobExplorer;
	
	@Autowired
    JobRepository jobRepository;

    @Autowired
    JobOperator jobOperator;
	
	@Autowired EntityManager entityManager;
	
	@EventListener(ContextRefreshedEvent.class)
	@Transactional
	public void runAfterStartup() {
		try{
			Query query = entityManager.createNativeQuery("UPDATE batch_job_execution set end_time = start_time, status = 'COMPLETED' WHERE status != 'COMPLETED'");
			query.executeUpdate();
			query = entityManager.createNativeQuery("UPDATE batch_step_execution set end_time = start_time, status = 'COMPLETED' WHERE status != 'COMPLETED'");
			query.executeUpdate();			
		}
		catch (Exception e) {
			log.error("", e);
		}
	}
	
	
	public void retartPending() {
        log.info("Container restart: restarting 'running' batch jobs");
        List<String> jobs = jobExplorer.getJobNames();
        for (String job : jobs) {
            Set<JobExecution> runningJobs = jobExplorer.findRunningJobExecutions(job);

            for (JobExecution runningJob : runningJobs) {
                try {
                	log.info("Restarting job {} with parameters {}", runningJob.getJobInstance().getJobName(), runningJob.getJobParameters().toString());
                    runningJob.setStatus(BatchStatus.FAILED);
                    runningJob.setEndTime(LocalDateTime.now());
                    jobRepository.update(runningJob);
                    jobOperator.restart(runningJob.getId());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

}

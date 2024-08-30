package com.moriset.bcephal.messenger.config;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.moriset.bcephal.messenger.services.StatisticsService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class BatchConfigPerformeJob {

	@Autowired
	JobLauncher jobLauncher;
	
	@Autowired
	StatisticsService statisticsService;
	
	@Autowired
	Job job;
	
	@Value("${bcephal.messenger.error.action:NONE}")
	String action;
	
	@Scheduled(cron="${bcephal.messenger.send.cron:0 * * ? * *}")
	public void performJob() throws Exception {
		log.debug("--------------------------------------------------Execute job...");
		try {
			JobParametersBuilder builder = new JobParametersBuilder();
			builder.addString("JobID", String.valueOf(System.currentTimeMillis()));
			builder.addDate("runDate", new Date());
			JobExecution execution = jobLauncher.run(job, builder.toJobParameters());
			log.debug("Job status : {}", execution.getStatus());
		}
		catch (Exception e) {
			String message = "Unexpected error! System will restart/stop to avoid duplicate mail.";
			message += "\n\nDetails : \n" + e.getMessage();
			message += "\n\n" + e.toString();
			log.error(message, e);
			statisticsService.sendError("B-cephal messenger : Error notification", message);
			if("RESTART".equalsIgnoreCase(action)) {
				log.trace("Try to restart application...");
				//BcephalMessengerServiceApplication.restart();
			}
			else if("STOP".equalsIgnoreCase(action)) {
				log.trace("Stop application...");
				//BcephalMessengerServiceApplication.stop(-1);
			}
		}		
	}

}

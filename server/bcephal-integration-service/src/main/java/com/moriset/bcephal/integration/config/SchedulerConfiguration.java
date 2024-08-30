/**
 * 
 */
package com.moriset.bcephal.integration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import lombok.Data;

/**
 * @author MORISET-004
 *
 */
@Configuration
@EnableScheduling
@Data
public class SchedulerConfiguration implements SchedulingConfigurer {
	
	@Value("${bcephal.scheduling.pool.size:5}")
	private Integer poolSize;

	@Override
	public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
		scheduledTaskRegistrar.setTaskScheduler(this.poolScheduler());
	}

    @Bean
    TaskScheduler poolScheduler() {		
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(poolSize != null ? poolSize : 5);
		scheduler.setThreadNamePrefix("Bcephal-Scheduling-pool");
		return scheduler;
	}
}

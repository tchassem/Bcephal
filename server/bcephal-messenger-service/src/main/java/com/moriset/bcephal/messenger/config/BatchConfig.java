/**
 * 
 */
package com.moriset.bcephal.messenger.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

import com.moriset.bcephal.messenger.model.AlarmMessageLogToSend;
import com.moriset.bcephal.messenger.repository.AlarmMessageLogToSendRepository;
import com.moriset.bcephal.messenger.services.AlarmMessageLogItemProcessor;
import com.moriset.bcephal.messenger.services.AlarmMessageLogItemReader;
import com.moriset.bcephal.messenger.services.AlarmMessageLogItemWriter;

/**
 * @author Moriset
 *
 */
@Configuration
public class BatchConfig {
//	
//	@Autowired
//	JobBuilderFactory jobBuilderFactory;
//	
//	@Autowired
//	StepBuilderFactory stepBuilderFactory;
	
	
	
	@Autowired
	AlarmMessageLogToSendRepository alarmMessageLogToSendRepository;
	
	@Value("${bcephal.messenger.send.page.size:10}")
	int pageSize;
	
		
	@Bean
	AlarmMessageLogItemReader reader(){
		AlarmMessageLogItemReader reader = new AlarmMessageLogItemReader();
		reader.setPageSize(pageSize);
		reader.setRepository(alarmMessageLogToSendRepository);
		return reader;
	}
	
	@Bean
	AlarmMessageLogItemProcessor processor() {
		return new AlarmMessageLogItemProcessor();
	}
	
	@Bean
	AlarmMessageLogItemWriter writer() {
		return new AlarmMessageLogItemWriter();
	}
	
	@Bean
	Step setp1(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
		DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute();
		transactionAttribute.setPropagationBehavior(TransactionAttribute.PROPAGATION_NEVER);
		return new StepBuilder("Step1",jobRepository)
				.<AlarmMessageLogToSend, AlarmMessageLogToSend>chunk(pageSize,transactionManager)				
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.allowStartIfComplete(true)
				.transactionAttribute(transactionAttribute)
				.build();
	}
	
	
	@Bean
	Job job(JobRepository jobRepository, Step step) throws Exception {
		return new JobBuilder("job",jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(step)
				.build();
	}
	
	
}

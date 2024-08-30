package com.moriset.bcephal.etl.config;

import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableBatchProcessing
public class BatchConfiguration {

//	@Bean
//	public Job mt940Job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//		return new JobBuilder("mt940Job", jobRepository)
//				.start(mt940Step(jobRepository, transactionManager))
//				.build();
//	}
//	
//	@Bean
//	public Step mt940Step(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
////		SimpleCompletionPolicy policy = new SimpleCompletionPolicy(1);
////		policy.set
//		return new StepBuilder("mt940Step", jobRepository)
//					.<MT940, List<TransactionItem>>chunk(1, transactionManager)
//					.reader(mt940ItemReader())
//					.processor(mt940ItemProcessor())
//					.writer(mt940ItemWriter())
//					.build();
//	}
//	
//	@Bean
//	public Job mt942Job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//		return new JobBuilder("mt942Job", jobRepository)
//				.preventRestart()
//				.start(mt942Step(jobRepository, transactionManager))
//				.build();
//	}
//	
//	@Bean
//	public Step mt942Step(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
////		SimpleCompletionPolicy policy = new SimpleCompletionPolicy(1);
////		policy.set
//		return new StepBuilder("mt942Job", jobRepository)
//					.<MT940, List<TransactionItem>>chunk(1, transactionManager)
//					.reader(mt940ItemReader())
//					.processor(mt940ItemProcessor())
//					.writer(mt940ItemWriter())
//					.build();
//	}
//
//	@Bean
//	MT940ItemReader mt940ItemReader() {
//		return new MT940ItemReader();
//	}
//
//	@Bean
//	MT940ItemProcessor mt940ItemProcessor() {
//		return new MT940ItemProcessor();
//	}
//
//	@Bean
//	MT940ItemWriter mt940ItemWriter() {
//		return new MT940ItemWriter();
//	}
	
}

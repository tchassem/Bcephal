package com.moriset.bcephal.etl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import lombok.extern.slf4j.Slf4j;

/**
 * Entry point for configuration server.
 * <p>
 * The configuration server centralizes all the configuration files for the
 * various microservices.
 * <p>
 * Each microservice calls this server to obtain its configuration parameters.
 * 
 * @author Joseph Wambo
 *
 */
@SpringBootApplication(
	scanBasePackages = {
			"com.moriset.bcephal.etl", "com.moriset.bcephal.multitenant.jpa", "com.moriset.bcephal.config" ,"com.moriset.bcephal.base"
	}, 
	exclude = { 
		DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class 
	}
)


@EnableDiscoveryClient
@Slf4j
public class BcephalEtlServiceApplication {

//	@Autowired MT940Service mt940Service;
//	
//	@Autowired
//    JobLauncher jobLauncher;
//
//	@Autowired
//	JobDataExecutionService execution;
//	
//	@Autowired
//	JobDataInstanceService instance;
//	
//	
//    @Autowired Job mt940Job;
    
   
	
	public static void main(String[] args) {
		SpringApplication.run(BcephalEtlServiceApplication.class, args);
		log.info("ETL service started!");		
	}
	
//	@EventListener(ApplicationReadyEvent.class)
//	public void runAfterStartup() throws Exception {
//		//mt940Service.parseTansaction();
//		runMT940Job();
//	}
//	
//	public void runMT940Job() throws Exception {
//		Date runDate = new Date();
//		JobParameters parameters = new JobParametersBuilder()
//				.addString(ParameterCode.RUN_ID, new SimpleDateFormat("yyyyMMddHHmmss").format(runDate))
//				.addString(ParameterCode.RUN_DATE, new SimpleDateFormat("yyyyMMdd").format(runDate))
//				.addString(ParameterCode.BASE_DIR, Path.of(System.getProperty("user.dir"), "mt940").toString())
//				.addString(ParameterCode.OUTPUT_DIR, Path.of(System.getProperty("user.dir"), "out", "mt940").toString())
//				.addString(ParameterCode.FILE_NAMES, "mt940_01.txt;mt940_02.txt")
//				.addString(ParameterCode.CONTINUE_WHEN_ERROR, "true")
//				.addString(ParameterCode.CSV_OUT_SEPARATOR, ParameterCode.CSV_SEPARATOR_VALUE)
//				.addString(ParameterCode.ADD_HEADER, "true")
//				
//				.addString(ParameterCode.DATE_IN_FORMAT, ParameterCode.DATE_IN_FORMAT_VALUE)
//				.addString(ParameterCode.DATE_OUT_FORMAT, ParameterCode.DATE_OUT_FORMAT_VALUE)
//				.addString(ParameterCode.TIME_FORMAT, ParameterCode.TIME_FORMAT_VALUE)
//				.addString(ParameterCode.DECIMAL_IN_SEPARATOR, ParameterCode.DECIMAL_IN_SEPARATOR_VALUE)
//				.addLong(ParameterCode.DECIMAL_IN_COUNT, ParameterCode.DECIMAL_IN_COUNT_VALUE)
//				.addString(ParameterCode.DECIMAL_OUT_SEPARATOR, ParameterCode.DECIMAL_OUT_SEPARATOR_VALUE)
//				.addLong(ParameterCode.DECIMAL_OUT_COUNT, ParameterCode.DECIMAL_OUT_COUNT_VALUE)
//				.toJobParameters();
        // jobLauncher.run(mt940Job, parameters);
		
//		JobDataInstance job = new JobDataInstance();
//		job.setId(214841);
//
//
//		log.trace("************************** {}",execution.getExecutionsByInstanceId(214899));


    
//    }

}

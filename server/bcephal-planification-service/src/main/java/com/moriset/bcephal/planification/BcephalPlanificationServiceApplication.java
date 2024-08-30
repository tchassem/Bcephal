package com.moriset.bcephal.planification;

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
 * The configuration server centralizes all the configuration files for the various microservices.
 * <p>
 * Each microservice calls this server to obtain its configuration parameters.
 * 
 * @author Joseph Wambo
 *
 */
@SpringBootApplication(
		scanBasePackages = {"com.moriset.bcephal.planification", "com.moriset.bcephal.task", 
				"com.moriset.bcephal.multitenant.jpa", "com.moriset.bcephal.config" , "com.moriset.bcephal" },
		exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class}
)
@EnableDiscoveryClient
@Slf4j
public class BcephalPlanificationServiceApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(BcephalPlanificationServiceApplication.class, args);
		log.info("Local dir: {}", System.getProperty("user.dir"));
		log.info("Planification service started!");
	}

}

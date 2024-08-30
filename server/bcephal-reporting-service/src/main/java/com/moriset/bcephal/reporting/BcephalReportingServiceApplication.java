/**
 * 
 */
package com.moriset.bcephal.reporting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.reporting", "com.moriset.bcephal.grid",
		"com.moriset.bcephal.multitenant.jpa", "com.moriset.bcephal.config", "com.moriset.bcephal.join.service"
		, "com.moriset.bcephal", "com.moriset.bcephal.planification"}, exclude = {
				DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class })
@EnableDiscoveryClient
@Slf4j
public class BcephalReportingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BcephalReportingServiceApplication.class, args);
		log.info("Local dir: {}", System.getProperty("user.dir"));
		log.info("Reporting service started!");
	}

}

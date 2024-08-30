package com.moriset.bcephal.dashboard;

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
@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.dashboard", "com.moriset.bcephal.multitenant.jpa",
		"com.moriset.bcephal.dashboard.service.socket", "com.moriset.bcephal.config","com.moriset.bcephal" }, exclude = {
				DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class })
@EnableDiscoveryClient
@Slf4j
public class BcephalDashboardServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BcephalDashboardServiceApplication.class, args);
		log.info("Local dir: {}", System.getProperty("user.dir"));
		log.info("Dashboard service started!");
	}

}

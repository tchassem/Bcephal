package com.moriset.bcephal.project;

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
@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.project", "com.moriset.bcephal.security",
		"com.moriset.bcephal.multitenant.jpa", "com.moriset.bcephal.config" }, exclude = {
				DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class })
@EnableDiscoveryClient
@Slf4j
public class BcephalProjectServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BcephalProjectServiceApplication.class, args);
		log.info("Home dir: {}", System.getProperty("user.home"));
		log.info("Local dir: {}", System.getProperty("user.dir"));
		log.info("Project service started!");
	}

}

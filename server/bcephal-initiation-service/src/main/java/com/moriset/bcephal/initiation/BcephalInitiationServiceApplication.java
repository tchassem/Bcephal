package com.moriset.bcephal.initiation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import lombok.extern.slf4j.Slf4j;

/**
 * Entry point for Initiation server.
 * <p>
 * 
 * @author Joseph Wambo
 *
 */
@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.initiation", "com.moriset.bcephal.multitenant.jpa",
		"com.moriset.bcephal.config" }, exclude = { DataSourceAutoConfiguration.class,
				HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class })
@EnableDiscoveryClient
@Slf4j
public class BcephalInitiationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BcephalInitiationServiceApplication.class, args);
		log.info("Local dir: {}", System.getProperty("user.dir"));
		log.info("Initiation service started!");
	}

}

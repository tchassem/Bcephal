package com.moriset.bcephal.scheduler;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication(
		scanBasePackages = {
				"com.moriset.bcephal.scheduler",
				"com.moriset.bcephal.multitenant.jpa",
				"com.moriset.bcephal.service" ,
				"com.moriset.bcephal.grid.service",
				"com.moriset.bcephal.task.service",
				"com.moriset.bcephal.loader.service",
				"com.moriset.bcephal.reconciliation.service",
				"com.moriset.bcephal.alarm.service",
				"com.moriset.bcephal.join.service",
				"com.moriset.bcephal.security.service",
				"com.moriset.bcephal.billing.service",
				"com.moriset.bcephal.planification.service",
				"com.moriset.bcephal.config",
				"com.moriset.bcephal.project.archive",
				"com.moriset.bcephal.integration.service",
				"com.moriset.bcephal.dashboard.service"},
		exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, 
				FlywayAutoConfiguration.class
				}
)
@EnableDiscoveryClient
@Slf4j
public class BcephalSchedulerServiceApplication {
	public static void main(String[] args) {
		BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
		SpringApplication.run(BcephalSchedulerServiceApplication.class, args);
		log.info("Local dir: {}", System.getProperty("user.dir"));
		log.info("Scheduler service started!");
	}
	
	
}

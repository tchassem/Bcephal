/**
 * 
 */
package com.moriset.bcephal.integration;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@SpringBootApplication(
		scanBasePackages = {
				"com.moriset.bcephal.integration",
				"com.moriset.bcephal.multitenant.jpa",
				"com.moriset.bcephal.service",
				"com.moriset.bcephal",
				"com.moriset.bcephal.security",
				"com.moriset.bcephal.config.messaging",
				"com.moriset.bcephal.config",
				
				},
		exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class}
)
@EnableDiscoveryClient
@Slf4j
public class BcephalIntegrationServiceApplication {

	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        System.setProperty("spring.main.allow-bean-definition-overriding", "true");
		SpringApplication.run(BcephalIntegrationServiceApplication.class, args);
		log.info("Local dir: {}", System.getProperty("user.dir"));
		log.info("User home: {}", System.getProperty("user.home"));
		log.info("Integration service started!");
	}

}

package com.moriset.bcephal.sourcing.grid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.context.request.RequestContextListener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
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
@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.sourcing.grid", "com.moriset.bcephal.planification", "com.moriset.bcephal.loader", "com.moriset.bcephal.multitenant.jpa",
		"com.moriset.bcephal.utils.domain.socket", "com.moriset.bcephal.config","com.moriset.bcephal" }, exclude = {
				DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class })
@EnableDiscoveryClient
@Slf4j
public class BcephalSourcingGridServiceApplication implements ServletContextInitializer {

	public static void main(String[] args) {
		
		SpringApplication.run(BcephalSourcingGridServiceApplication.class, args);
		log.info("Home dir: {}", System.getProperty("user.home"));
		log.info("Local dir: {}", System.getProperty("user.dir"));
		log.info("Sourcing service started!");
	}

	public void onStartup(ServletContext servletContext) throws ServletException {
		servletContext.addListener(RequestContextListener.class);
	}
}

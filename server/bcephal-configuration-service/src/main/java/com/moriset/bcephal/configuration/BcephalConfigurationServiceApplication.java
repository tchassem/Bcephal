package com.moriset.bcephal.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

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
@SpringBootApplication
@EnableConfigServer
@Slf4j
public class BcephalConfigurationServiceApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(BcephalConfigurationServiceApplication.class, args);
		log.trace("Local dir: {}",System.getProperty("user.dir"));
		log.info("Configuration service started!");
	}

}

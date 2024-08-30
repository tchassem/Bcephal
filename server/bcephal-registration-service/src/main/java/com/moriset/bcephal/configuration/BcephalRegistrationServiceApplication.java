package com.moriset.bcephal.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import lombok.extern.slf4j.Slf4j;

/**
 * Entry point for registration server.
 * <p>
 * Each new instance of each microservice calls this server to register.
 * <p>
 * This server also takes care of regularly checking whether each registered instance is still available 
 * in order to update its register by eliminating those which no longer exist.
 * 
 * @author Joseph Wambo
 *
 */
@SpringBootApplication
@EnableEurekaServer
@Slf4j
public class BcephalRegistrationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BcephalRegistrationServiceApplication.class, args);
		log.info("Registration service started!");
	}

}

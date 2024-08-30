package com.moriset.bcephal.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point for administration server.
 * <p>
 * This server is used for administration purpose.
 * 
 * 
 * @author Joseph Wambo
 *
 */
@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
//@EnableScheduling
@Slf4j
public class BcephalAdminServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BcephalAdminServiceApplication.class, args);
		log.info("Administration service started!");
	}

}

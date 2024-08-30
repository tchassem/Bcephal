package com.moriset.bcephal.license;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication(scanBasePackages = {
		"com.moriset.bcephal.license",
		"com.moriset.bcephal.config" , 
		"com.moriset.bcephal"
		})

@EnableDiscoveryClient
@Slf4j
public class BcephalLicenseManagerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BcephalLicenseManagerServiceApplication.class, args);
		log.trace("Local dir: {}", System.getProperty("user.dir"));
		log.info("License manager service started!");
	}

}

package com.moriset.bcephal.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import lombok.extern.slf4j.Slf4j;


@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class BcephalApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BcephalApiApplication.class, args);
		log.info("API service started!");
	}

}

package com.moriset.bcephal.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication(
		scanBasePackages = {
				"com.moriset.bcephal.multitenant.jpa", 
				"com.moriset.bcephal.security"  
				},
		exclude = { 
				DataSourceAutoConfiguration.class, 
				HibernateJpaAutoConfiguration.class
				}
)
@EnableDiscoveryClient
@Slf4j
public class BcephalSecurityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BcephalSecurityServiceApplication.class, args);
		log.info("Security service started!");
	}
}

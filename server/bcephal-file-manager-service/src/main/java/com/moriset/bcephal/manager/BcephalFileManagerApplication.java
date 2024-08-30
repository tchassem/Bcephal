package com.moriset.bcephal.manager;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.manager.config","com.moriset.bcephal" }, exclude = {
		DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class })
@EnableDiscoveryClient
@Slf4j
public class BcephalFileManagerApplication {

	public static void main(String[] args) {		
		SpringApplication.run(BcephalFileManagerApplication.class, args);
		log.info("Local dir: {}", System.getProperty("user.dir"));
		log.info("File Manager service started!");
	}
	
	@Bean
	PasswordEncoder getPasswordEncoder() {
		PasswordEncoder objectMapper = new BCryptPasswordEncoder();	
		return objectMapper;
	}
	
	@LoadBalanced
	@Bean
	RestTemplate loadBalancedRestTemplate() {
		RestTemplate template = new RestTemplate();
		addConverters(template);
		return template;
	}
	
	protected void addConverters(RestTemplate restTemplate) {
		ArrayList<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>(
				Arrays.asList(new MappingJackson2HttpMessageConverter(), new ResourceHttpMessageConverter(),
						new FormHttpMessageConverter(), new ByteArrayHttpMessageConverter(),
						new StringHttpMessageConverter(), new BufferedImageHttpMessageConverter()));
		restTemplate.getMessageConverters().addAll(converters);
		return;
	}
	
}

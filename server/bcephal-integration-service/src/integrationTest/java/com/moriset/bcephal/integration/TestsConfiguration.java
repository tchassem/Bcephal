package com.moriset.bcephal.integration;

import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"com.moriset.bcephal.integration",
		"com.moriset.bcephal.sourcing.grid.service",
		"com.moriset.bcephal.multitenant.jpa"
		,"com.moriset.bcephal.config","com.moriset.bcephal"}, exclude = {
				DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class,
				SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
public class TestsConfiguration { 
	
}

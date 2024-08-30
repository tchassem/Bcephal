package com.moriset.bcephal.multitenant.jpa;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "spring.datasource.hikari")
@Component
@Data
public class HikariProperties {

	private Boolean autoCommit;
	private Long connectionTimeout;
	private Long idleTimeout;	
	private Long validationTimeout;
	private Long initializationFailTimeout;
	private String connectionTestQuery;
	private Long leakDetectionThleshold;	
	private Long keepaliveTime;	
	private Long maxLifetime;
	private Integer minimumIdle;
	private Integer maximumPoolSize;
		

}

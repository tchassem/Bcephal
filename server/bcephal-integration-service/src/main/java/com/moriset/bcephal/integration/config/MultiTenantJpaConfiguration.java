package com.moriset.bcephal.integration.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableJpaRepositories(basePackages = { 
//		"com.moriset.bcephal.repository", }, transactionManagerRef = "transactionManager")
public class MultiTenantJpaConfiguration extends com.moriset.bcephal.multitenant.jpa.MultiTenantJpaConfiguration {

	@Override
	public String[] getPackagesToScan() {
		return new String[] {};
	}

	@Override
	protected DataSource buildSecurityDataSource() throws Exception {
		return null;
	}

}

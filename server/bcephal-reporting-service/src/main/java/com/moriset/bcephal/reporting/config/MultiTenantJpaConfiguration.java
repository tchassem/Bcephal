package com.moriset.bcephal.reporting.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.moriset.bcephal.domain.Persistent;

@Configuration
@EnableJpaRepositories(basePackages = {
		"com.moriset.bcephal.sourcing.grid.repository", 
		"com.moriset.bcephal.repository",
		"com.moriset.bcephal.grid.repository",
	    "com.moriset.bcephal.sheet.repository",
		"com.moriset.bcephal.join.repository",
		"com.moriset.bcephal.planification.repository"}, transactionManagerRef = "transactionManager")
public class MultiTenantJpaConfiguration extends com.moriset.bcephal.multitenant.jpa.MultiTenantJpaConfiguration {

	@Override
	public String[] getPackagesToScan() {
		return new String[] { Persistent.class.getPackage().getName(), "com.moriset.bcephal.grid.domain",
				"com.moriset.bcephal.sourcing.grid.domain","com.moriset.bcephal.sheet.domain"
				,"com.moriset.bcephal.join.domain", "com.moriset.bcephal.planification.domain"};
	}

	@Override
	protected DataSource buildSecurityDataSource() throws Exception {
		return null;
	}

	
	
}

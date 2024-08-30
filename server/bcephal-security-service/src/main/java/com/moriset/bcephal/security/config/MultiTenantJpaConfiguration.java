package com.moriset.bcephal.security.config;

import javax.sql.DataSource;

//@Configuration
//@EnableJpaRepositories(/*basePackages = {"com.moriset.bcephal.security.repository" },*/ transactionManagerRef = "transactionManager")
public class MultiTenantJpaConfiguration extends com.moriset.bcephal.multitenant.jpa.MultiTenantJpaConfiguration {

	@Override
	public String[] getPackagesToScan() {
		return new String[] {  };
	}
	
	@Override
	protected DataSource buildSecurityDataSource() throws Exception {
		return null;
	}

	
}

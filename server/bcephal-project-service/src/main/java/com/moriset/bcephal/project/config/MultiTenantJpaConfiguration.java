package com.moriset.bcephal.project.config;

import javax.sql.DataSource;

//@Configuration
//@EnableJpaRepositories(basePackages = {
//		"com.moriset.bcephal.project.repository" }, transactionManagerRef = "transactionManager")
public class MultiTenantJpaConfiguration extends com.moriset.bcephal.multitenant.jpa.MultiTenantJpaConfiguration {

	@Override
	public String[] getPackagesToScan() {
		// return new String[] { Project.class.getPackage().getName() };
		return new String[] { "com.moriset.bcephal.project.domain" };
	}

	@Override
	protected DataSource buildSecurityDataSource() throws Exception {
		return null;
	}

}

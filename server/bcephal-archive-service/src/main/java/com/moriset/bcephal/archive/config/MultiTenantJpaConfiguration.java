package com.moriset.bcephal.archive.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.moriset.bcephal.archive.domain.Archive;
import com.moriset.bcephal.domain.Persistent;

@Configuration
@EnableJpaRepositories(basePackages = {
		"com.moriset.bcephal.archive.repository" ,"com.moriset.bcephal.repository",
		"com.moriset.bcephal.grid.repository",
	    "com.moriset.bcephal.sheet.repository"}, transactionManagerRef = "transactionManager")
public class MultiTenantJpaConfiguration extends com.moriset.bcephal.multitenant.jpa.MultiTenantJpaConfiguration {

	@Override
	public String[] getPackagesToScan() {
		return new String[] { Archive.class.getPackage().getName(),
				Persistent.class.getPackage().getName(), "com.moriset.bcephal.grid.domain",
				"com.moriset.bcephal.sourcing.grid.domain","com.moriset.bcephal.sheet.domain" };
	}

	@Override
	protected DataSource buildSecurityDataSource() throws Exception {
		return null;
	}

}
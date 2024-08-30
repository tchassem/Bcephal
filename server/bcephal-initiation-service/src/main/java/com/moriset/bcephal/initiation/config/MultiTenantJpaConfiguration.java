package com.moriset.bcephal.initiation.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.initiation.domain.Model;

@Configuration
@EnableJpaRepositories(basePackages = {
		"com.moriset.bcephal.initiation.repository" }, transactionManagerRef = "transactionManager")
public class MultiTenantJpaConfiguration extends com.moriset.bcephal.multitenant.jpa.MultiTenantJpaConfiguration {

	@Override
	public String[] getPackagesToScan() {
		return new String[] { Model.class.getPackage().getName(), Parameter.class.getPackage().getName() };
	}

	@Override
	protected DataSource buildSecurityDataSource() throws Exception {
		return null;
	}

}

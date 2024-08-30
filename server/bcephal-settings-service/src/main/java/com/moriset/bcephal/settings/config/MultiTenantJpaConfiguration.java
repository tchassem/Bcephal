package com.moriset.bcephal.settings.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.settings.domain.ParameterEditorData;

@Configuration
@EnableJpaRepositories(basePackages = { 
		"com.moriset.bcephal.settings.repository", "com.moriset.bcephal.repository",
		"com.moriset.bcephal.grid.repository", "com.moriset.bcephal.join.repository",
		"com.moriset.bcephal.chat.repository"
		}, transactionManagerRef = "transactionManager")
public class MultiTenantJpaConfiguration extends com.moriset.bcephal.multitenant.jpa.MultiTenantJpaConfiguration {

	@Override
	public String[] getPackagesToScan() {
		return new String[] { 
				ParameterEditorData.class.getPackage().getName(), 
				Persistent.class.getPackage().getName(),
				"com.moriset.bcephal.grid.domain",
				"com.moriset.bcephal.chat.domain",
				"com.moriset.bcephal.join.domain" };
	}

	@Override
	protected DataSource buildSecurityDataSource() throws Exception {
		return null;
	}

}

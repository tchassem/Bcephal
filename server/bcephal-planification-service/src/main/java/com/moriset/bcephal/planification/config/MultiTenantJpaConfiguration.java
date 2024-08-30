package com.moriset.bcephal.planification.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutine;
import com.moriset.bcephal.planification.domain.script.Script;

@Configuration
@EnableJpaRepositories(basePackages = {
		"com.moriset.bcephal.planification.repository",
		"com.moriset.bcephal.task.repository",
		"com.moriset.bcephal.repository",
		"com.moriset.bcephal.grid.repository",
	    "com.moriset.bcephal.sheet.repository"
}, 
transactionManagerRef = "transactionManager")
public class MultiTenantJpaConfiguration extends com.moriset.bcephal.multitenant.jpa.MultiTenantJpaConfiguration {
	
	@Override
	public String[] getPackagesToScan() {
		return new String[] {
				TransformationRoutine.class.getPackage().getName(), 
				Script.class.getPackage().getName(), 
				Persistent.class.getPackage().getName(), "com.moriset.bcephal.task.domain", "com.moriset.bcephal.grid.domain",
				"com.moriset.bcephal.sourcing.grid.domain","com.moriset.bcephal.sheet.domain" 
		};
	}
		
	@Override
	protected DataSource buildSecurityDataSource() throws Exception {
		return null;
	}
	
	
}

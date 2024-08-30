package com.moriset.bcephal.etl.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.etl.domain.EmailAccount;

@Configuration
@EnableJpaRepositories(basePackages = {
		"com.moriset.bcephal.repository" ,"com.moriset.bcephal.etl.repository"}, transactionManagerRef = "transactionManager")
public class MultiTenantJpaConfiguration extends com.moriset.bcephal.multitenant.jpa.MultiTenantJpaConfiguration {

	@Override
	public String[] getPackagesToScan() {
		return new String[] { EmailAccount.class.getPackage().getName(), 
				Persistent.class.getPackage().getName(),"com.moriset.bcephal.etl.domain" };
	} 

	
}

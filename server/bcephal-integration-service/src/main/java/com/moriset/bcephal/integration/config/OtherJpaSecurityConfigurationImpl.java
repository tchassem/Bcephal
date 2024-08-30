package com.moriset.bcephal.integration.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.moriset.bcephal.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.config.OtherJpaSecurityConfiguration;
import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.grid.domain.SmartMaterializedGrid;
import com.moriset.bcephal.integration.domain.ConnectEntity;

@Configuration
@EnableJpaRepositories(basePackages = {
		"com.moriset.bcephal.integration.repository",
		"com.moriset.bcephal.repository",
		"com.moriset.bcephal.grid.repository" },
entityManagerFactoryRef = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER_FACTORY, 
transactionManagerRef = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
public class OtherJpaSecurityConfigurationImpl extends OtherJpaSecurityConfiguration {

	@Bean
	com.moriset.bcephal.config.ISecurityConfiguration ISecurityConfiguration() {
		return ()->{			
			List<String> items = new ArrayList<>();
			items.add(ConnectEntity.class.getPackage().getName());
			items.add(IPersistent.class.getPackage().getName());
			items.add(SmartMaterializedGrid.class.getPackage().getName());
			return items;
			};
	}
}

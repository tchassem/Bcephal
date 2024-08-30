package com.moriset.bcephal.reporting.config;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.moriset.bcephal.config.messaging.MessagingHandler;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.oauth2.config.AbstractOAuth2SecurityConfiguration;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class OAuth2SecurityConfiguration extends AbstractOAuth2SecurityConfiguration {
	@Autowired
	Map<String, DataSource> bcephalDataSources;

    @ConditionalOnProperty(prefix = "activemq", name = "enable", havingValue = "true")
    @Bean
    MessagingHandler getProjectHandler() {
		return new MessagingHandler() { 
			@Override
			public void closeProject(String message) {
			if (bcephalDataSources.containsKey(message)) {
				HikariDataSource item = (HikariDataSource) bcephalDataSources.remove(message);
				if (item != null) {
					item.close();
				}
				TenantContext.setCurrentTenant(null);
			}
		}
		};
	}
}

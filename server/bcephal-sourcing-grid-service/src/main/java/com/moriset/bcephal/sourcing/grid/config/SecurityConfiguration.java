/**
 * 
 */
package com.moriset.bcephal.sourcing.grid.config;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.moriset.bcephal.config.RestConfig;
import com.moriset.bcephal.config.messaging.MessagingHandler;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.oauth2.config.AbstractSecurityConfiguration;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableWebSecurity
@Import(RestConfig.class)
public class SecurityConfiguration extends AbstractSecurityConfiguration {
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

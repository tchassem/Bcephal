/**
 * 
 */
package com.moriset.bcephal.scheduler.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.moriset.bcephal.config.messaging.MessagingHandler;
import com.moriset.bcephal.domain.scheduler.ShedulerData;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.oauth2.config.AbstractSecurityConfiguration;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author Joseph Wambo
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends AbstractSecurityConfiguration {

    @Bean
    Map<String, List<ShedulerData>> scheduledRepository() {
		return new ConcurrentHashMap<>();
	}

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

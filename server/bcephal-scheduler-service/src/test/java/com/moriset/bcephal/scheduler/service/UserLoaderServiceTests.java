/**
 * 6 févr. 2024 - UserLoaderServiceTests.java
 *
 */
package com.moriset.bcephal.scheduler.service;

import java.util.Locale;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.NoSuchMessageException;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.loader.domain.UserLoader;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.scheduler.UserLoaderFactory;
import com.moriset.bcephal.utils.BCephalParameterTest;

/**
 * @author Emmanuel Emmeni
 *
 */
@SpringBootTest
@ActiveProfiles(profiles = {"int", "serv"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserLoaderServiceTests {
	
	@SpringBootApplication(
		scanBasePackages = {
				"com.moriset.bcephal.loader.service", "com.moriset.bcephal.service", "com.moriset.bcephal.multitenant.jpa", "com.moriset.bcephal.config", 
				"com.moriset.bcephal.task.service", "com.moriset.scheduler", "com.moriset.bcephal.planification"
		},
		exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class }
	)
	@EntityScan(
		basePackages = { 
			"com.moriset.bcephal.domain", "com.moriset.bcephal.security.domain"
		}
	)
	@ActiveProfiles(profiles = {"int", "serv"})
	static class ApplicationTests {}
	
	@Autowired
    private UserLoaderService userLoaderService;
    
	@Autowired
	MultiTenantInterceptor Interceptor;
	
	private static UserLoader userLoader;
    
    @BeforeAll
    static void init() {
    	userLoader = new UserLoaderFactory().build();
    	Assertions.assertThat(userLoader).isNotNull();
    	Assertions.assertThat(userLoader.getId()).isNull();
	}
    
    @Test
    @DisplayName("Validate user loader bean.")
    @Order(1)
	public void validateTest() throws Exception {
    	Interceptor.setTenantForServiceTest("" + BCephalParameterTest.PROJECT_CODE);
    	UserLoader userLoader = new UserLoaderFactory().build();
    	userLoader.setName(null);
	    org.junit.jupiter.api.Assertions.assertThrows(NoSuchMessageException.class, () -> {
	    	userLoaderService.save(userLoader, Locale.getDefault());
	    });
	}
    
}

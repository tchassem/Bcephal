package com.moriset.bcephal.initiation.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

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
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.initiation.domain.Model;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;

@SpringBootTest
@ActiveProfiles(profiles = {"int", "serv"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ModelServiceIntegretionTest {
	@SpringBootApplication(
			scanBasePackages = { 
					"com.moriset.bcephal.initiation",
					"com.moriset.bcephal.multitenant.jpa", 
					"com.moriset.bcephal.config" 
					}, 
			exclude = {
					DataSourceAutoConfiguration.class, 
					HibernateJpaAutoConfiguration.class
					}
			)
	@EntityScan(basePackages = { "com.moriset.bcephal.domain.parameter", "com.moriset.bcephal.initiation.domain" })
	@ActiveProfiles(profiles = {"int", "serv"})
	static class ApplicationTests {}
	
	private static Model model;
	
	
	@Autowired ModelService modelService;
	@Autowired
	MultiTenantInterceptor Interceptor;
	private String EntityTenantId = "bcephal_initiation_test";
	
	
	@BeforeAll
	static void init() {
		model = Model.builder().name("model").entityListChangeHandler(new ListChangeHandler<>()).build();
		
	}
	
	@Test
	@DisplayName("Save a new model")
	@Order(1)
	@Commit
	void saveNewModel() throws Exception {
		Interceptor.setTenantForServiceTest(EntityTenantId);
		assertThat(model).isNotNull();
		
		modelService.save(model, Locale.getDefault());
		assertThat(model.getId()).isNotNull();
		Model found = modelService.getById(model.getId());
		assertThat(found.isPersistent()).isTrue();
		assertThat(found.getId()).isEqualTo(model.getId());
	}
	

	@Test
	@DisplayName("delete model")
	@Order(2)
	@Commit
	void deleteModel() throws Exception {
		Interceptor.setTenantForServiceTest(EntityTenantId);
		assertThat(model).isNotNull();
		
		modelService.deleteModel(model, Locale.getDefault());
		Model found = modelService.getById(model.getId());
		assertThat(found).isNull();
		
	}
}

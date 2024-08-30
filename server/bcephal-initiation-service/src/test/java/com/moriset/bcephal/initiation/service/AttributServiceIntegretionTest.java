package com.moriset.bcephal.initiation.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.initiation.InitiationFactory;
import com.moriset.bcephal.initiation.domain.Attribute;
import com.moriset.bcephal.initiation.domain.Entity;
import com.moriset.bcephal.initiation.domain.Model;
import com.moriset.bcephal.initiation.domain.api.AttributeApi;
import com.moriset.bcephal.initiation.repository.EntityRepository;
import com.moriset.bcephal.initiation.repository.ModelRepository;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;

@SpringBootTest
@ActiveProfiles(profiles = {"int", "serv"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AttributServiceIntegretionTest {
	
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

	
	@Autowired
	MultiTenantInterceptor Interceptor;
	private String EntityTenantId = "bcephal_initiation_test";
	private static Attribute attribute;
	private static AttributeApi attributeapi;
	private static Entity entity;
	private static Model model;
	@Autowired AttributeService attributeService;
	@Autowired EntityRepository entityRepository;
	@Autowired ModelRepository modelRepository;
	@BeforeAll 
	static void init() {
		
		attributeapi = new AttributeApi(false, 1L);
		
		model = Model.builder().name("Billing_model").entityListChangeHandler(new ListChangeHandler<>()).build();
		
		entity = new InitiationFactory().buildEntity();
		
		Assertions.assertThat(entity).isNotNull();
    	attributeapi.setName("attributApiName12354");
		
		
		
	}
	
	@Test
	@DisplayName("create a new attribut")
	@Order(1)
	@Commit
	void createNewAttribut() throws Exception {
		Interceptor.setTenantForServiceTest(EntityTenantId);
		modelRepository.save(model);
		assertThat(model.getId()).isNotNull();
		entity.setModel(model);
		entityRepository.save(entity);
		assertThat(entity.getId()).isNotNull();
		attributeapi.setEntity(entity.getId());
		attribute = attributeService.createAttribute(attributeapi, Locale.getDefault());
		assertThat(attribute.getId()).isNotNull();
		Attribute found = attributeService.getById(attribute.getId());
		assertThat(found.isPersistent()).isTrue();
		
	}
	
	@Test
	@DisplayName("save a new attribut")
	@Order(2)
	@Commit
	void saveNewAttribut() throws Exception {
		Interceptor.setTenantForServiceTest(EntityTenantId);
		attribute = attributeService.save(attribute, Locale.getDefault());
		assertThat(attribute.getId()).isNotNull();
		Attribute found = attributeService.getById(attribute.getId());
		assertThat(found.isPersistent()).isTrue();
		
	}
	
	@Test
	@DisplayName("get attribute by EntityId")
	@Order(3)
	@Commit
	void getAttributByEntityId() throws Exception {
		Interceptor.setTenantForServiceTest(EntityTenantId);
		List<Attribute> attributes = attributeService.getAttributeByEntityId(entity.getId(), Locale.getDefault());
		assertThat(attributes.size()).isGreaterThan(0);
		
	}
	
	
	// le test de suppression renvoit l'erreur Unexpected error while delete attribute value mais la suppression s'effectue bien 
	@Test
	@DisplayName("delete attribut")
	@Order(10)
	@Commit
	void deleteAttribut() throws Exception {
		Interceptor.setTenantForServiceTest(EntityTenantId);
		assertThat(attribute).isNotNull();
		modelRepository.delete(model);
		entityRepository.delete(entity);
		attributeService.deleteAttribute(attribute, Locale.getDefault());
		Attribute found = attributeService.getById(attribute.getId());
		assertThat(found).isNull();
//		assertThat(found.isPersistent()).isFalse();
		
	}
	
}



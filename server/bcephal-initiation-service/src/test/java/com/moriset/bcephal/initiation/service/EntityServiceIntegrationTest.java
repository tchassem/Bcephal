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
import com.moriset.bcephal.initiation.domain.Entity;
import com.moriset.bcephal.initiation.domain.Model;
import com.moriset.bcephal.initiation.repository.EntityRepository;
import com.moriset.bcephal.initiation.repository.ModelRepository;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;

@SpringBootTest
@ActiveProfiles(profiles = {"int", "serv"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EntityServiceIntegrationTest {
	
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
	
	
	@Autowired EntityService entityService;
	@Autowired EntityRepository entityRepository;
	@Autowired ModelRepository modelRepository;
	private static Entity entity;
	private static Model model;
	
	@Autowired
	MultiTenantInterceptor Interceptor;
	private String EntityTenantId = "bcephal_initiation_test";
	@BeforeAll 
	static void init() {
		model = Model.builder().name("Billing-model").entityListChangeHandler(new ListChangeHandler<>()).build();
	
		entity = new InitiationFactory().buildEntity();
		
		Assertions.assertThat(entity).isNotNull();
		Assertions.assertThat(entity.getId()).isNull();
	}

	
	@Test
	@DisplayName("create a new entity test")
	@Order(1)
	@Commit
	void createEntityServiceTest() throws Exception {

    	Interceptor.setTenantForServiceTest(EntityTenantId);
    	assertThat(entity).isNotNull();
    	 modelRepository.save(model);
    	String name =entity.getName();
    	Long id = model.getId();
    	Locale local = Locale.getDefault();
		entity= entityService.createEntity(name, id , local );
		assertThat(entity.getName()).isEqualTo(entity.getName());
	}
	
	@Test
	@DisplayName("save a new entity test")
	@Order(2)
	@Commit
	void saveNewEntityServiceTest() throws Exception {

    	Interceptor.setTenantForServiceTest(EntityTenantId);
		assertThat(entity).isNotNull();
		entityService.saveEntity(entity, Locale.getDefault() );
    	assertThat(entity.isPersistent()).isTrue();
    	
		
	}
	
	@Test
	@DisplayName("Get Entity by modeId test")
	@Order(4)
	@Commit
	void getEntityByModelIdServiceTest() throws Exception {

    	Interceptor.setTenantForServiceTest(EntityTenantId);
		assertThat(entity).isNotNull();
		List<Entity> found =  entityService.getEntityByModelId(model.getId(), Locale.getDefault());
		assertThat(found).isNotEmpty();
    	
		
	}
	
	@Test
	@DisplayName("delete entity test")
	@Order(10)
	@Commit
	void deleteEntityServiceTest() throws Exception {
		Interceptor.setTenantForServiceTest(EntityTenantId);
		assertThat(entity).isNotNull();
		entityService.deleteEntity(entity, Locale.getDefault());
		modelRepository.delete(model);
		Entity  found = entityService.getById(entity.getId());
		assertThat(found).isNull();
	}
}

package com.moriset.bcephal.initiation.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
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
import com.moriset.bcephal.initiation.InitiationFactory;
import com.moriset.bcephal.initiation.domain.Attribute;
import com.moriset.bcephal.initiation.domain.AttributeValue;
import com.moriset.bcephal.initiation.repository.AttributeRepository;
import com.moriset.bcephal.initiation.repository.AttributeValueRepository;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;

@SpringBootTest
@ActiveProfiles(profiles = {"int", "serv"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class attributeValueServiceItegrationTest {

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
	
	@Autowired AttributeValueService attributeValueService;
	@Autowired AttributeValueRepository attributeValueRepository;
	@Autowired AttributeRepository attributeRepository;
	
	
	private static AttributeValue attributeValue;
	private static Attribute attribute;
	
	@BeforeAll
	static void init() {
		
		List<AttributeValue> children = new ArrayList<AttributeValue>();
		ListChangeHandler<AttributeValue> listChildren = new ListChangeHandler<>();
		children.add(attributeValue);
		attribute = new InitiationFactory().BuildAttribute();
		
		 attributeValue = AttributeValue.builder().name("attributName").children(children).childrenListChangeHandler(listChildren)
				.attribute(attribute)
				.build();
		
	}
	
	
	@Test
	@DisplayName("save a new attributeValue")
	@Order(1)
	@Commit
	void saveNewAttributeValue() throws Exception{
		Interceptor.setTenantForServiceTest(EntityTenantId);
		assertThat(attributeValue).isNotNull();
		attribute = attributeRepository.save(attribute);
		attributeValue = attributeValueService.save(attributeValue, Locale.getDefault());
		AttributeValue found = attributeValueService.getById(attributeValue.getId());
		assertThat(found).isNotNull();
		assertThat(found.getId()).isEqualTo(attributeValue.getId());
		assertThat(attributeValue.isPersistent()).isTrue();
	}
	
//	@Test
//	@DisplayName("get attributeValue by Id")
//	@Order(2)
//	@Commit
//	void getAttributeValueByAttributeId() throws Exception{
//		Interceptor.setTenantForServiceTest(EntityTenantId);
//		assertThat(attributeValue).isNotNull();
//		List<AttributeValue> attributes = attributeValueService.getAttributeValueByAttributeId(attributeValue.getId(), Locale.getDefault());
//		assertThat(attributes).isNotEmpty();
//	}
	
	@Test
	@DisplayName("delete attributeValue")
	@Order(4)
	@Commit
	void deletewAttributeValue() throws Exception{
		assertThat(attributeValue).isNotNull();
		
		attributeValueService.deleteValue(attributeValue, Locale.getDefault());
		attributeRepository.delete(attribute);
		AttributeValue found = attributeValueService.getById(attributeValue.getId());
		assertThat(found).isNull();
	}
}

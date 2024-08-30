package com.moriset.bcephal.initiation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.initiation.domain.Attribute;
import com.moriset.bcephal.initiation.domain.AttributeValue;

@DataJpaTest
@ActiveProfiles(profiles = { "int", "repo" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AttributValueRepositoryIntegrationTest {

	
	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.initiation", "com.moriset.bcephal.multitenant.jpa",
	"com.moriset.bcephal.config" })
@EntityScan(basePackages = { "com.moriset.bcephal.domain.parameter", "com.moriset.bcephal.initiation.domain" })
@ActiveProfiles(profiles = { "int", "repo" })
static class ApplicationTests { }
	
	@Autowired AttributeRepository attributeRepository;
	@Autowired AttributeValueRepository attributeValueRepository;
	private static AttributeValue attributeValue;
	private static Attribute attribute;
	
	@BeforeAll
	private static void init() {
		 attribute = Attribute.builder().defaultValue("DefaultValue").build();
		
		attributeValue = AttributeValue.builder()
				.attribute(attribute)
				.build();
	}
	
	@Test
	@DisplayName("save a new attributeValue")
	@Order(1)
	@Commit
	void createNewAttributeValueTest() throws Exception {
		
		assertThat(attributeValue).isNotNull();
		Attribute att = attributeRepository.save(attribute);
		attributeValue.setAttribute(att);
		AttributeValue attrib = attributeValueRepository.save(attributeValue);
		Optional<AttributeValue> found = attributeValueRepository.findById(attributeValue.getId());
		assertThat(found).isNotNull();
 		Assertions.assertTrue(attrib.isPersistent());
 		Assertions.assertSame(attributeValue, attrib);
		
	}
	
	@Test
	@DisplayName("update existing attributeValue")
	@Order(2)
	@Commit
	void updateExistingAttributValueTest() throws Exception{
		assertThat(attributeValue).isNotNull();
		attributeValue.setName("UpdateName");
		attributeValueRepository.save(attributeValue);
		assertEquals("UpdateName", attributeValue.getName());
	}
	
	
	@Test 
	@DisplayName("find attributeValue by attribute")
	@Order(4)
	void findattributValueByAttributeTest() throws Exception{
		assertThat(attributeValue).isNotNull();
		List<AttributeValue> attributeValueFound = attributeValueRepository.findByAttribute(attributeValue.getAttribute());
		assertThat(attributeValueFound).isNotEmpty();
	}
	@Test
	@DisplayName("find attribute value by id")
	@Order(3)
	@Commit
	void findAttributByIdTest() throws Exception{
		assertThat(attributeValue).isNotNull();
		Optional<AttributeValue>  attribFound = attributeValueRepository.findById(attributeValue.getId());
		assertThat(attribFound).isPresent();
		assertEquals(attribFound.get().getId(), attributeValue.getId());
	}
	
	
	@Test
	@DisplayName("delete attribute Value")
	@Order(10)
	@Commit
	void deleteAttributeValueTest() {
		assertThat(attributeValue).isNotNull();
		assertThat(attribute).isNotNull();
		attributeValueRepository.deleteById(attributeValue.getId());
		attributeValueRepository.findById(attributeValue.getId());
	
	}
}

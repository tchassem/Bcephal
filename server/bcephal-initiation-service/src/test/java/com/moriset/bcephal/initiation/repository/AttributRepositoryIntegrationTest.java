package com.moriset.bcephal.initiation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

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


@DataJpaTest
@ActiveProfiles(profiles = { "int", "repo" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AttributRepositoryIntegrationTest {
	
	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.initiation", "com.moriset.bcephal.multitenant.jpa",
	"com.moriset.bcephal.config" })
@EntityScan(basePackages = { "com.moriset.bcephal.domain.parameter", "com.moriset.bcephal.initiation.domain" })
@ActiveProfiles(profiles = { "int", "repo" })
static class ApplicationTests { }
		
	@Autowired
	private AttributeRepository attributeRepository;
	
	private static Attribute attribute;
	@BeforeAll 
	static void init() {
		
		attribute = Attribute.builder().defaultValue("newDefaultValue").build();
		
	}
	
	@Test
	@DisplayName("save a new attribut")
	@Order(1)
	@Commit
	void saveNewAttribut() {
		assertThat(attribute).isNotNull();
		attributeRepository.save(attribute);
		Optional<Attribute> attributFound = attributeRepository.findById(attribute.getId());
		assertThat(attributFound).isNotNull();
		assertEquals(attribute.getId(), attributFound.get().getId());
	}
	
	@Test
	@DisplayName("update attribut")
	@Order(2)
	@Commit
	void updateAttributTest() {
		assertThat(attribute).isNotNull();
		attribute.setDefaultValue("updatedDefaultVavue");
		attributeRepository.save(attribute);
		Optional<Attribute> attributFound = attributeRepository.findById(attribute.getId());
		assertThat(attributFound).isNotNull();
		assertEquals(attribute.getDefaultValue(), attributFound.get().getDefaultValue());
	}
	@Test
	@DisplayName("find Attribut by id")
	@Order(3)
	@Commit
	void findAttributByIdTest() {
		assertThat(attribute).isNotNull();
		Optional<Attribute> found = attributeRepository.findById(attribute.getId());
		assertThat(found).isNotEmpty();
		assertEquals(attribute.getId(), found.get().getId());
	}
	
	@Test
	@DisplayName("find attribut by entity")
	@Order(4)
	@Commit
	void findattributeByEntityTest() {
		List<Attribute> attributes = attributeRepository.findByEntity(attribute.getEntity());
		assertThat(attributes).isNotEmpty();
		assertThat(attributes.size()).isGreaterThan(0);
	}
	
//	@Test
//	@DisplayName("delete attribute")
//	@Order(10)
//	@Commit
//	void deleteAttribut() {
//		assertThat(attribute).isNotNull();
//		attributeRepository.deleteById(attribute.getId());
//		Optional<Attribute> att = attributeRepository.findById(attribute.getId());
//		assertThat(att).isEmpty();
//	}

}

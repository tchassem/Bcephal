package com.moriset.bcephal.initiation.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

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
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.initiation.domain.Model;

import jakarta.validation.ConstraintViolationException;
@DataJpaTest
@ActiveProfiles(profiles = { "int", "repo" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ModelRepositoryIntegrationTest {

	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.initiation", "com.moriset.bcephal.multitenant.jpa",
			"com.moriset.bcephal.config" })
	@EntityScan(basePackages = { "com.moriset.bcephal.domain.parameter", "com.moriset.bcephal.initiation.domain" })
	@ActiveProfiles(profiles = { "int", "repo" })
	static class ApplicationTests { }

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private ModelRepository modelRepository;
	
	private static Model model;

	@BeforeAll
	static void init() {
//		model = Model.builder().name("modelName")
//				.creationDate(new Timestamp(System.currentTimeMillis()))
//				.modificationDate(new Timestamp(System.currentTimeMillis()))
//				.build();
		model = Model.builder().name("modelTest").entityListChangeHandler(new ListChangeHandler<>()).build();
		Assertions.assertThat(model).isNotNull();
		Assertions.assertThat(model.getId()).isNull();
	}

	@Test
	@DisplayName("validate model bean")
	@Order(1)
	void validateModelBean() {
		Model model = Model.builder().name("modelName")			
				.build();

		model.setName(null);
		org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
			entityManager.persist(model);
			entityManager.flush();
		});

		model.setName("");
		org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
			entityManager.persist(model);
			entityManager.flush();
		});
		
		model.setName("");
		for(int i =1; i<300; i++) {
			model.setName(model.getName().concat("B"));
		}
		org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
			entityManager.persist(model);
			entityManager.flush();
		});

	}
	
	@Test
	@DisplayName("save a new model")
	@Order(2)
	@Commit
	void saveModelTest() {
		modelRepository.save(model);
		assertThat(model.getId()).isNotNull();
		Optional<Model> modelFound = modelRepository.findById(model.getId());
		assertThat(modelFound.isPresent()).isTrue();
		
	}
	
	@Test
	@DisplayName("update model")
	@Order(3)
	@Commit
	void updateModelTest() {		
		model.setName("updateModelName");
		modelRepository.save(model);
		Optional<Model> newModelFound = modelRepository.findById(model.getId());
		assertThat(newModelFound).isNotNull();
		Assertions.assertThat((model.getName()).equals( newModelFound.get().getName()));
	}
	
	@Test
	@DisplayName("find model by name")
	@Order(4)
	void findByNameTest() {
		assertThat(model).isNotNull();
		Model found = modelRepository.findByName(model.getName());
		assertThat(found).isNotNull();
		assertThat(model.getId()).isEqualTo(found.getId());
		
	}
	
	
	@Test
	@DisplayName("find model by name ignore case")
	@Order(5)
	void findByNameIgnoreCaseTest() {
		assertThat(model).isNotNull();
		Model found = modelRepository.findByNameIgnoreCase(model.getName());
		assertThat(found).isNotNull();
		assertThat(model.getId()).isEqualTo(found.getId());
		
	}
	

	
	@Test
	@DisplayName("delete model by id")
	@Order(10)
	@Commit
	void deleteModelByIdTest() {
	    modelRepository.deleteById(model.getId());
		Optional<Model> found = modelRepository.findById(model.getId());
		assertThat(found.isEmpty()).isTrue();
		
	}
}

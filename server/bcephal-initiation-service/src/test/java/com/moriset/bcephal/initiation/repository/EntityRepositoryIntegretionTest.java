package com.moriset.bcephal.initiation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Timestamp;
import java.util.List;
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
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.initiation.domain.Entity;
import com.moriset.bcephal.initiation.domain.Model;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles(profiles = { "int", "repo" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EntityRepositoryIntegretionTest {
	
	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.initiation", "com.moriset.bcephal.multitenant.jpa",
	"com.moriset.bcephal.config" })
@EntityScan(basePackages = { "com.moriset.bcephal.domain.parameter", "com.moriset.bcephal.initiation.domain" })
@ActiveProfiles(profiles = { "int", "repo" })
static class ApplicationTests { }
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private EntityRepository entityRepository;
	
	@Autowired
	private ModelRepository modelRepository;
	
	private static Entity entity;
	private static Model model;
	
	@BeforeAll
	static void init() {
		
		model = Model.builder().name("modelName")
				.creationDate(new Timestamp(System.currentTimeMillis()))
				.modificationDate(new Timestamp(System.currentTimeMillis()))
				.build();
		entity = Entity.builder().name("entityName")
				.model(model)
				.build();
		Assertions.assertThat(entity).isNotNull();
		Assertions.assertThat(entity.getId()).isNull();
	}
	
	
	@Test
	@DisplayName("Validation Entity bean")
	@Order(1)
	void validationEntityBean() {
		assertThat(entity).isNotNull();
		
		entity.setName("");
		assertThrows(IllegalStateException.class, ()->{
			entityManager.persist(entity);
			entityManager.flush();
		});
		
		entity.setName(null);
		assertThrows(IllegalStateException.class,()->{
			entityManager.persist(entity);
			entityManager.flush();
		});
		
		entity.setName("");
		for(int i= 1; i<300; i++) {
			entity.setName(entity.getName().concat("A"));
		}
		assertThrows(IllegalStateException.class, ()->{
			entityManager.persist(entity);
			entityManager.flush();
		});
		entity.setName("");
		
		entity.setModel(null);
		assertThrows(IllegalStateException.class, ()->{
			entityManager.persist(entity);
			entityManager.flush();
		});
	}
	
	@Test
	@DisplayName("save a new Entity")
	@Order(2)
	@Commit
	void saveNewEntity() {
		assertThat(entity).isNotNull();
		Model newModel = modelRepository.save(model);
		entity.setName("entityName0");
		entity.setModel(newModel);
		entityRepository.save(entity); 
		assertThat(entity.getId()).isNotNull();
		
		Optional<Entity> found = entityRepository.findById(entity.getId());
		assertThat(found.isPresent());
	}

	@Test
	@DisplayName("update existing Entity")
	@Order(3)
	@Commit
	void updateExistingentityTest() {
		assertThat(entity).isNotNull();
		Model newModel = modelRepository.save(model);
		entity.setModel(newModel);
		entity.setName("updatedName2");
		 Entity EntityToUpdate = entityRepository.save(entity);
		assertEquals(entity.getName(), EntityToUpdate.getName());
	}
	
	@Test
	@DisplayName("find entity by name")
	@Order(4)
	@Commit
	void findEntityByNameTest() {
		assertThat(entity).isNotNull();
		Entity entityFound = entityRepository.findByName(entity.getName());
		assertThat(entityFound).isNotNull();
		assertEquals(entity.getName(), entityFound.getName());
	}
	
	@Test
	@DisplayName("find entity by id")
	@Order(4)
	@Commit
	void findEntityByIdTest() {
		entity = entityRepository.save(entity);
		assertThat(entity).isNotNull();
		Optional<Entity> entityFound = entityRepository.findById(entity.getId());
		assertThat(entityFound).isNotNull();
		assertEquals(entity.getId(), entityFound.get().getId());
	}
	
	@Test
	@DisplayName("find entity by model")
	@Order(4)
	@Commit
	void findEntityByModelTest() {
		assertThat(entity).isNotNull();
		List<Entity> entityFounds = entityRepository.findByModel(entity.getModel());
		assertThat(entityFounds).isNotEmpty();
		assertThat(entityFounds.size()).isGreaterThan(0);
	}
	
	
	@Test
	@DisplayName("Delete Entity")
	@Order(10)
	@Commit
	void deleteEntity() {
		assertThat(entity).isNotNull();
		entityRepository.delete(entity);
		modelRepository.delete(model);
		Optional<Entity> found = entityRepository.findById(entity.getId());
		assertThat(found).isEmpty();
	}
}

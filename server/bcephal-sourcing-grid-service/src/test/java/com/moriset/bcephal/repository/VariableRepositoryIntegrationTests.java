package com.moriset.bcephal.repository;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.domain.BGroup;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.Variable;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VariableRepositoryIntegrationTests {
	
	@SpringBootApplication(
			scanBasePackages = {
					"com.moriset.bcephal.sourcing.grid"
			})
	@EntityScan(
			basePackages = { 
					"com.moriset.bcephal.domain.dimension",
					"com.moriset.bcephal.domain",
					"com.moriset.bcephal.domain.filters" 
					})
	@ActiveProfiles(profiles = {"int", "repo"})
	static class ApplicationTests {};
	
	@Autowired
	private VariableRepository repository;
	
	@Autowired
	private TestEntityManager entityManager;
	
	private static Variable variable;
	
	private static BGroup group;
	
	@BeforeAll
	static void init() {
		variable = CreateFactory.buildVariable();
		assertThat(variable).isNotNull();
		assertThat(variable.getId()).isNull();
		
		assertThat(group).isNull();
	}
	
	@Test
	@DisplayName("Save a new variable")
	@Order(1)
	@Commit
	public void saveVariableTest() {
		assertThat(variable).isNotNull();
		assertThat(group).isNull();
		group = entityManager.persist(new BGroup("New bgroup .."));
		assertThat(group.getId()).isNotNull();
		variable.setGroup(group);
		repository.save(variable);
		
		assertThat(variable.getId()).isNotNull();
		Optional<Variable> found = repository.findById(variable.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(variable.getName());
	}
	
	@Test
	@DisplayName("Update an existing variable")
	@Order(2)
	@Commit
	public void updateVariableTest() {
		assertThat(variable).isNotNull();
		assertThat(variable.getId()).isNotNull();
		variable.setDescription("New description ...");
		variable.setName("New spot name ...");
		repository.save(variable);
		
		assertThat(variable.getId()).isNotNull();
		Optional<Variable> found = repository.findById(variable.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(variable.getName());
		assertThat(found.get().getDescription()).isEqualTo(variable.getDescription());
	}
	
	@Test
	@DisplayName("Find variable by id")
	@Order(3)
	public void findVariableById() {
		assertThat(variable).isNotNull();
		assertThat(variable.getId()).isNotNull();
		
		Optional<Variable> found = repository.findById(variable.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(variable.getName());
	}
	
	@Test
	@DisplayName("Find variable by name")
	@Order(4)
	public void findVariableByName() {
		assertThat(variable).isNotNull();
		assertThat(variable.getName()).isNotNull();
		
		List<Variable> listResult = repository.findByName(variable.getName());
		assertThat(listResult.size() == 1).isTrue();
		assertThat(listResult.get(0).getName()).isEqualTo(variable.getName());
	}
	
	@Test
	@DisplayName("Find generic all spot as nameables")
	@Order(5)
	public void findGenericAllSpotAsNameables() {
		assertThat(variable).isNotNull();
		assertThat(variable.getId()).isNotNull();
		
		List<Nameable> listExclude = repository.findGenericAllAsNameables();
		assertThat(listExclude.size() == 1).isTrue();
		assertThat(listExclude.get(0).getName()).isEqualTo(variable.getName());
	}
	
	@Test
	@DisplayName("Find variable by group")
	@Order(6)
	public void findVariableByGroup() {
		assertThat(group).isNotNull();
		assertThat(group.getId()).isNotNull();
		Variable variableWithGroup = repository.findByGroup(group);
		assertThat(variableWithGroup).isNotNull();
		assertThat(variableWithGroup.getGroup().getId()).isEqualTo(group.getId());
	}
	
	@Test
    @DisplayName("Delete variable by id.")
    @Order(7)
    @Commit
	public void deleteVariableById() {
    	Assertions.assertThat(variable).isNotNull();	 
	    repository.deleteById(variable.getId());
	    Optional<Variable> notfound = repository.findById(variable.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
	
	@Test
    @DisplayName("Delete group")
    @Order(8)
    @Commit
	public void deleteGroupOfVariable() {
    	Assertions.assertThat(group).isNotNull();
    	Assertions.assertThat(group.getId()).isNotNull();
    	entityManager.clear();
	    BGroup group_ = entityManager.find(BGroup.class, group.getId());
	    
	    if(group_ != null) {
	    	entityManager.remove(group_);
	    }	 
	    BGroup group__ = entityManager.find(BGroup.class, group.getId());
	    Assertions.assertThat(group__).isNull();
	}
}

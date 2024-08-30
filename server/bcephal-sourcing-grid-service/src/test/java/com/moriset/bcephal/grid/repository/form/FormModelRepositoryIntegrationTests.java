package com.moriset.bcephal.grid.repository.form;

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
import com.moriset.bcephal.grid.domain.form.FormModel;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FormModelRepositoryIntegrationTests {

	@SpringBootApplication(
			scanBasePackages = {
					"com.moriset.bcephal.sourcing.grid"
			})
	@EntityScan(
			basePackages = { 
					"com.moriset.bcephal.domain",
					"com.moriset.bcephal.grid.domain"
					})
	@ActiveProfiles(profiles = {"int", "repo"})
	static class ApplicationTests {};
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private FormModelRepository repository;
	
	private static FormModel formModel;
	
	private static BGroup group;
	
	@BeforeAll
	static void init() {
		formModel = CreateFactory.buildFormModel();
		assertThat(formModel).isNotNull();
		assertThat(formModel.getId()).isNull();
		
		assertThat(group).isNull();
	}
	
	@Test
	@DisplayName("Save a new formModel")
	@Order(1)
	@Commit
	public void saveFormModelTest() {
		assertThat(formModel).isNotNull();
		assertThat(group).isNull();
		group = entityManager.persist(new BGroup("New bgroup .."));
		entityManager.flush();
		assertThat(group.getId()).isNotNull();
		formModel.setGroup(group);
		repository.save(formModel);
		
		assertThat(formModel.getId()).isNotNull();
		Optional<FormModel> found = repository.findById(formModel.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(formModel.getName());
	}
	
	@Test
	@DisplayName("Update an existing formModel")
	@Order(2)
	@Commit
	public void updateFormModelTest() {
		assertThat(formModel).isNotNull();
		assertThat(formModel.getId()).isNotNull();
		formModel.setDescription("New description ...");
		formModel.setName("New grille name ...");
		repository.save(formModel);
		
		assertThat(formModel.getId()).isNotNull();
		Optional<FormModel> found = repository.findById(formModel.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(formModel.getName());
		assertThat(found.get().getDescription()).isEqualTo(formModel.getDescription());
	}
	
	@Test
	@DisplayName("Find formModel by id")
	@Order(3)
	public void findFormModelById() {
		assertThat(formModel).isNotNull();
		assertThat(formModel.getId()).isNotNull();
		
		Optional<FormModel> found = repository.findById(formModel.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(formModel.getName());
	}
	

	@Test
	@DisplayName("Find formModel by name")
	@Order(4)
	public void findFormModelByName() {
		assertThat(formModel).isNotNull();
		assertThat(formModel.getName()).isNotNull();
		
		List<FormModel> listResult = repository.findByName(formModel.getName());
		assertThat(listResult.size() == 1).isTrue();
		assertThat(listResult.get(0).getName()).isEqualTo(formModel.getName());
		assertThat(listResult.get(0).getDescription()).isEqualTo(formModel.getDescription());
	}
	
	@Test
	@DisplayName("Find formModel by group")
	@Order(5)
	public void findFormModelByGroup() {
		assertThat(group).isNotNull();
		assertThat(group.getId()).isNotNull();
		FormModel formModelWithGroup = repository.findByGroup(group);
		assertThat(formModelWithGroup).isNotNull();
		assertThat(formModelWithGroup.getGroup().getId()).isEqualTo(group.getId());
	}
	
	@Test
	@DisplayName("Find generic all formModel grid as nameables")
	@Order(6)
	public void findGenericAllformModelAsNameables() {
		assertThat(formModel).isNotNull();
		assertThat(formModel.getId()).isNotNull();
		
		List<Nameable> listExclude = repository.findGenericAllAsNameables();
		assertThat(listExclude.size() == 1).isTrue();
		assertThat(listExclude.get(0).getName()).isEqualTo(formModel.getName());
	}
	
	
	@Test
    @DisplayName("Delete formModel by id.")
    @Order(7)
    @Commit
	public void deleteFormModelById() {
    	Assertions.assertThat(formModel).isNotNull();	 
    	repository.deleteById(formModel.getId());
	    Optional<FormModel> notfound = repository.findById(formModel.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
	
	@Test
    @DisplayName("Delete group")
    @Order(8)
    @Commit
	public void deleteGroupOfSpot() {
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

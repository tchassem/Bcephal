package com.moriset.bcephal.grid.repository;

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
import com.moriset.bcephal.grid.domain.GrilleCategory;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MaterializedGridRepositoryIntegrationTests {
	
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
	private MaterializedGridRepository gridRepository;
	
	private static BGroup group;
	
	private static MaterializedGrid matGrid;
	
	@BeforeAll
	static void init() {
		matGrid = CreateFactory.buildMaterialize();
		assertThat(matGrid).isNotNull();
		assertThat(matGrid.getId()).isNull();
		
		assertThat(group).isNull();
	}
	
	@Test
	@DisplayName("Save a new materialize grid")
	@Order(1)
	@Commit
	public void saveMaterializedGridTest() {
		assertThat(matGrid).isNotNull();
		assertThat(group).isNull();
		group = entityManager.persist(new BGroup("New bgroup .."));
		assertThat(group.getId()).isNotNull();
		matGrid.setGroup(group);
		gridRepository.save(matGrid);
		
		assertThat(matGrid.getId()).isNotNull();
		Optional<MaterializedGrid> found = gridRepository.findById(matGrid.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(matGrid.getName());
	}
	
	@Test
	@DisplayName("Update an existing materialize grid")
	@Order(2)
	@Commit
	public void updateMaterializedGridTest() {
		assertThat(matGrid).isNotNull();
		assertThat(matGrid.getId()).isNotNull();
		matGrid.setDescription("New description ...");
		matGrid.setName("New materialize grid name ...");
		gridRepository.save(matGrid);
		
		assertThat(matGrid.getId()).isNotNull();
		Optional<MaterializedGrid> found = gridRepository.findById(matGrid.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(matGrid.getName());
		assertThat(found.get().getDescription()).isEqualTo(matGrid.getDescription());
	}
	
	@Test
	@DisplayName("Find materialize grid by id")
	@Order(3)
	public void findMaterializedGridById() {
		assertThat(matGrid).isNotNull();
		assertThat(matGrid.getId()).isNotNull();
		
		Optional<MaterializedGrid> found = gridRepository.findById(matGrid.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(matGrid.getName());
	}
	
	@Test
	@DisplayName("Find materialize grid by name")
	@Order(4)
	public void findMaterializedGridByName() {
		assertThat(matGrid).isNotNull();
		assertThat(matGrid.getName()).isNotNull();
		
		List<MaterializedGrid> listResult = gridRepository.findByName(matGrid.getName());
		assertThat(listResult.size() == 1).isTrue();
		assertThat(listResult.get(0).getName()).isEqualTo(matGrid.getName());
	}

	@Test
	@DisplayName("Find materialize grid by name and category")
	@Order(5)
	public void findByNameAndCategory() {
		assertThat(matGrid).isNotNull();
		assertThat(matGrid.getName()).isNotNull();
		
		List<MaterializedGrid> listResult = gridRepository.findByNameAndCategory(matGrid.getName(), GrilleCategory.USER);
		assertThat(listResult.size() == 1).isTrue();
		assertThat(listResult.get(0).getCategory()).isEqualTo(matGrid.getCategory());
	}
	
	@Test
	@DisplayName("Find all materialize grid as nameables")
	@Order(6)
	public void findAllMaterializedGridAsNameables() {
		assertThat(matGrid).isNotNull();
		
		List<Nameable> listResult = gridRepository.findAllAsNameables();
		assertThat(listResult.size() == 1).isTrue();
		assertThat(listResult.get(0).getName()).isEqualTo(matGrid.getName());
	}
	
	@Test
	@DisplayName("Find all materialize grid as nameables exclude ids")
	@Order(7)
	public void findAllMaterializedGridAsNameablesExcludeIds() {
		assertThat(matGrid).isNotNull();
		assertThat(matGrid.getId()).isNotNull();
		
		List<Long> excludeIds = List.of(matGrid.getId());
		List<Nameable> listExclude = gridRepository.findAllAsNameablesExcludeIds(excludeIds);
		assertThat(listExclude.size() == 0).isTrue();
	}
	
	@Test
	@DisplayName("Find generic all materialize grid as nameables")
	@Order(8)
	public void findGenericAllMaterializedGridAsNameables() {
		assertThat(matGrid).isNotNull();
		assertThat(matGrid.getId()).isNotNull();
		
		List<Nameable> listExclude = gridRepository.findGenericAllAsNameables();
		assertThat(listExclude.size() == 1).isTrue();
		assertThat(listExclude.get(0).getName()).isEqualTo(matGrid.getName());
	}
	
	@Test
	@DisplayName("Find materialize grid by group")
	@Order(9)
	public void findMaterializedGridByGroup() {
		assertThat(group).isNotNull();
		assertThat(group.getId()).isNotNull();
		MaterializedGrid MaterializedGridWithGroup = gridRepository.findByGroup(group);
		assertThat(MaterializedGridWithGroup).isNotNull();
		assertThat(MaterializedGridWithGroup.getGroup().getId()).isEqualTo(group.getId());
	}
	
	@Test
    @DisplayName("Delete materialize grid by id.")
    @Order(10)
    @Commit
	public void deleteMaterializedGridById() {
    	Assertions.assertThat(matGrid).isNotNull();	 
    	gridRepository.deleteById(matGrid.getId());
	    Optional<MaterializedGrid> notfound = gridRepository.findById(matGrid.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
	
	@Test
    @DisplayName("Delete group")
    @Order(11)
    @Commit
	public void deleteGroupOfMaterializeGrid() {
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

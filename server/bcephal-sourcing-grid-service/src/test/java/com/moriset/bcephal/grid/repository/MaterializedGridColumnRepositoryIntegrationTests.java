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

import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MaterializedGridColumnRepositoryIntegrationTests {

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
	private MaterializedGridColumnRepository repository;
	
	private static MaterializedGridColumn column;

	private static MaterializedGrid grille;
	
	@BeforeAll
	static void init() {
		column = CreateFactory.buildMaterializedGridColumn();
		assertThat(column).isNotNull();
		assertThat(column.getId()).isNull();
		
		assertThat(grille).isNull();
	}
	
	@Test
	@DisplayName("Save a new materialized grid column")
	@Order(1)
	@Commit
	public void saveMaterializedGridColumnTest() {
		assertThat(column).isNotNull();
		MaterializedGrid grid = CreateFactory.buildMaterialize();
		grille = entityManager.persist(grid);
		entityManager.flush();
		assertThat(grille.getId()).isNotNull();
		column.setGrid(grille);
		repository.save(column);
		
		assertThat(column.getId()).isNotNull();
		Optional<MaterializedGridColumn> found = repository.findById(column.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(column.getName());
	}
	
	@Test
	@DisplayName("Update an existing materialized grid column")
	@Order(2)
	@Commit
	public void updateMaterializedGridColumnTest() {
		assertThat(column).isNotNull();
		assertThat(column.getId()).isNotNull();
		column.setName("New MaterializedGridColumn name ...");
		repository.save(column);
		
		assertThat(column.getId()).isNotNull();
		Optional<MaterializedGridColumn> found = repository.findById(column.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(column.getName());
	}
	
	@Test
	@DisplayName("Find materialized grid column by id")
	@Order(3)
	public void findMaterializedGridColumnById() {
		assertThat(column).isNotNull();
		assertThat(column.getId()).isNotNull();
		
		Optional<MaterializedGridColumn> found = repository.findById(column.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(column.getName());
	}	

	@Test
	@DisplayName("Find materialized grid column by gridId")
	@Order(5)
	public void findMaterializedGridColumnByGridId() {
		assertThat(column).isNotNull();
		assertThat(column.getName()).isNotNull();
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		List<MaterializedGridColumn> listResult = repository.findByGrid(grille.getId());
		assertThat(listResult.size() == 1).isTrue();
		assertThat(listResult.get(0).getName()).isEqualTo(column.getName());
	}
	
	@Test
	@DisplayName("Find materialized grid column ids by gridId")
	@Order(6)
	public void findMaterializedGridColumnIdsByGridId() {
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		List<Long> listResult = repository.getColumnIds(grille.getId());
		assertThat(listResult.size() == 1).isTrue();
	}
	
	@Test
    @DisplayName("Delete materialized grid column")
    @Order(7)
    @Commit
	public void deleteMaterializedGridColumnById() {
    	Assertions.assertThat(column).isNotNull();	 
    	repository.deleteById(column.getId());
	    Optional<MaterializedGridColumn> notfound = repository.findById(column.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
	
	@Test
    @DisplayName("Delete materialized grid")
    @Order(8)
    @Commit
	public void deleteGroupOfSpot() {
    	Assertions.assertThat(grille).isNotNull();
    	Assertions.assertThat(grille.getId()).isNotNull();
    	entityManager.clear();
    	MaterializedGrid grille_ = entityManager.find(MaterializedGrid.class, grille.getId());
	    
	    if(grille_ != null) {
	    	entityManager.remove(grille_);
	    }	 
	    MaterializedGrid group__ = entityManager.find(MaterializedGrid.class, grille.getId());
	    Assertions.assertThat(group__).isNull();
	}
}

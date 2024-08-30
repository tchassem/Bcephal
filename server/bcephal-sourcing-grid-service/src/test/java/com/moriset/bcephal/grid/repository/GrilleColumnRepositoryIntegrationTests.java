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

import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GrilleColumnRepositoryIntegrationTests {

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
	private GrilleColumnRepository columnRepository;
	
	private static GrilleColumn column;
	
	private static Grille grille;
	
	@BeforeAll
	static void init() {
		column = CreateFactory.buildGrilleColumn();
		assertThat(column).isNotNull();
		assertThat(column.getId()).isNull();
		
		assertThat(grille).isNull();
	}
	
	@Test
	@DisplayName("Save a new grilleColumn")
	@Order(1)
	@Commit
	public void saveGrilleColumnTest() {
		assertThat(column).isNotNull();
		Grille grid = CreateFactory.buildGrille();
		grille = entityManager.persist(grid);
		entityManager.flush();
		assertThat(grille.getId()).isNotNull();
		column.setGrid(grille);
		columnRepository.save(column);
		
		assertThat(column.getId()).isNotNull();
		Optional<GrilleColumn> found = columnRepository.findById(column.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(column.getName());
	}
	
	@Test
	@DisplayName("Update an existing grilleColumn")
	@Order(2)
	@Commit
	public void updateGrilleColumnTest() {
		assertThat(column).isNotNull();
		assertThat(column.getId()).isNotNull();
		column.setName("New grilleColumn name ...");
		columnRepository.save(column);
		
		assertThat(column.getId()).isNotNull();
		Optional<GrilleColumn> found = columnRepository.findById(column.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(column.getName());
	}
	
	@Test
	@DisplayName("Find grilleColumn by id")
	@Order(3)
	public void findGrilleColumnById() {
		assertThat(column).isNotNull();
		assertThat(column.getId()).isNotNull();
		
		Optional<GrilleColumn> found = columnRepository.findById(column.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(column.getName());
	}
	
	@Test
	@DisplayName("Find grilleColumn by grid")
	@Order(4)
	public void findGrilleColumnByGrid() {
		assertThat(column).isNotNull();
		assertThat(column.getName()).isNotNull();
		
		List<GrilleColumn> listResult = columnRepository.findByGrid(grille);
		assertThat(listResult.size() == 1).isTrue();
		assertThat(listResult.get(0).getName()).isEqualTo(column.getName());
	}
	

	@Test
	@DisplayName("Find grilleColumn by gridId")
	@Order(5)
	public void findGrilleColumnByGridId() {
		assertThat(column).isNotNull();
		assertThat(column.getName()).isNotNull();
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		List<GrilleColumn> listResult = columnRepository.findByGrid(grille.getId());
		assertThat(listResult.size() == 1).isTrue();
		assertThat(listResult.get(0).getName()).isEqualTo(column.getName());
	}
	
	@Test
	@DisplayName("Find grilleColumn ids by gridId")
	@Order(6)
	public void findGrilleColumnIdsByGridId() {
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		List<Long> listResult = columnRepository.getColumnIds(grille.getId());
		assertThat(listResult.size() == 1).isTrue();
	}
	
	@Test
    @DisplayName("Delete grille column")
    @Order(7)
    @Commit
	public void deleteGrilleById() {
    	Assertions.assertThat(column).isNotNull();	 
    	columnRepository.deleteById(column.getId());
	    Optional<GrilleColumn> notfound = columnRepository.findById(column.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
	
	@Test
    @DisplayName("Delete grille")
    @Order(8)
    @Commit
	public void deleteGroupOfSpot() {
    	Assertions.assertThat(grille).isNotNull();
    	Assertions.assertThat(grille.getId()).isNotNull();
    	entityManager.clear();
	    Grille grille_ = entityManager.find(Grille.class, grille.getId());
	    
	    if(grille_ != null) {
	    	entityManager.remove(grille_);
	    }	 
	    Grille group__ = entityManager.find(Grille.class, grille.getId());
	    Assertions.assertThat(group__).isNull();
	}
}

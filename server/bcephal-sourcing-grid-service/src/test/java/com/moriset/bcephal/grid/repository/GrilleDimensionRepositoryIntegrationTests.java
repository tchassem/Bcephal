package com.moriset.bcephal.grid.repository;

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

import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleDimension;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GrilleDimensionRepositoryIntegrationTests {

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
	private GrilleDimensionRepository repository;
	
	private static GrilleDimension dimension;
	
	private static Grille grille;
	
	@BeforeAll
	static void init() {
		dimension = CreateFactory.buildGrilleDimension();
		assertThat(dimension).isNotNull();
		assertThat(dimension.getId()).isNull();
		
		assertThat(grille).isNull();
	}
	
	@Test
	@DisplayName("Save a new grille dimension")
	@Order(1)
	@Commit
	public void saveGrilleDimensionTest() {
		assertThat(dimension).isNotNull();
		Grille grid = CreateFactory.buildGrille();
		grille = entityManager.persist(grid);
		entityManager.flush();
		assertThat(grille.getId()).isNotNull();
		dimension.setGrid(grille);
		
		repository.save(dimension);
		
		assertThat(dimension.getId()).isNotNull();
		Optional<GrilleDimension> found = repository.findById(dimension.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(dimension.getName());
	}
	
	@Test
	@DisplayName("Update an existing grille dimension")
	@Order(2)
	@Commit
	public void updateGrilleDimensionTest() {
		assertThat(dimension).isNotNull();
		assertThat(dimension.getId()).isNotNull();
		
		dimension.setName("New grille dimension name ...");
		repository.save(dimension);
		
		assertThat(dimension.getId()).isNotNull();
		Optional<GrilleDimension> found = repository.findById(dimension.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(dimension.getName());
	}
	
	@Test
	@DisplayName("Find grille dimension by id")
	@Order(3)
	public void findGrilleDimensionById() {
		assertThat(dimension).isNotNull();
		assertThat(dimension.getId()).isNotNull();
		
		Optional<GrilleDimension> found = repository.findById(dimension.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(dimension.getName());
	}	
	
	@Test
    @DisplayName("Delete grille dimension")
    @Order(4)
    @Commit
	public void deleteGrilleDimensionById() {
    	Assertions.assertThat(dimension).isNotNull();	 
    	repository.deleteById(dimension.getId());
	    Optional<GrilleDimension> notfound = repository.findById(dimension.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
	
	@Test
    @DisplayName("Delete grille")
    @Order(5)
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

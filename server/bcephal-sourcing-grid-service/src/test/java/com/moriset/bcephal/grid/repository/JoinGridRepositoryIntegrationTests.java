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
import com.moriset.bcephal.grid.domain.JoinGrid;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinGridRepositoryIntegrationTests {

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
	private JoinGridRepository repository;
	
	@Autowired
	private TestEntityManager entityManager;
	
	private static JoinGrid joinGrille;
	
	private static Grille grille;
	
	@BeforeAll
	static void init() {
		joinGrille = CreateFactory.buildJoinGrid();
		assertThat(joinGrille).isNotNull();
		assertThat(joinGrille.getId()).isNull();
		
		assertThat(grille).isNull();
	}
	
	@Test
	@DisplayName("Save a new join-grille")
	@Order(1)
	@Commit
	public void saveJoinGrilleTest() {
		assertThat(joinGrille).isNotNull();
		assertThat(grille).isNull();
		
		grille = entityManager.persist(CreateFactory.buildGrille());
		entityManager.flush();
		assertThat(grille.getId()).isNotNull();
		joinGrille.setGridId(grille.getId());
		
		repository.save(joinGrille);
		
		assertThat(joinGrille.getId()).isNotNull();
		Optional<JoinGrid> found = repository.findById(joinGrille.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(joinGrille.getName());
	}
	
	@Test
	@DisplayName("Update an existing join-grille")
	@Order(2)
	@Commit
	public void updateJoinGrilleTest() {
		assertThat(joinGrille).isNotNull();
		assertThat(joinGrille.getId()).isNotNull();
		joinGrille.setName("New grille name ...");
		joinGrille.setMainGrid(true);
		repository.save(joinGrille);
		
		assertThat(joinGrille.getId()).isNotNull();
		Optional<JoinGrid> found = repository.findById(joinGrille.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getMainGrid()).isTrue();
		assertThat(found.get().getName()).isEqualTo(joinGrille.getName());
	}
	@Test
	@DisplayName("Find join-grille by gridId and gridType and joinId")
	@Order(3)
	public void findGridIdAndGridTypeAndJoinId() {
		assertThat(joinGrille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		assertThat(joinGrille.getId()).isNotNull();
		
		Optional<JoinGrid> found = repository.findByGridIdAndGridTypeAndJoinId(grille.getId(), joinGrille.getGridType(), joinGrille.getJoinId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(joinGrille.getName());
	}
	@Test
    @DisplayName("Delete join-grille by id.")
    @Order(4)
    @Commit
	public void deleteGrilleById() {
    	Assertions.assertThat(joinGrille).isNotNull();	 
    	repository.deleteById(joinGrille.getId());
	    Optional<JoinGrid> notfound = repository.findById(joinGrille.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
	
	@Test
    @DisplayName("Delete grille")
    @Order(5)
    @Commit
	public void deleteGroupOfSpot() {
    	Assertions.assertThat(grille).isNotNull();
    	Assertions.assertThat(grille).isNotNull();
    	entityManager.clear();
    	Grille grille_ = entityManager.find(Grille.class, grille.getId());
	    
	    if(grille_ != null) {
	    	entityManager.remove(grille_);
	    }	 
	    Grille grille__ = entityManager.find(Grille.class, grille.getId());
	    Assertions.assertThat(grille__).isNull();
	}
}

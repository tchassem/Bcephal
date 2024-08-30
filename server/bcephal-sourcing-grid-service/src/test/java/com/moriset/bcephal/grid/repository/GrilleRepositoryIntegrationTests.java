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
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleStatus;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GrilleRepositoryIntegrationTests {

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
	private GrilleRepository grilleRepository;
	
	
	private static BGroup group;
	
	private static Grille grille;
	
	@BeforeAll
	static void init() {
		grille = CreateFactory.buildGrille();
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNull();
		
		assertThat(group).isNull();
	}
	
	@Test
	@DisplayName("Save a new grille")
	@Order(1)
	@Commit
	public void saveGrilleTest() {
		assertThat(grille).isNotNull();
		assertThat(group).isNull();
		group = entityManager.persist(new BGroup("New bgroup .."));
		entityManager.flush();
		assertThat(group.getId()).isNotNull();
		grille.setGroup(group);
		grilleRepository.save(grille);
		
		assertThat(grille.getId()).isNotNull();
		Optional<Grille> found = grilleRepository.findById(grille.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(grille.getName());
	}
	
	@Test
	@DisplayName("Update an existing grille")
	@Order(2)
	@Commit
	public void updateGrilleTest() {
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		grille.setDescription("New description ...");
		grille.setName("New grille name ...");
		grille.setType(GrilleType.REPORT);
		grilleRepository.save(grille);
		
		assertThat(grille.getId()).isNotNull();
		Optional<Grille> found = grilleRepository.findById(grille.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(grille.getName());
		assertThat(found.get().getDescription()).isEqualTo(grille.getDescription());
	}
	

	@Test
	@DisplayName("Change status grille")
	@Order(3)
	@Commit
	public void changeStatusTest() {
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		int response = grilleRepository.changeStatus(grille.getId(), GrilleStatus.UNLOADED);
		assertThat(response == 1).isTrue();
		Optional<Grille> found = grilleRepository.findById(grille.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getStatus()).isEqualTo(GrilleStatus.UNLOADED);
	}
	
	@Test
	@DisplayName("Find grille by id")
	@Order(4)
	public void findGrilleById() {
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		Optional<Grille> found = grilleRepository.findById(grille.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(grille.getName());
	}
	
	@Test
	@DisplayName("Find grille by type")
	@Order(5)
	public void findGrilleByType() {
		assertThat(grille).isNotNull();
		assertThat(grille.getName()).isNotNull();
		
		List<Grille> listResult = grilleRepository.findByName(grille.getName());
		assertThat(listResult.size() == 1).isTrue();
		assertThat(listResult.get(0).getType()).isEqualTo(grille.getType());
	}
	

	@Test
	@DisplayName("Find grille by name and type")
	@Order(6)
	public void findGrilleByNameAndType() {
		assertThat(grille).isNotNull();
		assertThat(grille.getName()).isNotNull();
		
		List<Grille> listResult = grilleRepository.findByNameAndType(grille.getName(), grille.getType());
		assertThat(listResult.size() == 1).isTrue();
		assertThat(listResult.get(0).getName()).isEqualTo(grille.getName());
		assertThat(listResult.get(0).getType()).isEqualTo(grille.getType());
	}
	
	@Test
	@DisplayName("Find all grille as nameables exclude type")
	@Order(7)
	public void findAllGrilleAsNameablesExcludeIds() {
		assertThat(grille).isNotNull();
		assertThat(grille.getId()).isNotNull();
		
		List<Long> excludeIds = List.of(grille.getId());
		List<Nameable> listExclude = grilleRepository.findByTypeExclude(grille.getType(), excludeIds);
		assertThat(listExclude.size() == 0).isTrue();
	}
	
	@Test
	@DisplayName("Find grille by group")
	@Order(8)
	public void findGrilleByGroup() {
		assertThat(group).isNotNull();
		assertThat(group.getId()).isNotNull();
		Grille grilleWithGroup = grilleRepository.findByGroup(group);
		assertThat(grilleWithGroup).isNotNull();
		assertThat(grilleWithGroup.getGroup().getId()).isEqualTo(group.getId());
	}
	
	@Test
    @DisplayName("Delete grille by id.")
    @Order(9)
    @Commit
	public void deleteGrilleById() {
    	Assertions.assertThat(grille).isNotNull();	 
    	grilleRepository.deleteById(grille.getId());
	    Optional<Grille> notfound = grilleRepository.findById(grille.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
	
	@Test
    @DisplayName("Delete group")
    @Order(10)
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

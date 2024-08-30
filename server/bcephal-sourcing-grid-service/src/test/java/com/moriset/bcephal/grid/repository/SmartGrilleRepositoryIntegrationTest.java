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
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.grid.domain.SmartGrille;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SmartGrilleRepositoryIntegrationTest {

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
	private SmartGrilleRepository repository; 
	
	private static SmartGrille smartGrille;
	
	@BeforeAll
	static void init() {
		smartGrille = CreateFactory.buildSmartGrille();
		assertThat(smartGrille).isNotNull();
		assertThat(smartGrille.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new smart-grille")
	@Order(1)
	@Commit
	public void saveSmartGrilleTest() {
		assertThat(smartGrille).isNotNull();
		repository.save(smartGrille);
		
		assertThat(smartGrille.getId()).isNotNull();
		Optional<SmartGrille> found = repository.findById(smartGrille.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(smartGrille.getName());
	}
	
	@Test
	@DisplayName("Update an existing smart-grille")
	@Order(2)
	@Commit
	public void updateSmartGrilleTest() {
		assertThat(smartGrille).isNotNull();
		assertThat(smartGrille.getId()).isNotNull();
		smartGrille.setName("New smart-grille name ...");
		smartGrille.setGridType(JoinGridType.JOIN);
		repository.save(smartGrille);
		
		assertThat(smartGrille.getId()).isNotNull();
		Optional<SmartGrille> found = repository.findById(smartGrille.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(smartGrille.getName());
	}
	
	@Test
	@DisplayName("Find smart-grille by type")
	@Order(3)
	public void findSmartGrilleByType() {
		assertThat(smartGrille).isNotNull();
		assertThat(smartGrille.getName()).isNotNull();
		
		List<Nameable> listResult = repository.findByType(smartGrille.getType());
		assertThat(listResult.size() == 1).isTrue();
		assertThat(listResult.get(0).getName()).isEqualTo(smartGrille.getName());
	}
	
	@Test
	@DisplayName("Find smart-grille by types")
	@Order(3)
	public void findSmartGrilleByGrilleTypes() {
		assertThat(smartGrille).isNotNull();
		assertThat(smartGrille.getId()).isNotNull();
		
		List<GrilleType> types = List.of(GrilleType.INPUT, GrilleType.RECONCILIATION);
		List<SmartGrille> listResult = repository.findByTypes(types);
		assertThat(listResult.size() == 1).isTrue();
		assertThat(listResult.get(0).getType()).isEqualTo(smartGrille.getType());
	}
	
	@Test
	@DisplayName("Find smart-grille by types all exclude")
	@Order(4)
	public void findByTypesAllExclude() {
		assertThat(smartGrille).isNotNull();
		assertThat(smartGrille.getId()).isNotNull();
		
		List<Long> excludeIds = List.of(smartGrille.getId());
		List<GrilleType> types = List.of(GrilleType.INPUT, GrilleType.RECONCILIATION);
		List<SmartGrille> listExclude = repository.findByTypesAllExclude(types, excludeIds);
		assertThat(listExclude.size() == 0).isTrue();
	}
	
	@Test
	@DisplayName("Find smart-grille name by id")
	@Order(5)
	public void findNameById() {
		assertThat(smartGrille).isNotNull();
		assertThat(smartGrille.getId()).isNotNull();
		
		String found = repository.findNameById(smartGrille.getId());
		assertThat(StringUtils.hasLength(found)).isTrue();
		assertThat(found).isEqualTo(smartGrille.getName());
	}
	
	@Test
    @DisplayName("Delete smart-grille by id.")
    @Order(6)
    @Commit
	public void deleteSmartGrilleById() {
    	Assertions.assertThat(smartGrille).isNotNull();	 
    	repository.deleteById(smartGrille.getId());
	    Optional<SmartGrille> notfound = repository.findById(smartGrille.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}

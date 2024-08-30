package com.moriset.bcephal.repository.filters;

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
import com.moriset.bcephal.domain.dimension.Spot;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpotRepositoryIntegrationTests {
	
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
	private SpotRepository spotRepository;
	
	@Autowired
	private TestEntityManager entityManager;
	
	private static Spot spot;
	
	private static BGroup group;
	
	@BeforeAll
	static void init() {
		spot = CreateFactory.buildSpot();
		assertThat(spot).isNotNull();
		assertThat(spot.getId()).isNull();
		
		assertThat(group).isNull();
	}
	
	@Test
	@DisplayName("Save a new spot")
	@Order(1)
	@Commit
	public void saveSpotTest() {
		assertThat(spot).isNotNull();
		assertThat(group).isNull();
		group = entityManager.persist(new BGroup("New bgroup .."));
		assertThat(group.getId()).isNotNull();
		spot.setGroup(group);
		spotRepository.save(spot);
		
		assertThat(spot.getId()).isNotNull();
		Optional<Spot> found = spotRepository.findById(spot.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(spot.getName());
	}
	
	@Test
	@DisplayName("Update an existing spot")
	@Order(2)
	@Commit
	public void updateSpotTest() {
		assertThat(spot).isNotNull();
		assertThat(spot.getId()).isNotNull();
		spot.setDescription("New description ...");
		spot.setName("New spot name ...");
		spotRepository.save(spot);
		
		assertThat(spot.getId()).isNotNull();
		Optional<Spot> found = spotRepository.findById(spot.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(spot.getName());
		assertThat(found.get().getDescription()).isEqualTo(spot.getDescription());
	}
	
	@Test
	@DisplayName("Find spot by id")
	@Order(3)
	public void findSpotById() {
		assertThat(spot).isNotNull();
		assertThat(spot.getId()).isNotNull();
		
		Optional<Spot> found = spotRepository.findById(spot.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(spot.getName());
	}
	
	@Test
	@DisplayName("Find spot by name")
	@Order(4)
	public void findSpotByName() {
		assertThat(spot).isNotNull();
		assertThat(spot.getName()).isNotNull();
		
		List<Spot> listResult = spotRepository.findByName(spot.getName());
		assertThat(listResult.size() == 1).isTrue();
		assertThat(listResult.get(0).getName()).isEqualTo(spot.getName());
	}
	
	@Test
	@DisplayName("Find all spot as nameables")
	@Order(5)
	public void findAllSpotAsNameables() {
		assertThat(spot).isNotNull();
		
		List<Nameable> listResult = spotRepository.findAllAsNameables();
		assertThat(listResult.size() == 1).isTrue();
		assertThat(listResult.get(0).getName()).isEqualTo(spot.getName());
	}
	
	@Test
	@DisplayName("Find all spot as nameables exclude ids")
	@Order(6)
	public void findAllSpotAsNameablesExcludeIds() {
		assertThat(spot).isNotNull();
		assertThat(spot.getId()).isNotNull();
		
		List<Long> excludeIds = List.of(spot.getId());
		List<Nameable> listExclude = spotRepository.findAllAsNameablesExcludeIds(excludeIds);
		assertThat(listExclude.size() == 0).isTrue();
	}
	
	@Test
	@DisplayName("Find generic all spot as nameables")
	@Order(7)
	public void findGenericAllSpotAsNameables() {
		assertThat(spot).isNotNull();
		assertThat(spot.getId()).isNotNull();
		
		List<Nameable> listExclude = spotRepository.findGenericAllAsNameables();
		assertThat(listExclude.size() == 1).isTrue();
		assertThat(listExclude.get(0).getName()).isEqualTo(spot.getName());
	}
	
	@Test
	@DisplayName("Find spot by group")
	@Order(8)
	public void findSpotByGroup() {
		assertThat(group).isNotNull();
		assertThat(group.getId()).isNotNull();
		Spot spotWithGroup = spotRepository.findByGroup(group);
		assertThat(spotWithGroup).isNotNull();
		assertThat(spotWithGroup.getGroup().getId()).isEqualTo(group.getId());
	}
	
	@Test
    @DisplayName("Delete spot by id.")
    @Order(9)
    @Commit
	public void deleteSpotById() {
    	Assertions.assertThat(spot).isNotNull();	 
	    spotRepository.deleteById(spot.getId());
	    Optional<Spot> notfound = spotRepository.findById(spot.getId());	 
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

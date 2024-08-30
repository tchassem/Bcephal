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

import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.grid.domain.SmartMaterializedGrid;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SmartMaterializedGridRepositoryIntegrationTests {

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
	private SmartMaterializedGridRepository repository;
	
private static SmartMaterializedGrid smartMat;
	
	@BeforeAll
	static void init() {
		smartMat = CreateFactory.buildSmartMaterializedGrid();
		assertThat(smartMat).isNotNull();
		assertThat(smartMat.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new smart-materialized-grid")
	@Order(1)
	@Commit
	public void saveSmartMaterializedGridTest() {
		assertThat(smartMat).isNotNull();
		repository.save(smartMat);
		
		assertThat(smartMat.getId()).isNotNull();
		Optional<SmartMaterializedGrid> found = repository.findById(smartMat.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(smartMat.getName());
	}
	
	@Test
	@DisplayName("Update an existing smart-materialized-grid")
	@Order(2)
	@Commit
	public void updatesSmartMaterializedGridTest() {
		assertThat(smartMat).isNotNull();
		assertThat(smartMat.getId()).isNotNull();
		smartMat.setName("New smart-join name ...");
		smartMat.setGridType(JoinGridType.GRID);
		repository.save(smartMat);
		
		assertThat(smartMat.getId()).isNotNull();
		Optional<SmartMaterializedGrid> found = repository.findById(smartMat.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(smartMat.getName());
	}
	
	@Test
	@DisplayName("Find smart-materialized-grid all exclude ids")
	@Order(3)
	public void findAllExcludeIds() {
		assertThat(smartMat).isNotNull();
		assertThat(smartMat.getId()).isNotNull();
		
		List<Long> excludeIds = List.of(smartMat.getId(), 25L);
		List<SmartMaterializedGrid> listExclude = repository.findAllExclude(excludeIds);
		assertThat(listExclude.size() == 0).isTrue();
	}
	
	@Test
	@DisplayName("Find smart-materialized-grid name by id")
	@Order(4)
	public void findNameById() {
		assertThat(smartMat).isNotNull();
		assertThat(smartMat.getId()).isNotNull();
		
		String found = repository.findNameById(smartMat.getId());
		assertThat(StringUtils.hasLength(found)).isTrue();
		assertThat(found).isEqualTo(smartMat.getName());
	}
	
	@Test
    @DisplayName("Delete smart-materialized-grid by id.")
    @Order(5)
    @Commit
	public void deleteSmartMaterializedGridById() {
    	Assertions.assertThat(smartMat).isNotNull();	 
    	repository.deleteById(smartMat.getId());
	    Optional<SmartMaterializedGrid> notfound = repository.findById(smartMat.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}

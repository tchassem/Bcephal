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
import com.moriset.bcephal.grid.domain.SmartJoin;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SmartJoinRepositoryIntegrationTest {

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
	private SmartJoinRepository repository;
	
	private static SmartJoin smartJoin;
	
	@BeforeAll
	static void init() {
		smartJoin = CreateFactory.buildSmartJoin();
		assertThat(smartJoin).isNotNull();
		assertThat(smartJoin.getId()).isNull();
	}
	
	@Test
	@DisplayName("Save a new smart-join")
	@Order(1)
	@Commit
	public void saveSmartJoinTest() {
		assertThat(smartJoin).isNotNull();
		repository.save(smartJoin);
		
		assertThat(smartJoin.getId()).isNotNull();
		Optional<SmartJoin> found = repository.findById(smartJoin.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(smartJoin.getName());
	}
	
	@Test
	@DisplayName("Update an existing smart-join")
	@Order(2)
	@Commit
	public void updateSmartJoinTest() {
		assertThat(smartJoin).isNotNull();
		assertThat(smartJoin.getId()).isNotNull();
		smartJoin.setName("New smart-join name ...");
		smartJoin.setGridType(JoinGridType.GRID);
		repository.save(smartJoin);
		
		assertThat(smartJoin.getId()).isNotNull();
		Optional<SmartJoin> found = repository.findById(smartJoin.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(smartJoin.getName());
	}
	
	@Test
	@DisplayName("Find smart-join all exclude id")
	@Order(3)
	public void findAllExcludeId() {
		assertThat(smartJoin).isNotNull();
		assertThat(smartJoin.getId()).isNotNull();
		
		List<SmartJoin> listExclude = repository.findAllExclude(smartJoin.getId());
		assertThat(listExclude.size() == 0).isTrue();
	}
	
	@Test
	@DisplayName("Find smart-join all exclude ids")
	@Order(4)
	public void findAllExcludeIds() {
		assertThat(smartJoin).isNotNull();
		assertThat(smartJoin.getId()).isNotNull();
		
		List<Long> excludeIds = List.of(smartJoin.getId(), 25L);
		List<SmartJoin> listExclude = repository.findAllExclude(excludeIds);
		assertThat(listExclude.size() == 0).isTrue();
	}
	
	@Test
	@DisplayName("Find smart-join name by id")
	@Order(5)
	public void findNameById() {
		assertThat(smartJoin).isNotNull();
		assertThat(smartJoin.getId()).isNotNull();
		
		String found = repository.findNameById(smartJoin.getId());
		assertThat(StringUtils.hasLength(found)).isTrue();
		assertThat(found).isEqualTo(smartJoin.getName());
	}
	
	@Test
    @DisplayName("Delete smart-join by id.")
    @Order(6)
    @Commit
	public void deleteSmartJoinById() {
    	Assertions.assertThat(smartJoin).isNotNull();	 
    	repository.deleteById(smartJoin.getId());
	    Optional<SmartJoin> notfound = repository.findById(smartJoin.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
}

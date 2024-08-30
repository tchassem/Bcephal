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
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.sourcing.grid.CreateFactory;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinRepositoryIntegrationTests {

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
	private JoinRepository repository;
	
	@Autowired
	private TestEntityManager entityManager;
	
	private static Join join;
	
	private static BGroup group;
	
	@BeforeAll
	static void init() {
		join = CreateFactory.buildJoin();
		assertThat(join).isNotNull();
		assertThat(join.getId()).isNull();
		
		assertThat(group).isNull();
	}
	
	@Test
	@DisplayName("Save a new join")
	@Order(1)
	@Commit
	public void saveJoinTest() {
		assertThat(join).isNotNull();
		assertThat(group).isNull();
		group = entityManager.persist(new BGroup("New bgroup .."));
		assertThat(group.getId()).isNotNull();
		join.setGroup(group);
		repository.save(join);
		
		assertThat(join.getId()).isNotNull();
		Optional<Join> found = repository.findById(join.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(join.getName());
	}
	
	@Test
	@DisplayName("Update an existing join")
	@Order(2)
	@Commit
	public void updateJoinTest() {
		assertThat(join).isNotNull();
		assertThat(join.getId()).isNotNull();
		join.setName("New join name ...");
		join.setScheduled(true);
		repository.save(join);
		
		assertThat(join.getId()).isNotNull();
		Optional<Join> found = repository.findById(join.getId());
		assertThat(found.isPresent()).isTrue();
		assertThat(found.get().getName()).isEqualTo(join.getName());
	}
	
	@Test
	@DisplayName("Find join by name")
	@Order(3)
	public void findJoinByName() {
		assertThat(join).isNotNull();
		assertThat(join.getName()).isNotNull();
		
		List<Join> listResult = repository.findByName(join.getName());
		assertThat(listResult.size() == 1).isTrue();
		assertThat(listResult.get(0).getName()).isEqualTo(join.getName());
	}
	
	@Test
	@DisplayName("Find by active and scheduled and cronExpression IsNotNull")
	@Order(4)
	public void findByActiveAndScheduledAndCronExpressionIsNotNull() {
		assertThat(join).isNotNull();
		assertThat(join.getId()).isNotNull();
		
		List<Join> result = repository.findByActiveAndScheduledAndCronExpressionIsNotNull(true, true);
		assertThat(result.size() == 1).isTrue();
		assertThat(result.get(0).getName()).isEqualTo(join.getName());
	}	

	@Test
	@DisplayName("Find all join as nameables")
	@Order(5)
	public void findAllAsNameables() {
		assertThat(join).isNotNull();
		
		List<Nameable> listResult = repository.findAllAsNameables();
		assertThat(listResult.size() == 1).isTrue();
		assertThat(listResult.get(0).getName()).isEqualTo(join.getName());
	}
	
	@Test
	@DisplayName("Find all join as nameables exclude ids")
	@Order(6)
	public void findAllAsNameablesExcludeIds() {
		assertThat(join).isNotNull();
		assertThat(join.getId()).isNotNull();
		
		List<Long> excludeIds = List.of(join.getId());
		List<Nameable> listExclude = repository.findAllAsNameablesExcludeIds(excludeIds);
		assertThat(listExclude.size() == 0).isTrue();
	}
	
	@Test
	@DisplayName("Find join by group")
	@Order(7)
	public void findJoinByGroup() {
		assertThat(group).isNotNull();
		assertThat(group.getId()).isNotNull();
		Join joinWithGroup = repository.findByGroup(group);
		assertThat(joinWithGroup).isNotNull();
		assertThat(joinWithGroup.getGroup().getId()).isEqualTo(group.getId());
	}
	
	@Test
    @DisplayName("Delete join by id")
    @Order(8)
    @Commit
	public void deleteJoinById() {
    	Assertions.assertThat(join).isNotNull();	 
    	repository.deleteById(join.getId());
	    Optional<Join> notfound = repository.findById(join.getId());	 
	    Assertions.assertThat(notfound.isEmpty()).isTrue();
	}
	@Test
    @DisplayName("Delete group")
    @Order(9)
    @Commit
	public void deleteGroupOfJoin() {
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

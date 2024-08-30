/**
 * 
 */
package com.moriset.bcephal.security.repository;

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
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.security.domain.FunctionalityBlockGroup;

import jakarta.validation.ConstraintViolationException;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FunctionalityBlockGroupRepositoryIntegrationTests {
	
	@SpringBootApplication(
			scanBasePackages = { 
					"com.moriset.bcephal.project", 
					"com.moriset.bcephal.security",
					"com.moriset.bcephal.multitenant.jpa", 
					"com.moriset.bcephal.config" 
					}
			)
	@EntityScan(basePackages = { "com.moriset.bcephal.FunctionalityBlockGroup.domain", "com.moriset.bcephal.security.domain" })
	@ActiveProfiles(profiles = {"int", "repo"})
	static class ApplicationTests {}
	
	
	@Autowired
    private TestEntityManager entityManager;
     
    private static FunctionalityBlockGroup functionalityBlockGroup;
    
    @BeforeAll
    static void init(){
    	functionalityBlockGroup = new FunctionalityBlockGroup();
    	functionalityBlockGroup.setProjectId(1L);
    	functionalityBlockGroup.setUsername("myusername");
    	Assertions.assertThat(functionalityBlockGroup).isNotNull();
    	Assertions.assertThat(functionalityBlockGroup.getId()).isNull();
    }
    
    @Test
    @DisplayName("Validate functionalityBlockGroup bean.")
    @Order(1)
	public void validateTest() {
    	FunctionalityBlockGroup functionalityBlockGroup = new FunctionalityBlockGroup();
    	functionalityBlockGroup.setProjectId(null);
	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
	    	entityManager.persist(functionalityBlockGroup);
	    	entityManager.flush();
	    });  	    
    	functionalityBlockGroup.setUsername(null);
	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
	    	entityManager.persist(functionalityBlockGroup);
	    	entityManager.flush();
	    });  	    
	}
    
//    @Test
//    @DisplayName("Save a new functionalityBlockGroup")
//    @Order(2)
//    @Commit
//	public void saveFunctionalityBlockGroupTest() {
//    	Assertions.assertThat(functionalityBlockGroup).isNotNull();
//    	functionalityBlockGroup.setProjectId(1L);
//    	functionalityBlockGroup.setUsername("myusername2");
//    	functionalityBlockGroup.setName("myusername2");
//    	functionalityBlockGroup = functionalityBlockGroupRepository.save(functionalityBlockGroup);
//    	FunctionalityBlock funcbloc = new FunctionalityBlock();
//    	funcbloc.setProjectId(1L);
//    	funcbloc.setUsername("myusername2");
//    	funcbloc.setCode("##################");
//       	funcbloc.setGroupId(functionalityBlockGroup);
//    	functionalityBlockRepository.save(funcbloc);
//	    Assertions.assertThat(functionalityBlockGroup.getId()).isNotNull();
//	    Optional<FunctionalityBlockGroup> found = functionalityBlockGroupRepository.findById(functionalityBlockGroup.getId());	 
//	    Assertions.assertThat(found.isPresent()).isTrue();
//	    Assertions.assertThat(found.get().getName()).isEqualTo(functionalityBlockGroup.getName());
//	}    
//    
//    @Test
//    @DisplayName("Delete functionalityBlockGroup by ID.")
//    @Order(10)
//    @Commit
//	public void deleteFunctionalityBlockGroupById() {
//    	Long id= functionalityBlockGroup.getId();
//    	Assertions.assertThat(functionalityBlockGroup).isNotNull();	 
//    	functionalityBlockGroupRepository.deleteById(functionalityBlockGroup.getId());	
//    	
//	    Optional<FunctionalityBlockGroup> founds = functionalityBlockGroupRepository.findById(id);
//	    Assertions.assertThat(founds.isEmpty()).isTrue();
//	}

}

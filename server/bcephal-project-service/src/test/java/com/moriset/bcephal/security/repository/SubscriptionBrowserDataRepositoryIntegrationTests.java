/**
 * 
 */
package com.moriset.bcephal.security.repository;

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

import com.moriset.bcephal.security.domain.SubscriptionBrowserData;

import jakarta.validation.ConstraintViolationException;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SubscriptionBrowserDataRepositoryIntegrationTests {
	
	@SpringBootApplication(
			scanBasePackages = { 
					"com.moriset.bcephal.SubscriptionBrowserData", 
					"com.moriset.bcephal.security",
					"com.moriset.bcephal.multitenant.jpa", 
					"com.moriset.bcephal.config" 
					}
			)
	@EntityScan(basePackages = { "com.moriset.bcephal.subscription.domain", "com.moriset.bcephal.security.domain" })
	@ActiveProfiles(profiles = {"int", "repo"})
	static class ApplicationTests {}
	
	
	@Autowired
    private TestEntityManager entityManager;
 
    @Autowired
    private SubscriptionBrowserDataRepository subscriptionBrowserDataRepository;
    
    private static SubscriptionBrowserData subscriptionBrowserData;
    
    @BeforeAll
    static void init(){
    	subscriptionBrowserData = new SubscriptionBrowserData();
    	subscriptionBrowserData.setName(String.format("Subcription" + System.currentTimeMillis()));
    	Assertions.assertThat(subscriptionBrowserData).isNotNull();
    	Assertions.assertThat(subscriptionBrowserData.getId()).isNull();
    }  
    
    @Test
    @DisplayName("Validate subscriptionBrowserData bean.")
    @Order(1)
	public void validateTest() {
	    SubscriptionBrowserData subscriptionBrowserData = new SubscriptionBrowserData();
	    subscriptionBrowserData.setName(null);
	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
	    	entityManager.persist(subscriptionBrowserData);
	    	entityManager.flush();
	    });
	    
	    subscriptionBrowserData.setName("");
	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
	    	entityManager.persist(subscriptionBrowserData);
	    	entityManager.flush();
	    });
	    	    
	    subscriptionBrowserData.setName("12");
	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
	    	entityManager.persist(subscriptionBrowserData);
	    	entityManager.flush();
	    });
	    
	    subscriptionBrowserData.setName("");
        for(int i = 1; i < 200; i++) {
        	subscriptionBrowserData.setName(subscriptionBrowserData.getName().concat("A"));
        }
        org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
	    	entityManager.persist(subscriptionBrowserData);
	    	entityManager.flush();
	    });
	    
	}
    
    @Test
    @DisplayName("Save a new subscriptionBrowserData")
    @Order(2)
    @Commit
	public void saveSubscriptionBrowserDataTest() {
    	Assertions.assertThat(subscriptionBrowserData).isNotNull();
	    subscriptionBrowserDataRepository.save(subscriptionBrowserData);
	 
	    Assertions.assertThat(subscriptionBrowserData.getId()).isNotNull();
	    Optional<SubscriptionBrowserData> found = subscriptionBrowserDataRepository.findById(subscriptionBrowserData.getId());	 
	    Assertions.assertThat(found.isPresent()).isTrue();
	    Assertions.assertThat(found.get().getName()).isEqualTo(subscriptionBrowserData.getName());
	}
    
    @Test
    @DisplayName("Update an existing subscriptionBrowserData")
    @Order(3)
    @Commit
	public void updateSubscriptionBrowserDataTest() {
    	Assertions.assertThat(subscriptionBrowserData).isNotNull();
    	Assertions.assertThat(subscriptionBrowserData.getId()).isNotNull();
    	subscriptionBrowserData.setName("New name...");
    	subscriptionBrowserDataRepository.save(subscriptionBrowserData);
	    Assertions.assertThat(subscriptionBrowserData.getId()).isNotNull();
	    Optional<SubscriptionBrowserData> found = subscriptionBrowserDataRepository.findById(subscriptionBrowserData.getId());	 
	    Assertions.assertThat(found.isPresent()).isTrue();
	    Assertions.assertThat(found.get().getName()).isEqualTo(subscriptionBrowserData.getName());
	}
         
    @Test
    @DisplayName("Find subscriptionBrowserData by default subscription")
    @Order(4)
	public void findSubscriptionBrowserDataByDefaultSubscription() {
    	Assertions.assertThat(subscriptionBrowserData).isNotNull();	 
    	List<SubscriptionBrowserData> found = subscriptionBrowserDataRepository.findByDefaultSubscription(subscriptionBrowserData.isDefaultSubscription());	 
		Assertions.assertThat(found.isEmpty()).isFalse();
		Assertions.assertThat(found.size()).isEqualTo(1);
	    Assertions.assertThat(found.get(0).isDefaultSubscription()).isEqualTo(subscriptionBrowserData.isDefaultSubscription());
	}    
    
    @Test
    @DisplayName("Delete subscriptionBrowserData by ID.")
    @Order(10)
    @Commit
	public void deleteSubscriptionBrowserDataById() {
    	Assertions.assertThat(subscriptionBrowserData).isNotNull();	 
	    subscriptionBrowserDataRepository.delete(subscriptionBrowserData);	
	    
	    Optional<SubscriptionBrowserData> found = subscriptionBrowserDataRepository.findById(subscriptionBrowserData.getId());	 
	    Assertions.assertThat(found.isEmpty()).isTrue();
	}
    
}

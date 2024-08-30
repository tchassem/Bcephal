/**
 * 
 */
package com.moriset.bcephal.security.repository;

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

import com.moriset.bcephal.project.ProjectFactory;
import com.moriset.bcephal.security.domain.Subscription;

import jakarta.validation.ConstraintViolationException;

@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SubscriptionRepositoryIntegrationTests {
	
	@SpringBootApplication(
			scanBasePackages = { 
					"com.moriset.bcephal.subscription", 
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
    private SubscriptionRepository subscriptionRepository;
    
    private static Subscription subscription;
    
    @BeforeAll
    static void init(){
    	subscription = new ProjectFactory().buildSubscription();
    	Assertions.assertThat(subscription).isNotNull();
    	Assertions.assertThat(subscription.getId()).isNull();
    }
    
    @Test
    @DisplayName("Validate subscription bean.")
    @Order(1)
	public void validateTest() {
	    Subscription subscription = new ProjectFactory().buildSubscription();
	    subscription.setName(null);
	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
	    	entityManager.persist(subscription);
	    	entityManager.flush();
	    });
	    
	    subscription.setName("");
	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
	    	entityManager.persist(subscription);
	    	entityManager.flush();
	    });
	    	    
	    subscription.setName("12");
	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
	    	entityManager.persist(subscription);
	    	entityManager.flush();
	    });
	    
	    subscription.setName("");
        for(int i = 1; i < 200; i++) {
        	subscription.setName(subscription.getName().concat("A"));
        }
        org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
	    	entityManager.persist(subscription);
	    	entityManager.flush();
	    });
	    
	}
    
    @Test
    @DisplayName("Save a new subscription")
    @Order(2)
    @Commit
	public void saveSubscriptionTest() {
    	Assertions.assertThat(subscription).isNotNull();
	    subscriptionRepository.save(subscription);
	 
	    Assertions.assertThat(subscription.getId()).isNotNull();
	    Optional<Subscription> found = subscriptionRepository.findById(subscription.getId());	 
	    Assertions.assertThat(found.isPresent()).isTrue();
	    Assertions.assertThat(found.get().getName()).isEqualTo(subscription.getName());
	}
    
    @Test
    @DisplayName("Update an existing subscription")
    @Order(3)
    @Commit
	public void updateSubscriptionTest() {
    	Assertions.assertThat(subscription).isNotNull();
    	Assertions.assertThat(subscription.getId()).isNotNull();
    	subscription.setName("New name...");
    	subscriptionRepository.save(subscription);
	    Assertions.assertThat(subscription.getId()).isNotNull();
	    Optional<Subscription> found = subscriptionRepository.findById(subscription.getId());	 
	    Assertions.assertThat(found.isPresent()).isTrue();
	    Assertions.assertThat(found.get().getName()).isEqualTo(subscription.getName());
	}
         
    @Test
    @DisplayName("Find subscription by name")
    @Order(4)
	public void findSubscriptionByName() {
    	Assertions.assertThat(subscription).isNotNull();	 
    	Subscription found = subscriptionRepository.findByName(subscription.getName());	 
	    Assertions.assertThat(found).isNotNull();
	    Assertions.assertThat(found.getName()).isEqualTo(subscription.getName());
	}
    
    
    @Test
    @DisplayName("Delete subscription by ID.")
    @Order(10)
    @Commit
	public void deleteSubscriptionById() {
    	Assertions.assertThat(subscription).isNotNull();	 
	    subscriptionRepository.deleteById(subscription.getId());	
	    
	    Optional<Subscription> found = subscriptionRepository.findById(subscription.getId());	 
	    Assertions.assertThat(found.isEmpty()).isTrue();
	}

}

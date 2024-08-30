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
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.security.SecurityFactory;
import com.moriset.bcephal.security.domain.Client;
import com.moriset.bcephal.security.domain.ClientNature;



@DataJpaTest
@ActiveProfiles(profiles = {"int", "repo"})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientRepositoryIntegrationTests {
	
	@SpringBootApplication(scanBasePackages = {"com.moriset.bcephal.client","com.moriset.bcephal.security.domain","com.moriset.bcephal.multitenant.jpa",
	"com.moriset.bcephal.config" }, exclude = { DataSourceAutoConfiguration.class,
			HibernateJpaAutoConfiguration.class })
	@EntityScan(basePackages = { "com.moriset.bcephal.security", "com.moriset.bcephal.security.domain" })
	@ActiveProfiles(profiles = {"int", "repo"})
	static class ApplicationTests {}
	 
    @Autowired
    ClientRepository clientRepository;
    
    private static Client client;
    
    
    @BeforeAll
    static void init(){
    	client = new SecurityFactory().buildClient();
    	Assertions.assertThat(client).isNotNull();
    	Assertions.assertThat(client.getId()).isNull();
    }
    
    @Test
    @DisplayName("Validate client bean.")
    @Order(1)
	public void validateTest() {
//    	Client client = new SecurityFactory().buildClient();
//    	
//    	client.setNature(null);
//	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
//	    	entityManager.persist(client);
//	    	entityManager.flush();
//	    });
//	    
//	    client.setStatus(null);
//	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
//	    	entityManager.persist(client);
//	    	entityManager.flush();
//	    });
//	    	    
//	    client.setType(null);
//	    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
//	    	entityManager.persist(client);
//	    	entityManager.flush();
//	    });
//	
//	    client.setName(null);
//        org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
//	    	entityManager.persist(client);
//	    	entityManager.flush();
//	    });
//        
//        client.setName("");
//        for(int i = 1; i < 105; i++) {
//        	client.setName(client.getName().concat("A"));
//        }
//        org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
//	    	entityManager.persist(client);
//	    	entityManager.flush();
//	    });
//        
//        client.setNature(ClientNature.COMPANY);
//        client.setType(ClientType.BUSINESS);
//        client.setStatus(ClientStatus.ACTIVE);
//        
//        client.setName("");
//        for(int i = 1; i <= 100; i++) {
//        	client.setName(client.getName().concat("A"));
//        }
//        entityManager.persist(client);
//    	entityManager.flush();
//    	Assertions.assertThat(client).isNotNull();
//    	Assertions.assertThat(client.getId()).isNotNull();
//    	
//    	client.setName("");
//        for(int i = 1; i <= 5; i++) {
//        	client.setName(client.getName().concat("A"));
//        }
//        entityManager.persist(client);
//    	entityManager.flush();
//    	Assertions.assertThat(client).isNotNull();
//    	Assertions.assertThat(client.getId()).isNotNull();
//    	
	}
	
    @Test
    @DisplayName("Save a new client.")
    @Order(2)
    @Commit
	public void saveClientTest() {
    	Assertions.assertThat(client).isNotNull();
    	clientRepository.save(client);
	 
	    Assertions.assertThat(client.getId()).isNotNull();
	    Optional<Client> found = clientRepository.findById(client.getId());	 
	    Assertions.assertThat(found.isPresent()).isTrue();
	    Assertions.assertThat(found.get().getNature()).isEqualTo(client.getNature());
	    Assertions.assertThat(found.get().getType()).isEqualTo(client.getType());
	    Assertions.assertThat(found.get().getStatus()).isEqualTo(client.getStatus());
	}
    
    @Test
    @DisplayName("Update an existing client.")
    @Order(3)
    @Commit
	public void updateProjectTest() {
    	Assertions.assertThat(client).isNotNull();
    	Assertions.assertThat(client.getId()).isNotNull();
    	client.setNature(ClientNature.PERSONAL);
    	clientRepository.save(client);
	    Assertions.assertThat(client.getId()).isNotNull();
	    Optional<Client> found = clientRepository.findById(client.getId());	 
	    Assertions.assertThat(found.isPresent()).isTrue();
	    Assertions.assertThat(found.get().getNature()).isEqualTo(client.getNature());
	}
        
    @Test
    @DisplayName("Find client by code.")
    @Order(4)
	public void findByCode() {
    	Assertions.assertThat(client).isNotNull();	 
	    Optional<Client> found = clientRepository.findByCode(client.getCode());	 
	    Assertions.assertThat(found.isPresent()).isTrue();
	    Assertions.assertThat(found.get().getCode()).isEqualTo(client.getCode());
	}

    
    @Test
    @DisplayName("Find all clients in ascending order. ")
    @Order(5)
	public void findAllByOrderByNameAsc() {
    	List<Client> clients = clientRepository.findAllByOrderByNameAsc();	 
	    Assertions.assertThat(clients.isEmpty()).isFalse();
	}
    
    @Test
    @DisplayName("Validate find All clients by defaultClient in descending order and by name in ascending order.")
    @Order(6)
	public void findAllByOrderByDefaultClientDescNameAsc() {
    	List<Client> clients = clientRepository.findAllByOrderByDefaultClientDescNameAsc();	 
	    Assertions.assertThat(clients.isEmpty()).isFalse();
	}
    
    @Test
    @DisplayName("Validate delete client.")
    @Order(7)
    @Commit
	public void deleteClient() {
    	Assertions.assertThat(client).isNotNull();	
    	Assertions.assertThat(client.getId()).isNotNull();	
	    clientRepository.deleteById(client.getId());
	    Optional<Client> found = clientRepository.findById(client.getId());	 
	    Assertions.assertThat(found.isPresent()).isFalse();
	}
        
}

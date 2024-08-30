package com.moriset.bcephal.security.service;

import java.util.List;
import java.util.Locale;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.moriset.bcephal.security.SecurityFactory;
import com.moriset.bcephal.security.domain.Client;
import com.moriset.bcephal.security.domain.ClientNature;
import com.moriset.bcephal.security.domain.ClientStatus;
import com.moriset.bcephal.security.domain.ClientType;

@SpringBootTest
@ActiveProfiles(profiles = { "int", "serv" })
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientServiceIntegrationTests {

	@SpringBootApplication(scanBasePackages = { "com.moriset.bcephal.security",
			"com.moriset.bcephal.multitenant.jpa", }, exclude = { DataSourceAutoConfiguration.class,
					HibernateJpaAutoConfiguration.class, })
	@EntityScan(basePackages = { "com.moriset.bcephal.security.domain" })
	@ActiveProfiles(profiles = { "int", "serv" })
	static class ApplicationTests {
	}

	@Autowired
	private ClientService clientService;

	private static Client client;

	@BeforeAll
	static void init() {
		client = new SecurityFactory().buildClient();
		Assertions.assertThat(client).isNotNull();
		Assertions.assertThat(client.getId()).isNull();
	}

	@Test
	@DisplayName("Validate client bean.")
	@Order(1)
	@Commit
	public void validateTest() {
		Client client = new SecurityFactory().buildClient();
		
		/** validation name client not yet implemented **/
		
		client.setNature(null);
		org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
			 clientService.save(client, Locale.FRENCH);
		});
		
		client.setNature(ClientNature.PERSONAL);
		client.setType(null);
		 org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
			 clientService.save(client, Locale.FRENCH);
		});
		
		client.setType(ClientType.PREMIUM);
		client.setStatus(null);
		org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
			 clientService.save(client, Locale.FRENCH);
		});
		
	}

	@Test
	@DisplayName("Save a new client.")
	@Order(2)
	@Commit
	public void saveClientTest() {
		Assertions.assertThat(client).isNotNull();
		client.setStatus(ClientStatus.ACTIVE);
		Keycloak keycloak = clientService.getKeycloak();
		RealmResource realmResource = keycloak.realm(clientService.getRealm());
		ClientsResource clientsResource = realmResource.clients();
		List<ClientRepresentation> clientRepresentations = clientsResource.findByClientId(client.getClientId());
		if(!clientRepresentations.isEmpty() && clientRepresentations.size() == 1) {
			ClientRepresentation clientRepresentation = clientRepresentations.getFirst();
			ClientResource clientResource = clientsResource.get(clientRepresentation.getId());
					if(clientResource != null) {
						clientResource.remove();
					}	
		}
		
		clientService.save(client, Locale.FRENCH);
		Assertions.assertThat(client.getId()).isNotNull();
		Client found = clientService.getById(client.getId());
		Assertions.assertThat(found).isNotNull();
		Assertions.assertThat(found.getNature()).isEqualTo(client.getNature());
		Assertions.assertThat(found.getType()).isEqualTo(client.getType());
		Assertions.assertThat(found.getStatus()).isEqualTo(client.getStatus());
	}

	@Test
	@DisplayName("Update an existing client.")
	@Order(3)
	@Commit
	public void updateProjectTest() {
		Assertions.assertThat(client).isNotNull();
		Assertions.assertThat(client.getId()).isNotNull();
		client.setNature(ClientNature.PERSONAL);
		clientService.save(client, Locale.FRENCH);
		Assertions.assertThat(client.getId()).isNotNull();
		Client found = clientService.getById(client.getId());
		Assertions.assertThat(found).isNotNull();
		Assertions.assertThat(found.getNature()).isEqualTo(client.getNature());
	}

	@Test
	@DisplayName("Find client by name.")
	@Order(4)
	public void findByNameTest() {
		Assertions.assertThat(client).isNotNull();
		Client found = clientService.findByName(client.getName());
		Assertions.assertThat(found).isNotNull();
		Assertions.assertThat(found.getName()).isEqualTo(client.getName());
	}

	@Test
	@DisplayName("Delete client.")
	@Order(10)
	@Commit
	public void deleteClientTest() {
		Assertions.assertThat(client).isNotNull();
		clientService.deleteById(client.getId());
		Client found = clientService.getById(client.getId());
		Assertions.assertThat(found).isNull();
	}

}

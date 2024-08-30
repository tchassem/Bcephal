package com.moriset.bcephal.security;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.security.domain.Address;
import com.moriset.bcephal.security.domain.Client;
import com.moriset.bcephal.security.domain.ClientNature;
import com.moriset.bcephal.security.domain.ClientStatus;
import com.moriset.bcephal.security.domain.ClientType;

public class ClientFactory {

	public static Client BuildClient(Client client) {
		
		Client client_ = new Client();
		client_.setAddress(client.getAddress());
		client_.setClientId(client.getClientId());
		client_.setCode(client.getCode());
		client_.setDefaultClient(client.isDefaultClient());
		client_.setDefaultLanguage(client.getDefaultLanguage());
		client_.setDescription(client.getDescription());
		client_.setMaxUser(client.getMaxUser());
		client_.setName(client.getName());
		client_.setModificationDate(client.getModificationDate());
		client_.setNature(client.getNature());
		client_.setCreationDate(client.getCreationDate());
		client_.setStatus(client.getStatus());
		client_.setType(client.getType());
		client_.setSecret(client.getSecret());
		
		return client_;
	}
	
	public static List<Client> BuildClients() {
		
		List<Client> clients = new ArrayList<>();
		Client client1 = new Client();
		client1.setName("Default client11784");
		client1.setAddress(new Address());
		client1.setClientId("");
		client1.setCode("Default client11784");
	    client1.setDefaultClient(true);
	    client1.setDefaultLanguage(null);
	    client1.setMaxUser(200);
	    client1.setNature(ClientNature.COMPANY);
	    client1.setType(ClientType.SILVER);
	    client1.setStatus(ClientStatus.ACTIVE);    
	    
		clients.add(BuildClient(client1));
		
		Client client2 = new Client();
		client2.setName("Client 112");
		Address address= new Address();
		address.setEmail("gfbn1");
		address.setCountry("h");
		address.setStreet("hh");
		client2.setAddress(address);
		client2.setClientId("");
		client2.setCode("Client 112");
		client2.setDefaultClient(false);
		client2.setDefaultLanguage(null);
		client2.setMaxUser(0);
		client2.setNature(ClientNature.COMPANY);
		client2.setType(ClientType.SILVER);
		client2.setStatus(ClientStatus.ACTIVE);
	    
	    
		clients.add(BuildClient(client2));
		
		
		Client client3 = new Client();
		client2.setName("Demo du client12");
		client3.setClientId("");
		client3.setCode("Demo du client12");
		client3.setDefaultClient(false);
		client3.setDefaultLanguage(null);
		client3.setMaxUser(0);
		client3.setNature(ClientNature.COMPANY);
		client3.setType(ClientType.SILVER);
		client3.setStatus(ClientStatus.ACTIVE);
	    
	    
		clients.add(BuildClient(client3));
		
		Client client4 = new Client();
		client4.setName("Client 212");
		client4.setClientId("");
		client4.setCode("Client 212");
		client4.setDefaultClient(false);
		client4.setDefaultLanguage(null);
		client4.setMaxUser(0);
		client4.setDefaultLanguage("Fr");
		client4.setNature(ClientNature.COMPANY);
		client4.setType(ClientType.SILVER);
		client4.setStatus(ClientStatus.ACTIVE);
	    
	    
		clients.add(BuildClient(client4));
		
		Client client5 = new Client();
		client5.setName("Client test22");
		client5.setClientId("");
		client5.setCode("Client test22");
		client5.setDefaultClient(false);
		client5.setDefaultLanguage(null);
		client5.setMaxUser(0);
		client5.setNature(ClientNature.COMPANY);
		client5.setType(ClientType.SILVER);
		client5.setStatus(ClientStatus.ACTIVE);
	    
	 
		
		
		Client client7 = new Client();
		client7.setName("Client 323");
		client7.setClientId("");
		client7.setCode("Client 323");
		client7.setDefaultClient(false);
		client7.setDefaultLanguage(null);
		client7.setMaxUser(0);
		client7.setNature(ClientNature.COMPANY);
		client7.setType(ClientType.SILVER);
		client7.setStatus(ClientStatus.ACTIVE);
	    
	    
		clients.add(BuildClient(client7));
		
		
		Client client8 = new Client();
		client8.setName("Client Joel Test33");
		client8.setClientId("");
		client8.setCode("Client Joel Test33");
		client8.setDefaultClient(false);
		client8.setDefaultLanguage(null);
		client8.setMaxUser(10);
		client8.setDefaultLanguage("Fr");
		client8.setNature(ClientNature.COMPANY);
		client8.setType(ClientType.SILVER);
		client8.setStatus(ClientStatus.ACTIVE);
	    client8.setDescription("Mon client pour les tests bcephal");
	    
		clients.add(BuildClient(client8));
		
		
		Client client9 = new Client();
		client9.setName("Client 433");
		client9.setClientId("");
		client9.setCode("Client 433");
		client9.setDefaultClient(false);
		client9.setDefaultLanguage(null);
		client9.setMaxUser(3);
		client9.setNature(ClientNature.COMPANY);
		client9.setType(ClientType.SILVER);
		client9.setStatus(ClientStatus.ACTIVE);
	    
		clients.add(BuildClient(client9));
		
		
		Client client10 = new Client();
		client10.setName("TestLogin33");
		client10.setClientId("");
		client10.setCode("TestLogin33");
		client10.setDefaultClient(false);
		client10.setDefaultLanguage(null);
		client10.setMaxUser(0);
		client10.setDefaultLanguage("Fr");
		client10.setNature(ClientNature.COMPANY);
		client10.setType(ClientType.SILVER);
		client10.setStatus(ClientStatus.ACTIVE);
		client10.setDescription("hjj");
	    
		clients.add(BuildClient(client10));
		
		return clients;
	}
	
}

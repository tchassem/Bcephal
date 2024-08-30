/**
 * 
 */
package com.moriset.bcephal.security.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.security.domain.Client;
import com.moriset.bcephal.security.domain.ClientBase;
import com.moriset.bcephal.security.domain.ClientEditorData;
import com.moriset.bcephal.security.domain.ClientFunctionality;
import com.moriset.bcephal.security.domain.ClientNature;
import com.moriset.bcephal.security.domain.ClientSecurityBrowserData;
import com.moriset.bcephal.security.domain.ClientStatus;
import com.moriset.bcephal.security.domain.ClientType;
import com.moriset.bcephal.security.domain.Profile;
import com.moriset.bcephal.security.domain.ProfileType;
import com.moriset.bcephal.security.domain.ProfileUser;
import com.moriset.bcephal.security.domain.Right;
import com.moriset.bcephal.security.domain.User;
import com.moriset.bcephal.security.repository.ClientRepository;
import com.moriset.bcephal.security.repository.ProfileRepository;
import com.moriset.bcephal.security.repository.ProfileUserRepository;
import com.moriset.bcephal.security.repository.RightRepository;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
public class ClientService extends MainObjectSecurityService<Client, ClientSecurityBrowserData> {
	
	@Autowired
	ClientRepository repository;
		
	@Autowired
	ProfileService profileService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	ClientFunctionalityService clientFunctionalityService;
	
	@Autowired
	RightRepository rightRepository;
	
	@Autowired
	ProfileRepository profileRepository;
	
	@Autowired
	ProfileUserRepository profileUserRepository;
	
	@Override
	public ClientRepository getRepository() {
		return repository;
	}
	

	public PrivilegeObserver getPrivilegeObserver(Long clientId, Long profileId, String username, String projectCode, HttpSession session) {
		User user = userService.findByName(username, clientId);
		if(user != null) {
			PrivilegeObserver observer = new PrivilegeObserver();
			observer.setUser(user);
			if(profileId != null) {
				Profile profile = profileService.getById(profileId, clientId, null);		
				if(profile != null) {
					observer.setProfile(profile);
					observer.setClientFunctionalities(clientFunctionalityService.findCodesByClientId(clientId, true));
					if(!StringUtils.hasText(projectCode)) {
						observer.setRights(rightRepository.findByProfileIdAndObjectIdIsNull(profileId));
					}else {
						observer.setRights(rightRepository.findByProfileIdAndObjectIdIsNullOrFunctionalityLike(profileId,projectCode,"Form_%"));
						observer.getRights().addAll(rightRepository.findByProfileIdAndObjectIdIsNullOrFunctionalityLike(profileId,projectCode,"USER_LOAD_%"));
					}					
					if(StringUtils.hasText(projectCode)) {
						 List<Right> rights = rightRepository.findByProfileIdAndProjectCodeAndFunctionalityLike(profileId,projectCode,"Form_%");
						 rights.addAll(rightRepository.findByProfileIdAndProjectCodeAndFunctionalityLike(profileId,projectCode,"USER_LOAD_%"));
						 if(rights != null && rights.size() > 0) {
							 for (Right right : rights) {
								 if(!observer.getClientFunctionalities().contains(right.getFunctionality())) {
									 observer.getClientFunctionalities().add(right.getFunctionality());
								 }
							 }							 
						 }
						 if(observer.isAdministrator()) {
							 profileService.buildRemoteFunctionalityTostring(observer.getClientFunctionalities(), session, clientId, projectCode, Locale.ENGLISH);
						 }
					}
				}		
			}
			return observer;
		}	
		else {
			throw new BcephalException("User not found : " + username);
		}
	}
	
//	private void addFunctionality(List<ClientFunctionality> items, Right right, Long clientId) {
//		
//		List<ClientFunctionality> f0 = items.stream()
//				.filter(item-> StringUtils.hasText(item.getCode()) 
//						&& item.getCode().equalsIgnoreCase(right.getFunctionality()))
//				.collect(Collectors.toList());
//		ClientFunctionality f = null;
//		if(f0 != null && f0.size() > 0) {
//			f = f0.get(0);
//		}else {
//			f = new ClientFunctionality();
//		}
//		f.setCode(right.getFunctionality());
//		f.setName(right.getFunctionality());
//		f.setClientId(clientId);
//		f.setActive(true);
//		if(f.getActions() == null) {
//			f.setActions(new ArrayList<>());
//		}
//		f.setPosition(items.size());
//		f.getActions().add(right.getLevel());
//		f.setLowLevelActions(Arrays.asList(RightLevel.values()));
//		items.add(f);
//	}
	
	@Override
	public Client getById(Long id, Long clientId,String projectCode) {
		Client client = super.getById(id, clientId, projectCode);
		if(client!= null) {
			List<Profile> profiles = profileService.findByClientId(client.getId());
			profiles.forEach(profile -> { client.getProfileListChangeHandler().getOriginalList().add(new Nameable(profile.getId(), profile.getName()));});
			List<ClientFunctionality> functionalities = clientFunctionalityService.findByClientId(client.getId());
			client.getFunctionalityListChangeHandler().setOriginalList(functionalities);
		}	
		return super.getById(id, clientId, projectCode);
	}
	
	@Override
	public Client getById(Long id) {
		return super.getById(id, null, null);
	}
	
	@Override
	public ClientEditorData getEditorData(EditorDataFilter filter, Long clientId,String projectCode, String sessionId, HttpSession session, Locale locale) throws Exception {
		ClientEditorData data = new ClientEditorData();
		if (filter.isNewData()) {
			data.setItem(getNewItem());
			new ClientFunctionalitiesBuilder().build(data.getItem());
		} else {
			data.setItem(getById(filter.getId(), clientId, projectCode));
			new ClientFunctionalitiesBuilder().buildDeleteIfEmptyCode(data.getItem());
			new ClientFunctionalitiesBuilder().buildUpdate(data.getItem());
		}
		data.getFunctionalities().addAll(FunctionalityCodes.GetAll());
		return data;
	}

	public Client findByName(String name) {
		log.trace("Try to retrieve all clients by name : {}", name);
		List<Client> objects = repository.findByName(name);
		return objects.size() > 0 ? objects.get(0) : null;
	}
		
	@Override
	protected Client getNewItem() {
		Client client = new Client();
		client.setNature(ClientNature.COMPANY);
		client.setStatus(ClientStatus.ACTIVE);
		client.setType(ClientType.SILVER);
		String baseName = "Client ";
		int i = 1;
		client.setName(baseName + i);
		while (findByName(client.getName()) != null) {
			i++;
			client.setName(baseName + i);
		}		
		return client;
	}
	
	public List<Nameable> getUserClients(String username) {
		log.debug("Try to read clients for user : {}", username);
		List<Nameable> clients = new ArrayList<>();
		User user = userService.findByName(username);
		if(user == null) {
			log.debug("User not found : {}", username);
			user = userService.tryToBuildAdminUser(username);
			if(user == null) {
				throw new BcephalException("User not found!");
			}
		}	
		if(user.isAdministrator()) {
			log.trace("User is an administrator : {}", username);
			List<Client> allClients = repository.findAllByOrderByDefaultClientDescNameAsc();
			allClients.forEach(client -> {clients.add(new Nameable(client.getId(), client.getName()));});			
		}
		else if(user.getClientId() != null) {
			Optional<Client> allClients = repository.findById(user.getClientId());
			if(allClients.isPresent()) {
				clients.add(new Nameable(allClients.get().getId(), allClients.get().getName()));
			}
			else {
				log.debug("User client not found! User : {} Client Id : {}", username, user.getClientId());
			}
		}		
		else {
			log.debug("User is not associate to a client : {}", username);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "User is not associate to a client!");
		}
		return clients;
	}

	public List<Nameable> getUserProfiles(Long clientId, String username) {
		List<Nameable> profiles = new ArrayList<>();
		User user = userService.findByName(username);
		if(user == null) {
			log.debug("User not found : {}", username);
			throw new BcephalException("User not found!");
		}
		Optional<Client> client = repository.findById(clientId);
		if(client.isEmpty()) {
			log.debug("Client not found : {}", clientId);
			throw new BcephalException("Client not found!");
		}
		if(user.isAdministratorOrSuperUser()) {
			List<Profile> allprofiles = profileService.findByClientId(client.get().getId());
			allprofiles.forEach(profile -> { profiles.add(new Nameable(profile.getId(), profile.getName())); });			
		}
		else if(user.getClientId() != null && user.getClientId() == client.get().getId()) {
			return profileService.findByClientIdAndUserId(client.get().getId(), user.getId());
		}		
		else if(user.getClientId() == null) {
			log.debug("User is not associate to a client : {}", username);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "User is not associate to a client!");
		}
		else {
			log.debug("User is not belong to the given client!");
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "User is not belong to the given client!");
		}
		return profiles;
	}
	
	@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER, propagation = Propagation.REQUIRES_NEW)
	@Override
	public Client save(Client client, Locale locale) {
		log.debug("Try to  Save client : {}", client);
		
		try {
			if (client == null) {
				String message = messageSource.getMessage("unable.to.save.null.object", new Object[] { client },
						locale);
				throw new BcephalException(message);
			}
			long count = getRepository().count();
			validateBeforeSave(client,locale);
			ListChangeHandler<ClientFunctionality> functionalities = client.getFunctionalityListChangeHandler();
			ListChangeHandler<Nameable> profiles = client.getProfileListChangeHandler();
			saveClientRepresentation(client,locale);
			if(!StringUtils.hasText(client.getClientId())){
				client.setClientId("Client_" + System.currentTimeMillis());
			}
			setCreationAndModificationDates(client);
			client = getRepository().save(client);	
			Long id = client.getId();
			String code = client.getCode();
			
			if(count == 0) {
				List<Profile> profils = profileRepository.findByType(ProfileType.ADMINISTRATOR);
			    if(profils != null && profils.size() > 0) {
			    	profils.get(0).setClientId(id);
			    	profileRepository.save(profils.get(0));
			    	List<ProfileUser> profileUser = profileUserRepository.findByProfileId(profils.get(0).getId());
			    	if(profileUser != null && profileUser.size() > 0) {
			    		profileUser.get(0).setClientId(id);
			    		profileUserRepository.save(profileUser.get(0));
			    	}
			    }
			}
			
						
			functionalities.getItems().forEach( item -> {
				log.trace("Try to save ClientFunctionality : {}", item);
				item.setClientId(id);
				item.setClient(code);
				item = clientFunctionalityService.save(item, locale);
				log.trace("ClientFunctionality saved : {}", item.getId());
			});
			
			profiles.getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete profile : {}", item);
					profileService.deleteById(item.getId());
					log.trace("Profile deleted : {}", item.getId());
				}
			});
			
			log.debug("Client successfully saved : {} ", client);
			return client;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save client : {}", client, e);
			String message = messageSource.getMessage("unable.to.save.client", new Object[] { client }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	public void delete(ClientBase client) {
		log.debug("Try to  delete client : {}", client.getName());
		if (client == null || client.getId() == null) {
			return;
		}		
		log.debug("Try to read client from sso : {}", client.getCode());
		Keycloak keycloak = getKeycloak();
		RealmResource realmResource = keycloak.realm(realm);
		ClientsResource clientsResource = realmResource.clients();
		ClientResource ClientResource = clientsResource.get(client.getCode());
		if(ClientResource != null) {
			log.debug("Try to delete sso client : {}", client.getCode());
			ClientResource.remove();
			log.debug("Sso client deleted : {}", client.getCode());
		}	
		clientFunctionalityService.deleteByClientId(client.getId());
		userService.deleteByClientId(client.getId());	
		profileService.deleteByClientId(client.getId());
		getRepository().deleteById(client.getId());
		log.debug("Client successfully deleted : {} ", client);
	}
	
	protected ClientRepresentation saveClientRepresentation(Client client, Locale locale) {
		log.debug("Try to save or update sso client : {}", client.getName());
		Keycloak keycloak = getKeycloak();
		RealmResource realmResource = keycloak.realm(realm);
		ClientsResource clientsResource = realmResource.clients();
		ClientRepresentation representation = null;
		
		log.debug("Client is persistent ? : {}", client.isPersistent());
		if(client.isPersistent()) {	
			log.debug("Try to read client from sso : {}", client.getCode());
			ClientResource ClientResource = clientsResource.get(client.getCode());
			representation = ClientResource.toRepresentation();			
			if(representation != null) {
				log.debug("Sso client found : {}", representation.getClientId());
				representation = client.updateClientRepresentation(representation);
				log.debug("Try to update sso client : {}", representation.getClientId());
				ClientResource.update(representation);
				log.debug("Sso client updated : {}", representation.getClientId());
			}	
			else {
				log.debug("Sso client not found!");
			}
		}		
		if(representation == null) {
			representation = client.getClientRepresentation();
			representation.setWebOrigins(Arrays.asList(origin + "*"));
			representation.setRedirectUris(Arrays.asList(origin + "*"));
			log.debug("Try to create sso client : {}", representation.getClientId());
			Response response = clientsResource.create(representation);
			log.trace("Response : {}", response.getStatus());
			if (response.getStatus() == 201) {
				String representationId = CreatedResponseUtil.getCreatedId(response);
				log.debug("Sso client created : {}", representationId);
				client.setCode(representationId);	
				client.setClientId(representation.getClientId());
				saveClientRoles(clientsResource, representationId);
			}
			else {
				log.debug("Unable to create sso client. Status code : {}", response.getStatus());
				String message = String.format("Unable to create sso client. Status code : {}: {} | Status Info: {}", response.getStatus(), response.getStatusInfo());	
				log.debug(message);
				if(response.getStatusInfo().equals(Response.Status.CONFLICT)) {
					message = messageSource.getMessage("unable.to.create.sso.client", new Object[] { client.getClientId()}, locale);
				}
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
		}	
		return representation;
		
	}
	
	
	private void saveClientRoles(ClientsResource clientsResource, String clientId) {
		ClientResource clientResource = clientsResource.get(clientId);
		RolesResource rolesResource = clientResource.roles();
		List<RoleRepresentation> roles = new RoleBuilder().buildRoleRepresentations();
		roles.forEach(role -> {rolesResource.create(role);});		
	}

	public List<RoleRepresentation> getProfils(String representationId) {
		Keycloak keycloak = getKeycloak();
		RealmResource realmResource = keycloak.realm(realm);
		ClientsResource clientsResource = realmResource.clients();
		ClientResource response = clientsResource.get(representationId);
		if (response != null) {
			return response.roles().list();
		}
		return new ArrayList<>();
	}
	
	@Override
	protected ClientSecurityBrowserData getNewBrowserData(Client item) {
		return new ClientSecurityBrowserData(item);
	}

	@Override
	protected Specification<Client> getBrowserDatasSpecification(BrowserDataFilter filter, Long clientId, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<Client> qBuilder = new RequestQueryBuilder<Client>(root, query, cb);
			qBuilder.select(ClientSecurityBrowserData.class, root.get("id"), root.get("name"), root.get("creationDate"), root.get("modificationDate"));			
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}	
			if(filter.getColumnFilters() != null) {
				build(filter.getColumnFilters());
		    	filter.getColumnFilters().getItems().forEach(filte ->{
		    		build(filte);
		    	});
				qBuilder.addFilter(filter.getColumnFilters());
			}
			return qBuilder.build();
		};
	}

	protected void build(ColumnFilter columnFilter) {
		if ("clientId".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("clientId");
			columnFilter.setType(String.class);
		} else if ("code".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("code");
			columnFilter.setType(String.class);
		} else if ("description".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("description");
			columnFilter.setType(String.class);
		}else
		if ("secret".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("secret");
			columnFilter.setType(String.class);
		} else if ("defaultLanguage".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("defaultLanguage");
			columnFilter.setType(String.class);
		} else if ("defaultClient".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("defaultClient");
			columnFilter.setType(Boolean.class);
		}else
			if ("maxUser".equalsIgnoreCase(columnFilter.getName())) {
				columnFilter.setName("maxUser");
				columnFilter.setType(Integer.class);
			} else if ("nature".equalsIgnoreCase(columnFilter.getName())) {
				columnFilter.setName("nature");
				columnFilter.setType(ClientNature.class);
			} else if ("type".equalsIgnoreCase(columnFilter.getName())) {
				columnFilter.setName("type");
				columnFilter.setType(ClientType.class);
			} else if ("status".equalsIgnoreCase(columnFilter.getName())) {
				columnFilter.setName("status");
				columnFilter.setType(ClientStatus.class);
			}
		super.build(columnFilter);
	}


	public SubPrivilegeObserver getSubPrivilegeObserver(String functionalityCode, Long objectId, Long clientId, Long profileId, String username, String projectCode) {
		User user = userService.findByName(username, clientId);
		if(user != null) {
			List<String> clientFunctionalities = clientFunctionalityService.findCodesByClientId(clientId, true);
			if(clientFunctionalities != null && clientFunctionalities.size() > 0) {
				SubPrivilegeObserver observer = new SubPrivilegeObserver();
				observer.setUser(user);
				observer.setFunctionalityCode(functionalityCode);
					if(profileId != null) {
						Profile profile = profileService.getById(profileId, clientId,projectCode);		
						if(profile != null) {
							observer.setProfile(profile);	
							observer.setClientFunctionalities(clientFunctionalityService.findCodesByClientId(clientId, true));	
							observer.setRights(rightRepository.findByProfileIdAndFunctionalityAndObjectIdAndProjectCode(profileId, functionalityCode, objectId, projectCode));
						}		
					}
				return observer;
			}else {
				throw new BcephalException("Functionality not found : " + functionalityCode);
			}
		}	
		else {
			throw new BcephalException("User not found : " + username);
		}
	}


	
	public List<String> getShortcutByProfile(Long profileId, String name) {
		Optional<Profile> item = profileRepository.findById(profileId);
		if(item.isPresent()) {
			return item.get().getShortcuts();
		}
		return  new ArrayList<>();
	}
	
}

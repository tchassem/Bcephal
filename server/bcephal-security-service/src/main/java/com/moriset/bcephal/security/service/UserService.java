/**
 * 
 */
package com.moriset.bcephal.security.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;


import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.security.domain.Profile;
import com.moriset.bcephal.security.domain.ProfileType;
import com.moriset.bcephal.security.domain.ProfileUser;
import com.moriset.bcephal.security.domain.User;
import com.moriset.bcephal.security.domain.UserBrowserData;
import com.moriset.bcephal.security.domain.UserEditorData;
import com.moriset.bcephal.security.repository.ProfileRepository;
import com.moriset.bcephal.security.repository.ProfileUserRepository;
import com.moriset.bcephal.security.repository.UserRepository;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;

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
public class UserService extends MainObjectSecurityService<User, UserBrowserData>  {
	
	@Autowired
	UserRepository repository;
	
	@Autowired
	ProfileUserRepository profileUserRepository;
	
	
	@Autowired
	ProfileRepository profileRepository;
	
	@Override
	public UserRepository getRepository() {
		return repository;
	}
	
	@Override
	public User getById(Long id, Long clientId,String projectCode) {
		User user = super.getById(id, clientId, projectCode);
		if(user != null) {
			log.debug("Try to read user profiles...");
			user.getProfileListChangeHandler().setOriginalList(profileUserRepository.getProfilesByClientIdAndUserId(clientId, user.getId()));
		}	
		return user;
	}
	
	@Override
	public UserEditorData getEditorData(EditorDataFilter filter, Long clientId,String projectCode, String username, HttpSession session, Locale locale) throws Exception {
		UserEditorData data = new UserEditorData();
		if (filter.isNewData()) {
			data.setItem(getNewItem());
		} else {
			data.setItem(getById(filter.getId(), clientId,projectCode));
		}
		data.getLanguages().add(Locale.ENGLISH.getDisplayName());
		data.getLanguages().add(Locale.FRENCH.getDisplayName());
		return data;
	}
	
	public User findByName(String username) {
		log.trace("Try to retrieve user by name : {}", username);
		Optional<User> user = repository.findByUsernameIgnoreCase(username);
		return user.isPresent() ? user.get() : null;
	}
	
	public User findByName(String username, Long clientId) {
		log.trace("Try to retrieve user by name : {}", username);
		Optional<User> user = repository.findByUsernameIgnoreCaseAndClientId(username, clientId);
		if(user.isEmpty()) {
			user = repository.findByUsernameIgnoreCase(username);
		}
		return user.isPresent() ? user.get() : null;
	}

	@Override
	protected User getNewItem() {
		User user = new User();
		user.setEnabled(true);
		user.setType(ProfileType.USER);
		String baseName = "User";
		int i = 1;
		user.setName(baseName + i);
		while (findByName(user.getName()) != null) {
			i++;
			user.setName(baseName + i);
		}
		return user;
	}
	
	@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
	@Override
	public User save(User user, Long clientId, Locale locale, String projectCode) {
		log.debug("Try to  Save user : {}", user);
		
		try {
			if (user == null) {
				String message = messageSource.getMessage("unable.to.save.null.object", new Object[] { user },
						locale);
				throw new BcephalException(message);
			}
			ListChangeHandler<Nameable> userProfiles = user.getProfileListChangeHandler();
			user.buildName();
			validateBeforeSave(user, locale);
			saveUserRepresentation(user, locale);
			setCreationAndModificationDates(user);
			user.setClientId(clientId);			
			user = getRepository().save(user);
			saveUserProfiles(userProfiles, user.getId(), clientId);			
			log.debug("Client successfully saved : {} ", user);
			return user;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save client : {}", user, e);
			String message = messageSource.getMessage("unable.to.save.client", new Object[] { user }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	private void saveUserProfiles(ListChangeHandler<Nameable> profiles, Long userId, Long clientId) {
		profiles.getNewItems().forEach(item -> {
			log.trace("Try to save user profile : {}", item);
			ProfileUser prof = new ProfileUser(item.getId(), userId, clientId);
			profileUserRepository.save(prof);
			log.trace("User profile saved : {}", item.getId());
		});
		if (profiles.getDeletedItems().size() > 0) {
			profiles.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					List<ProfileUser> userClientProfiles = profileUserRepository.findByProfileIdAndUserIdAndClientId(item.getId(), userId, clientId);
					userClientProfiles.forEach(ucp -> {
						log.trace("Try to delete user profile : {}", item);
						profileUserRepository.delete(ucp);
						log.trace("User profile deleted : {}", item.getId());
					});
				}
			});
		}
	}
	
	public void delete(User user) {
		log.debug("Try to  delete user : {}", user.getName());
		if (user == null || user.getId() == null) {
			return;
		}		
		log.debug("Try to read user from sso : {}", user.getUserId());
		Keycloak keycloak = getKeycloak();
		RealmResource realmResource = keycloak.realm(realm);
		UsersResource usersResource = realmResource.users();
		if(StringUtils.hasText(user.getUserId())) {
			UserResource userResource = usersResource.get(user.getUserId());
			if(userResource != null) {
				log.debug("Try to delete sso user : {}", user.getUserId());
				userResource.remove();
				log.debug("Sso user deleted : {}", user.getUserId());
			}
		}
		profileUserRepository.deleteByUserId(user.getId());
		getRepository().deleteById(user.getId());
		log.debug("User successfully deleted : {} ", user);
	}
	
	protected UserRepresentation saveUserRepresentation(User user, Locale locale) {
		log.debug("Try to save or update sso user : {}", user.getName());
		Keycloak keycloak = getKeycloak();
		RealmResource realmResource = keycloak.realm(realm);
		UsersResource usersResource = realmResource.users();
		UserRepresentation representation = null;
		
		validateUser(user, usersResource);
		
		log.debug("User is persistent ? : {}", user.isPersistent());
		if(user.isPersistent() && StringUtils.hasText(user.getUserId())) {	
			log.debug("Try to read user from sso : {}", user.getUserId());
			UserResource userResource = usersResource.get(user.getUserId());
			representation = userResource.toRepresentation();			
			if(representation != null) {
				log.debug("Sso user found : {}", representation.getId());
				representation = user.updateUserRepresentation(representation);
				log.debug("Try to update sso client : {}", representation.getId());
				userResource.update(representation);
				log.debug("Sso user updated : {}", representation.getId());
			}	
			else {
				log.debug("Sso user not found!");
			}
		}		
		if(representation == null) {
			representation = user.getUserRepresentation();			
			log.debug("Try to create sso user : {}", representation.getId());
			Response response = usersResource.create(representation);
			log.trace("Response : {}", response.getStatus());
			if (response.getStatus() == 201) {
				
//				CredentialRepresentation passwordCred = new CredentialRepresentation();
//				passwordCred.setTemporary(false);
//				passwordCred.setType(CredentialRepresentation.PASSWORD);
//				passwordCred.setValue(user.getPassword());
				
				String representationId = CreatedResponseUtil.getCreatedId(response);
				log.debug("Sso user created : {}", representationId);
				user.setUserId(representationId);
			}
			else {
				String message = String.format("Unable to create sso user. Status code : {0} | Status Info: {1}", response.getStatus(), response.getStatusInfo());	
				log.error(message);
				if(response.getStatusInfo().equals(Response.Status.CONFLICT)) {
					message = messageSource.getMessage("unable.to.create.sso.user", new Object[] { user.getUsername(),user.getEmail() }, locale);
				}
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
		}	
		return representation;
		
	}
	

	private void validateUser(User user, UsersResource usersResource) {
		log.debug("Try to validate user data... : {}", user.getName());		
		usersResource.list().forEach(rep -> {
			if(!rep.getId().equals(user.getUserId())) {
				if(rep.getEmail() != null && rep.getEmail().equals(user.getEmail())) {
					throw new BcephalException("Another user is already associated to this email : " + rep.getEmail());
				}
			}
		});
		log.debug("User is persistent ? : {}", user.isPersistent());
	}

	@Override
	protected UserBrowserData getNewBrowserData(User user) {
		return new UserBrowserData(user);
	}

	@Override
	protected Specification<User> getBrowserDatasSpecification(BrowserDataFilter filter, Long clientId, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<User> qBuilder = new RequestQueryBuilder<User>(root, query, cb);
			qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), root.get("creationDate"), root.get("modificationDate"));
			if (clientId != null) {
				qBuilder.addEquals("clientId", clientId);
			}
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
		if ("userId".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("userId");
			columnFilter.setType(String.class);
		} else if ("username".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("username");
			columnFilter.setType(String.class);
		} else if ("enabled".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("enabled");
			columnFilter.setType(Boolean.class);
		} else if ("firstName".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("firstName");
			columnFilter.setType(String.class);
		} else if ("lastName".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("lastName");
			columnFilter.setType(String.class);
		} else if ("emailVerified".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("emailVerified");
			columnFilter.setType(Boolean.class);
		} else if ("email".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("email");
			columnFilter.setType(String.class);
		} else if ("defaultLanguage".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("defaultLanguage");
			columnFilter.setType(String.class);
		} else if ("type".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("type");
			columnFilter.setType(ProfileType.class);
		}
		super.build(columnFilter);
	}
	
	
	public List<UserRepresentation> getUsers() {
		Keycloak keycloak = getKeycloak();
		// Get realm
		RealmResource realmResource = keycloak.realm(realm);
		return realmResource.users().list();
	}

	@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER, propagation = Propagation.REQUIRES_NEW)
	public User tryToBuildAdminUser(String username) {
		log.debug("Try to build admin user : {}", username);
		try {
			long count = repository.count();
			log.debug("User : {}", count);
			if(count == 0) {
				log.debug("Building admin user : {}", username);
				UserRepresentation representation = getUserRepresentation(username);
				log.debug("Sso user : {}", representation);
				if(representation != null) {
					User user = new User();
					user.setName(username);
					user.setFirstName("B-CEPHAL");
					user.setUsername(username);
					user.setEnabled(true);
					user.setEmail(representation.getEmail());
					user.setUserId(representation.getId());
					user.setType(ProfileType.ADMINISTRATOR);
					
					setCreationAndModificationDates(user);
					user = getRepository().save(user);	
					
					Profile profile = new Profile();
					profile.setClientId(user.getClientId());
					profile.setCode("B-CEPHAL");
					profile.setType(ProfileType.ADMINISTRATOR);
					profile.setName("ADMINISTRATOR");
					setCreationAndModificationDates(profile);
					profile = profileRepository.save(profile);
					
					ProfileUser profileUser = new ProfileUser();
					
					profileUser.setClientId(user.getClientId());
					profileUser.setProfileId(profile.getId());
					profileUser.setUserId(user.getId());
					profileUser = profileUserRepository.save(profileUser);
					
					log.debug("Admin user successfully saved : {} ", user);
					return user;
				}
			}
			return null;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save admin user : {}", username, e);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "unable.to.save.admin.user");
		}	
	}
	
	private UserRepresentation getUserRepresentation(String username) {
		log.debug("Try to read sso user : {}", username);
		Keycloak keycloak = getKeycloak();
		RealmResource realmResource = keycloak.realm(realm);
		UsersResource usersResource = realmResource.users();
		List<UserRepresentation> users = usersResource.search(username, true);
		if(users != null && users.size() > 0) {
			return users.get(0);
		}
		return null;		
	}
	
	
	public void deleteByClientId(Long clientId) {
		log.trace("Try to delete users by client Id : {}", clientId);
		if(clientId != null) {
			repository.deleteByClientId(clientId);
		}
	}
	
}

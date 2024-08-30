/**
 * 
 */
package com.moriset.bcephal.security.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


import org.apache.catalina.session.StandardSessionFacade;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.security.domain.AccessRightEditorData;
import com.moriset.bcephal.security.domain.ClientFunctionality;
import com.moriset.bcephal.security.domain.Profile;
import com.moriset.bcephal.security.domain.ProfileBrowserData;
import com.moriset.bcephal.security.domain.ProfileDashboard;
import com.moriset.bcephal.security.domain.ProfileDashboardEditorData;
import com.moriset.bcephal.security.domain.ProfileEditorData;
import com.moriset.bcephal.security.domain.ProfileProject;
import com.moriset.bcephal.security.domain.ProfileType;
import com.moriset.bcephal.security.domain.ProfileUser;
import com.moriset.bcephal.security.domain.Right;
import com.moriset.bcephal.security.domain.User;
import com.moriset.bcephal.security.repository.ProfileDashboardRepository;
import com.moriset.bcephal.security.repository.ProfileProjectRepository;
import com.moriset.bcephal.security.repository.ProfileRepository;
import com.moriset.bcephal.security.repository.ProfileUserRepository;
import com.moriset.bcephal.security.repository.RightRepository;
import com.moriset.bcephal.security.repository.UserRepository;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

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
public class ProfileService extends MainObjectSecurityService<Profile, ProfileBrowserData> {
	
	@Autowired
	ProfileRepository repository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RightRepository rightRepository;
	
	@Autowired
	ProfileUserRepository profileUserRepository;
	
	@Autowired
	ProfileProjectRepository profileProjectRepository;
		
	@Autowired
	ClientFunctionalityService clientFunctionalityService;
	
	@Autowired
	ProfileDashboardRepository profileDashboardRepository;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	RestTemplate loadBalancedRestTemplate;
	
	@Override
	public ProfileRepository getRepository() {
		return repository;
	}
	
	@Override
	public Profile getById(Long id, Long clientId,String projectCode) {
		Profile prifile = super.getById(id, clientId,projectCode);
		if(prifile != null) {
			log.debug("Try to read profile rights...");
			prifile.getRightListChangeHandler().setOriginalList(rightRepository.findByProfileId(prifile.getId()));
			log.debug("Try to read profile users...");
			prifile.getUserListChangeHandler().setOriginalList(profileUserRepository.getUsersByProfileId(prifile.getId()));
		}		
		return prifile;
	}
	
	@Override
	public ProfileEditorData getEditorData(EditorDataFilter filter, Long clientId,String projectCode, String username, HttpSession session, Locale locale) throws Exception {
		ProfileEditorData data = new ProfileEditorData();
		if (filter.isNewData()) {
			data.setItem(getNewItem());
		} else {
			data.setItem(getById(filter.getId(), clientId,projectCode));			
		}
		Optional<User> response = userRepository.findByUsernameIgnoreCase(username);
		if(response.isPresent()) {
			User user = response.get();
			boolean isAdminOrSameClient = user.isAdministrator()
					|| user.getClientId() != null && user.getClientId().equals(clientId);
			if(isAdminOrSameClient) {
				if(clientId != null) {
					List<ClientFunctionality> functionalities = clientFunctionalityService.findByClientId(clientId, true);
					data.setFunctionalities(functionalities);
					List<User> users = userRepository.findByClientId(clientId);
					users.forEach(u ->{
						data.getUsers().add(new Nameable(u.getId(), u.getName()));
					}); 
					buildRemoteFunctionality(functionalities, session, clientId, projectCode, locale);
				}
				else {
					throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Client not found!");
				}					
			}
			else {
				log.debug("User '{}' do not belongs to the given client '{}'", username, clientId);
				throw new BcephalException("User do not belongs to the given client");
			}
		}
		else {
			throw new BcephalException("User not found : " + username);
		}
		return data;
	}
	
	
	public void buildRemoteFunctionality(List<ClientFunctionality> functionalities, HttpSession session, Long clientId,String projectCode, Locale locale) {
		try {					
				List<ClientFunctionality> functionalities2 = getRemoteFunctionality(session, clientId, projectCode, locale);
				if(functionalities2 != null ) {
					for(ClientFunctionality funct : functionalities2) {
						if(funct != null && !contains(functionalities,funct)) {
							functionalities.add(funct);
						}
					}
				}
		}
		catch(Exception ex) {
			log.error("", ex);
		}
		
		try {					
			List<ClientFunctionality> functionalities2 = getRemoteUserLoadFunctionality(session, clientId, projectCode, locale);
			if(functionalities2 != null ) {
				for(ClientFunctionality funct : functionalities2) {
					if(funct != null && !contains(functionalities,funct)) {
						functionalities.add(funct);
					}
				}
			}
		}
		catch(Exception ex) {
			log.error("", ex);
		}
	}
	
	
	
	
	 
	  
	private List<ClientFunctionality> getRemoteFunctionality(HttpSession session, Long clientId,String projectCode, Locale locale) {
		try {
			String path = "/form/data/code-access-rights";
			HttpHeaders requestHeaders = getHttpHeaders_in(session, locale); //getHttpHeaders(session, locale);
			log.trace("try to get access right request requestHeaders : {}", requestHeaders);
			//HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);
			//ResponseEntity<String> responseAccessRight = restTemplate.exchange(path, HttpMethod.POST, requestEntity, String.class);
			
			HttpEntity<String> requestEntity = new HttpEntity<String>(requestHeaders);
			ResponseEntity<String> responseAccessRight = loadBalancedRestTemplate.exchange("lb://form-service"+ path, HttpMethod.POST ,requestEntity, String.class);
			
			
			if (responseAccessRight.getStatusCode() != HttpStatus.OK) {
				log.error(
						"Unable to get right to project : {} . get access rights call faild with :\n- Http status : {}\n- Message : {}",
						projectCode, responseAccessRight.getStatusCode(), responseAccessRight.toString());
			}
			else {
				String result = responseAccessRight.getBody();						
				return new ObjectMapper().readValue(result, 
						new TypeReference<List<ClientFunctionality>>() { });
			}
		}
		catch(Exception ex) {
			log.error("", ex);
		}
		return new ArrayList<>();
	}
	
	private List<ClientFunctionality> getRemoteUserLoadFunctionality(HttpSession session, Long clientId,String projectCode, Locale locale) {
		try {
			String path = "/scheduler/user-load/code-access-rights";
			HttpHeaders requestHeaders = getHttpHeaders_in(session, locale);
			log.trace("try to get access right request requestHeaders : {}", requestHeaders);
			
			HttpEntity<String> requestEntity = new HttpEntity<String>(requestHeaders);
			ResponseEntity<String> responseAccessRight = loadBalancedRestTemplate.exchange("lb://scheduler-service"+ path, HttpMethod.POST ,requestEntity, String.class);
			
			
			if (responseAccessRight.getStatusCode() != HttpStatus.OK) {
				log.error(
						"Unable to get right to project : {} . get access rights call faild with :\n- Http status : {}\n- Message : {}",
						projectCode, responseAccessRight.getStatusCode(), responseAccessRight.toString());
			}
			else {
				String result = responseAccessRight.getBody();						
				return new ObjectMapper().readValue(result, 
						new TypeReference<List<ClientFunctionality>>() { });
			}
		}
		catch(Exception ex) {
			log.error("", ex);
		}
		return new ArrayList<>();
	}
	
	
	public void buildRemoteFunctionalityTostring(List<String> functionalities, HttpSession session, Long clientId,String projectCode, Locale locale) {
		try {					
				List<ClientFunctionality> functionalities2 = getRemoteFunctionality(session, clientId, projectCode, locale);
				if(functionalities2 != null ) {
					for(ClientFunctionality funct : functionalities2) {
						if(funct != null && !functionalities.contains(funct.getCode())) {
							functionalities.add(funct.getCode());
						}
					}
			}
			}catch(Exception ex) {
				
			}
		
		try {					
			List<ClientFunctionality> functionalities2 = getRemoteUserLoadFunctionality(session, clientId, projectCode, locale);
			if(functionalities2 != null ) {
				for(ClientFunctionality funct : functionalities2) {
					if(funct != null && !functionalities.contains(funct.getCode())) {
						functionalities.add(funct.getCode());
					}
				}
		}
		}catch(Exception ex) {
			
		}
	}
	
	
	private boolean contains(List<ClientFunctionality> functionalities, ClientFunctionality functionality) {
		if(functionalities == null || functionalities.size() == 0 || functionality == null) {
			return false;
		}
		for(ClientFunctionality funct : functionalities) {
			if(StringUtils.hasText(funct.getCode()) && funct.getCode().equalsIgnoreCase(functionality.getCode())) {
				return true;
			}
		}
		
		return false;
	}
	public Profile getBySubPrivilegeId(Long id, Long clientId,String functionalityCode,String projectCode) {
		Profile prifile = super.getById(id, clientId, projectCode);
		if(prifile != null) {
			log.debug("Try to read profile rights...");;
			prifile.getRightListChangeHandler().setOriginalList(rightRepository.findByProfileIdAndFunctionalityAndProjectCode(prifile.getId(),functionalityCode, projectCode));
			log.debug("Try to read profile users...");
			prifile.getUserListChangeHandler().setOriginalList(profileUserRepository.getUsersByProfileId(prifile.getId()));
		}		
		return prifile;
	}
	
	public ProfileEditorData getSubPrivilegeEditorData(EditorDataFilter filter, Long clientId, String username,String projectCode, Locale locale) throws Exception {
		ProfileEditorData data = new ProfileEditorData();
		if (filter.isNewData()) {
			data.setItem(getNewItem());
		} else {
			data.setItem(getBySubPrivilegeId(filter.getId(), clientId, filter.getSubjectType(), projectCode));			
		}
		Optional<User> response = userRepository.findByUsernameIgnoreCase(username);
		if(response.isPresent()) {
			User user = response.get();
			boolean isAdminOrSameClient = user.isAdministrator()
					|| user.getClientId() != null && user.getClientId().equals(clientId);
			if(isAdminOrSameClient) {
				if(clientId != null) {
					List<ClientFunctionality> functionalities = clientFunctionalityService.findByClientId(clientId, true);
					data.setFunctionalities(functionalities);
					List<User> users = userRepository.findByClientId(clientId);
					users.forEach(u ->{
						data.getUsers().add(new Nameable(u.getId(), u.getName()));
					}); 
				}
				else {
					throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Client not found!");
				}					
			}
			else {
				log.debug("User '{}' do not belongs to the given client '{}'", username, clientId);
				throw new BcephalException("User do not belongs to the given client");
			}
		}
		else {
			throw new BcephalException("User not found : " + username);
		}
		return data;
	}

	public Profile findByName(String name) {
		log.trace("Try to retrieve all profiles by name : {}", name);
		List<Profile> objects = repository.findByName(name);
		return objects.size() > 0 ? objects.get(0) : null;
	}
	
	@Override
	protected Profile getNewItem() {
		Profile profile = new Profile();
		profile.setType(ProfileType.USER);
		String baseName = "Profile ";
		int i = 1;
		profile.setName(baseName + i);
		while (findByName(profile.getName()) != null) {
			i++;
			profile.setName(baseName + i);
		}
		return profile;
	}
	
	public List<Profile> findByClient(String client) {
		log.trace("Try to retrieve profiles by client : {}", client);
		if(StringUtils.hasText(client)) {
			return repository.findByClient(client);
		}
		return new ArrayList<>();
	}
	
	public List<Profile> findByClientId(Long clientId) {
		log.trace("Try to retrieve profiles by client Id : {}", clientId);
		if(clientId != null) {
			return repository.findByClientIdOrderByNameAsc(clientId);
		}
		return new ArrayList<>();
	}
	
	public List<Nameable> findByClientIdAndUserId(Long clientId, Long userId) {
		log.trace("Try to retrieve profiles by client Id : {} and user Id :{}", clientId, userId);
		if(clientId != null) {
			return profileUserRepository.getProfilesByClientIdAndUserId(clientId, userId);
		}
		return new ArrayList<>();
	}
	
	
	
	
	@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER, propagation = Propagation.REQUIRES_NEW)
	@Override
	public Profile save(Profile profile, Long clientId, Locale locale, String projectCode) {
		log.debug("Try to  Save profile : {}", profile);
		
		try {
			if (profile == null) {
				String message = messageSource.getMessage("unable.to.save.null.object", new Object[] { profile },
						locale);
				throw new BcephalException(message);
			}
			ListChangeHandler<Right> rights = profile.getRightListChangeHandler();
			ListChangeHandler<Nameable> users = profile.getUserListChangeHandler();
			saveGroupRepresentation(profile);			
			setCreationAndModificationDates(profile);
			profile.setClientId(clientId);
			validateBeforeSave(profile,locale);
			profile.initShortCutImpl();
			profile = getRepository().save(profile);
			Long profileId = profile.getId();
			rights.getNewItems().forEach(right -> {
				right.setProfileId(profileId);
				right.setProjectCode(projectCode);
				rightRepository.save(right);});
			rights.getUpdatedItems().forEach(right -> {
				right.setProfileId(profileId); 
			right.setProjectCode(projectCode);
			rightRepository.save(right);});
			rights.getDeletedItems().forEach(right -> {rightRepository.deleteById(right.getId());});		
			
			users.getNewItems().forEach(user -> {
				ProfileUser profileUser = new ProfileUser();
				profileUser.setProfileId(profileId);
				profileUser.setUserId(user.getId());
				profileUser.setClientId(clientId);
				
				profileUserRepository.save(profileUser);});
			users.getUpdatedItems().forEach(user -> {
				ProfileUser profileUser = new ProfileUser();
				profileUser.setProfileId(profileId);
				profileUser.setUserId(user.getId());
				profileUser.setClientId(clientId);
				profileUserRepository.save(profileUser);});
			users.getDeletedItems().forEach(user -> {
				profileUserRepository.deleteByProfileIdAndUserId(profileId, user.getId());
				});
			
			
			log.debug("Profile successfully saved : {} ", profile);
			return profile;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save profile : {}", profile, e);
			String message = messageSource.getMessage("unable.to.save.profile", new Object[] { profile }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	@Override
	public void delete(Profile profile) {
		log.debug("Try to  delete profile : {}", profile.getName());
		if (profile == null || profile.getId() == null) {
			return;
		}		
		if(profile.getCode() != null) {
			log.debug("Try to read group from sso : {}", profile.getCode());
			Keycloak keycloak = getKeycloak();
			RealmResource realmResource = keycloak.realm(realm);
			GroupsResource groupsResource = realmResource.groups();
			GroupResource groupResource = groupsResource.group(profile.getCode());
			if(groupResource != null) {
				log.debug("Try to delete sso group : {}", profile.getCode());
				groupResource.remove();
				log.debug("Sso group deleted : {}", profile.getCode());
			}	
		}
		rightRepository.deleteByProfileId(profile.getId());
		profileUserRepository.deleteByProfileId(profile.getId());
		profileProjectRepository.deleteByProfileId(profile.getId());
		getRepository().deleteById(profile.getId());
		log.debug("Profile successfully deleted : {} ", profile);
	}
	
	protected GroupRepresentation saveGroupRepresentation(Profile profile) {
		log.debug("Try to save or update sso group : {}", profile.getName());
		Keycloak keycloak = getKeycloak();
		RealmResource realmResource = keycloak.realm(realm);
		GroupsResource groupsResource = realmResource.groups();
		GroupRepresentation representation = null;
		
		log.debug("Profile is persistent ? : {}", profile.isPersistent());
		if(profile.isPersistent() && StringUtils.hasText(profile.getCode())) {	
			log.debug("Try to read group from sso : {}", profile.getCode());
			GroupResource groupResource = groupsResource.group(profile.getCode());
			representation = groupResource.toRepresentation();			
			if(representation != null) {
				log.debug("Sso group found : {}", representation.getId());
				representation = profile.updateGroupRepresentation(representation);
				log.debug("Try to update sso group : {}", representation.getId());
				groupResource.update(representation);
				log.debug("Sso group updated : {}", representation.getId());
			}	
			else {
				log.debug("Sso group not found!");
			}
		}		
		if(representation == null) {
			representation = profile.getGroupRepresentation();
			log.debug("Try to create sso group : {}", representation.getId());
			Response response = groupsResource.add(representation);
			log.trace("Response : {}", response.getStatus());
			if (response.getStatus() == 201) {
				String representationId = CreatedResponseUtil.getCreatedId(response);
				log.debug("Sso group created : {}", representationId);
				profile.setCode(representationId);	
			}
			else {
				log.error("Unable to create sso group. Status code : {} - {}", response.getStatus(), response.getEntity());
			}
		}	
		return representation;
		
	}

	@Override
	protected ProfileBrowserData getNewBrowserData(Profile item) {
		return new ProfileBrowserData(item);
	}

	@Override
	protected Specification<Profile> getBrowserDatasSpecification(BrowserDataFilter filter, Long clientId, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<Profile> qBuilder = new RequestQueryBuilder<Profile>(root, query, cb);
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
		if ("code".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("code");
			columnFilter.setType(String.class);
		} else if ("description".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("description");
			columnFilter.setType(String.class);
		}else if ("type".equalsIgnoreCase(columnFilter.getName())) {
				columnFilter.setName("type");
				columnFilter.setType(ProfileType.class);
			}
		super.build(columnFilter);
	}
	
	protected Specification<User> getUserSpecification(EditorDataFilter filter, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<User> qBuilder = new RequestQueryBuilder<User>(root, query, cb);
			qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), root.get("creationDate"), root.get("modificationDate"));
			if (filter != null && StringUtils.hasText(filter.getSubjectType())) {
				qBuilder.addLikeCriteria("name", filter.getSubjectType());
			}
			return qBuilder.build();
		};
	}

	public void deleteByClientId(Long clientId) {
		log.trace("Try to delete profiles by client Id : {}", clientId);
		if(clientId != null) {
			repository.deleteByClientId(clientId);
		}
	}

	public AccessRightEditorData getAccessRightEditorData(Long clientId, Long project) {
		AccessRightEditorData data = new AccessRightEditorData();
		data.getItemListChangeHandler().setOriginalList(profileProjectRepository.findByProjectId(project));
		List<Profile> profiles = repository.findByClientIdOrderByNameAsc(clientId);
		profiles.forEach(profile -> {data.getProfiles().add(new Nameable(profile.getId(), profile.getName()));});
		data.getItemListChangeHandler().getOriginalList().forEach(item -> {
			profiles.forEach(profile -> {
				if(item.getProfileId() != null && item.getProfileId().equals(profile.getId())) {
					item.setName(profile.getName());
				}
			});
		});
		return data;
	}

	public void save(ListChangeHandler<ProfileProject> profiles, Long clientId, Long project, Locale locale) {
		log.debug("Try to  Save profile project : {}", profiles);
		try {			
			profiles.getNewItems().forEach(item -> {
				log.trace("Try to save Profile Project : {}", item);
				item.setProjectId(project);
				profileProjectRepository.save(item);
				log.trace("Profile Project saved : {}", item.getId());
			});
			profiles.getUpdatedItems().forEach(item -> {
				log.trace("Try to save Profile Project : {}", item);
				item.setProjectId(project);
				profileProjectRepository.save(item);
				log.trace("Profile Project saved : {}", item.getId());
			});
			profiles.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete Profile Project : {}", item);
					profileProjectRepository.deleteById(item.getId());
					log.trace("Profile Project deleted : {}", item.getId());
				}
			});

			log.debug("profile Projects saved");
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save profile projects : {}", profiles, e);
			String message = getMessageSource().getMessage("unable.to.save.profile.profiles", new Object[] { },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	
	public ProfileDashboardEditorData getProfileDashboardEditorData(Long dashboardId, Locale locale) {
		ProfileDashboardEditorData data = new ProfileDashboardEditorData();
		data.getItemListChangeHandler().setOriginalList(profileDashboardRepository.findByDashboardId(dashboardId));
		List<Profile> profiles = repository.findAll();
		profiles.forEach(profile -> {data.getProfiles().add(new Nameable(profile.getId(), profile.getName()));});
		data.getItemListChangeHandler().getOriginalList().forEach(item -> {
			profiles.forEach(profile -> {
				if(item.getProfileId() != null && item.getProfileId().equals(profile.getId())) {
					item.setName(profile.getName());
				}
			});
		});
		return data;
	}
	
	@Transactional
	public void save(ListChangeHandler<ProfileDashboard> dashboards, Long dashboardId, Locale locale) {
		log.debug("Try to  Save profile dashboards : {}", dashboards);
		try {			
			dashboards.getNewItems().forEach(item -> {
				log.trace("Try to save Profile Dashboard : {}", item);
				item.setDashboardId(dashboardId);
				profileDashboardRepository.save(item);
				log.trace("Profile Dashboard saved : {}", item.getId());
			});
			dashboards.getUpdatedItems().forEach(item -> {
				log.trace("Try to save Profile Dashboard : {}", item);
				item.setDashboardId(dashboardId);
				profileDashboardRepository.save(item);
				log.trace("Profile Dashboard saved : {}", item.getId());
			});
			dashboards.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete Profile Dashboard : {}", item);
					profileDashboardRepository.deleteById(item.getId());
					log.trace("Profile Dashboard deleted : {}", item.getId());
				}
			});

			log.debug("profileId Dashboards saved");
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save profile dashboards : {}", dashboards, e);
			String message = getMessageSource().getMessage("unable.to.save.profile.dashboards", new Object[] { },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	
	
	/**
	 * Creates a new project.
	 * 
	 * @param projectName The name of the project to create
	 * @param locale      User locale
	 * @param clientId
	 * @return Created project
	 */
//	private HttpHeaders getHttpHeaders(HttpSession session, Locale locale) {
//		HttpHeaders requestHeaders = new HttpHeaders();
//		//String cId = (HttpHeaders.COOKIE+ "__").toLowerCase();
//		String cId = HttpHeaders.COOKIE.toLowerCase();
//		String remoteAddress = "remote_address";
//		List<String> remoteAddressValue = null;
//		List<String> items = Arrays.asList("bc-profile", "bc-client", "BC-PROFILE", "BC-CLIENT", "authorization", HttpHeaders.AUTHORIZATION, cId,HttpHeaders.COOKIE);
//		if (session instanceof StandardSessionFacade) {
//			HttpHeaders requestHeaders2 = (HttpHeaders) session.getAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME);
//			if (requestHeaders2 == null) {
//				requestHeaders2 = new HttpHeaders();
//
//			} else {
//				remoteAddressValue = requestHeaders2.get(remoteAddress);
//				requestHeaders2.forEach((key, value) -> {
//					if (items.contains(key)) {
//						if ("authorization".equalsIgnoreCase(key)) {
//							requestHeaders.add(HttpHeaders.AUTHORIZATION, value.get(0));
//						} else if (cId.equalsIgnoreCase(key)) {
//							requestHeaders.add(HttpHeaders.COOKIE, value.get(0));
//						} else
//						{
//							requestHeaders.add(key, value.get(0));
//						}
//					}
//				});
//			}
//		}
//		requestHeaders.set(MultiTenantInterceptor.TENANT_HEADER_NAME, TenantContext.getCurrentTenant());
//		requestHeaders.set(RequestParams.LANGUAGE, locale.getLanguage());
//		if(remoteAddressValue != null && remoteAddressValue.size() > 0) {
//			UriComponents uriComponents;
//			try {
//				uriComponents = UriComponentsBuilder.newInstance()
//						.uri(new URI(remoteAddressValue.get(0))).build();
//				restTemplate.setUriTemplateHandler(
//						  new DefaultUriBuilderFactory(String.format("%s://%s:%s", uriComponents.getScheme(),uriComponents.getHost(),uriComponents.getPort())));
//			} catch (URISyntaxException e) {
//				e.printStackTrace();
//			}
//		}
//		return requestHeaders;
//	}
	
	
	
	private HttpHeaders getHttpHeaders_in(HttpSession session, Locale locale) {
		HttpHeaders requestHeaders = new HttpHeaders();
		String cId = HttpHeaders.COOKIE.toLowerCase();
		List<String> items = Arrays.asList("bc-profile", "bc-client", "BC-PROFILE", "BC-CLIENT", "authorization", HttpHeaders.AUTHORIZATION, cId,HttpHeaders.COOKIE);
		if (session instanceof StandardSessionFacade) {
			HttpHeaders requestHeaders2 = (HttpHeaders) session.getAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME);
			if (requestHeaders2 == null) {
				requestHeaders2 = new HttpHeaders();

			} else {
				requestHeaders2.forEach((key, value) -> {
					if (items.contains(key)) {
						if ("authorization".equalsIgnoreCase(key)) {
							requestHeaders.add(HttpHeaders.AUTHORIZATION, value.get(0));
						} else if (cId.equalsIgnoreCase(key)) {
							requestHeaders.add(HttpHeaders.COOKIE, value.get(0));
						} else
						{
							requestHeaders.add(key, value.get(0));
						}
					}
				});
			}
		}
		requestHeaders.set(MultiTenantInterceptor.TENANT_HEADER_NAME, TenantContext.getCurrentTenant());
		requestHeaders.set(RequestParams.LANGUAGE, locale.getLanguage());		
		return requestHeaders;
	}
}

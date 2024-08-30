/**
 * 
 */
package com.moriset.bcephal.security.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.security.domain.ClientFunctionality;
import com.moriset.bcephal.security.repository.ClientFunctionalityRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Service
@Slf4j
@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
public class ClientFunctionalityService extends PersistentService<ClientFunctionality, BrowserData> {

	@Autowired
	ClientFunctionalityRepository clientFunctionalityRepository;
	
	@Override
	public ClientFunctionalityRepository getRepository() {
		return clientFunctionalityRepository;
	}
	
	
	public List<ClientFunctionality> findByClient(String client) {
		log.trace("Try to retrieve ClientFunctionality by client : {}", client);
		if(StringUtils.hasText(client)) {
			return clientFunctionalityRepository.findByClient(client);
		}
		return new ArrayList<>();
	}
	
	public List<ClientFunctionality> findByClient(String client, boolean active) {
		log.trace("Try to retrieve ClientFunctionality by client : {} and active : {}", client, active);
		if(StringUtils.hasText(client)) {
			return clientFunctionalityRepository.findByClientAndActive(client, active);
		}
		return new ArrayList<>();
	}
	
	public List<ClientFunctionality> findByClientId(Long clientId) {
		log.trace("Try to retrieve ClientFunctionality by client Id : {}", clientId);
		if(clientId != null) {
			return clientFunctionalityRepository.findByClientId(clientId);
		}
		return new ArrayList<>();
	}
	
	public List<ClientFunctionality> findByClientId(Long clientId, boolean active) {
		log.trace("Try to retrieve ClientFunctionality by client Id : {} and active : {}", clientId, active);
		if(clientId != null) {
			return clientFunctionalityRepository.findByClientIdAndActive(clientId, active);
		}
		return new ArrayList<>();
	}
	
	public List<String> findCodesByClientId(Long clientId, boolean active) {
		log.trace("Try to retrieve ClientFunctionality codes by client Id : {} and active : {}", clientId, active);
		List<ClientFunctionality> functionalities = findByClientId(clientId, active);
		List<String> codes = new ArrayList<String>();
		functionalities.forEach(f->{codes.add(f.getCode());});
		return codes;
	}
	
	@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
	@Override
	public ClientFunctionality save(ClientFunctionality functionality, Locale locale) {
		log.debug("Try to  Save ClientFunctionality : {}", functionality);
		
		try {
			if (functionality == null) {
				String message = messageSource.getMessage("unable.to.save.null.object", new Object[] { functionality },
						locale);
				throw new BcephalException(message);
			}
			//functionality.buildActionAsString();			
			functionality = getRepository().save(functionality);			
			log.debug("ClientFunctionality successfully saved : {} ", functionality);
			return functionality;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save ClientFunctionality : {}", functionality, e);
			String message = messageSource.getMessage("unable.to.save.client.functionality", new Object[] { functionality }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}


	public void deleteByClientId(Long clientId) {
		log.trace("Try to delete ClientFunctionality by client Id : {}", clientId);
		if(clientId != null) {
			clientFunctionalityRepository.deleteByClientId(clientId);
		}
	}
	

}

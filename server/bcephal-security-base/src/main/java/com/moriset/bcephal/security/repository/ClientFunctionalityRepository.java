/**
 * 
 */
package com.moriset.bcephal.security.repository;

import java.util.List;

import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.security.domain.ClientFunctionality;

/**
 * @author Moriset
 *
 */
public interface ClientFunctionalityRepository extends PersistentRepository<ClientFunctionality> {

	List<ClientFunctionality> findByClient(String client);
	
	List<ClientFunctionality> findByClientId(Long clientId);

	List<ClientFunctionality> findByClientAndActive(String client, boolean active);

	List<ClientFunctionality> findByClientIdAndActive(Long clientId, boolean active);
	
	void deleteByClientId(Long clientId);
	
}

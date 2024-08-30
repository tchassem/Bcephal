/**
 * 
 */
package com.moriset.bcephal.security.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.security.domain.Client;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface ClientRepository extends MainObjectRepository<Client> {

	public Optional<Client> findByCode(String code);

	public List<Client> findAllByOrderByNameAsc();

	public List<Client> findAllByOrderByDefaultClientDescNameAsc();
	
}

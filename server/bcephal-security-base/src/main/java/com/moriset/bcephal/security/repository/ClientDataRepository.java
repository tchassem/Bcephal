/**
 * 
 */
package com.moriset.bcephal.security.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.security.domain.ClientBase;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface ClientDataRepository extends MainObjectRepository<ClientBase> {

	public Optional<ClientBase> findByCode(String code);

	public List<ClientBase> findAllByOrderByNameAsc();

	public List<ClientBase> findAllByOrderByDefaultClientDescNameAsc();
	
}

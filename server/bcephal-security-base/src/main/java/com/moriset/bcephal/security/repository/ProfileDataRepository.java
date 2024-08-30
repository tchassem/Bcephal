/**
 * 
 */
package com.moriset.bcephal.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.security.domain.ProfileData;

/**
 * @author Moriset
 *
 */
public interface ProfileDataRepository extends PersistentRepository<ProfileData> {

	@Query(value = "SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.security.domain.ProfileData model where model.clientId = :clientId ORDER BY name")
	List<Nameable> findAllAsNameablesByClient(@Param("clientId")Long clientId);
	
}

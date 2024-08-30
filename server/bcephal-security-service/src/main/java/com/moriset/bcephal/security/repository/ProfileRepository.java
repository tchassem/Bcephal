/**
 * 
 */
package com.moriset.bcephal.security.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.security.domain.Profile;
import com.moriset.bcephal.security.domain.ProfileType;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface ProfileRepository extends MainObjectRepository<Profile> {

	List<Profile> findByClient(String client);

	List<Profile> findByClientIdOrderByNameAsc(Long clientId);
	
	List<Profile> findByClientIdAndName(Long clientId, String name);
	
	List<Profile> findByType(ProfileType type);
	
	void deleteByClientId(Long clientId);
	
}

/**
 * 
 */
package com.moriset.bcephal.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.security.domain.ProfileUser;

/**
 * @author Moriset
 *
 */
public interface ProfileUserRepository extends PersistentRepository<ProfileUser> {

	List<ProfileUser> findByProfileId(Long profileId);
	
	List<ProfileUser> findByUserId(Long userId);
	
	@Query("SELECT new com.moriset.bcephal.domain.Nameable(p.id, p.name) FROM com.moriset.bcephal.security.domain.ProfileUser pu LEFT JOIN com.moriset.bcephal.security.domain.Profile p ON pu.profileId = p.id WHERE pu.clientId = :clientId AND pu.userId = :userId  ORDER By p.name ASC")
	List<Nameable> getProfilesByClientIdAndUserId(@Param("clientId")Long clientId, @Param("userId")Long userId);
	
	@Query("SELECT new com.moriset.bcephal.domain.Nameable(u.id, u.name) FROM com.moriset.bcephal.security.domain.ProfileUser pu LEFT JOIN com.moriset.bcephal.security.domain.User u ON pu.userId = u.id WHERE pu.profileId = :profileId ORDER By u.name ASC")
	List<Nameable> getUsersByProfileId(@Param("profileId") Long profileId);
			
	void deleteByProfileIdAndUserId(Long profileId, Long userId);
	
	void deleteByProfileId(Long profileId);
	
	void deleteByUserId(Long userId);
	
	List<ProfileUser> findByProfileIdAndUserIdAndClientId(Long profileId, Long userId, Long clientId);
	List<ProfileUser> findByProfileIdAndUserId(Long profileId, Long userId);
	
}

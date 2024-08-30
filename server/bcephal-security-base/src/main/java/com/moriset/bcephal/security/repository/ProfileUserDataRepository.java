/**
 * 
 */
package com.moriset.bcephal.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.security.domain.ProfileUserData;
import com.moriset.bcephal.security.domain.UserData;

/**
 * @author Moriset
 *
 */
public interface ProfileUserDataRepository extends PersistentRepository<ProfileUserData> {

	@Query("SELECT u FROM com.moriset.bcephal.security.domain.ProfileUserData p LEFT JOIN com.moriset.bcephal.security.domain.UserData u ON (p.userId = u.id) WHERE p.id = :profileId")
	List<UserData> findUsersByProfileId(@Param("profileId")Long profileId);
}

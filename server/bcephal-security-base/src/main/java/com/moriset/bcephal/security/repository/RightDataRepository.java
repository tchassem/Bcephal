/**
 * 
 */
package com.moriset.bcephal.security.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.security.domain.RightData;
import com.moriset.bcephal.security.domain.RightLevel;

/**
 * @author Moriset
 *
 */
public interface RightDataRepository extends PersistentRepository<RightData> {

	@Query(value = "SELECT objectId FROM RightData WHERE profileId = :profileId AND level = :level AND functionality = :functionality AND projectCode = :projectCode")
	Optional<List<Long>> findByProfileIdAndLevel(@Param("profileId") Long profilId, @Param("level")RightLevel level, @Param("functionality")String functionality, @Param("projectCode")String projectCode);
}

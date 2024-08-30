/**
 * 
 */
package com.moriset.bcephal.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.security.domain.Right;

/**
 * @author Moriset
 *
 */
@Repository
public interface RightRepository extends PersistentRepository<Right> {

	List<Right> findByProfileId(Long profileId);
	
	List<Right> findByProfileIdAndObjectIdIsNull(Long profileId);
	
	@Query("SELECT r FROM Right r  WHERE (r.profileId = :profileId AND (r.objectId Is Null OR (r.projectCode = :projectCode AND r.functionality like :functionality)))")
	List<Right> findByProfileIdAndObjectIdIsNullOrFunctionalityLike(@Param("profileId")Long profileId,@Param("projectCode")String projectCode,@Param("functionality")String functionality);
	
	List<Right> findByProfileIdAndFunctionalityAndObjectIdAndProjectCode(Long profileId, String functionalityCode,Long objectId, String projectCode);
	
	List<Right> findByProfileIdAndFunctionalityAndProjectCode(Long profileId, String functionalityCode,String projectCode);
	
	@Query("SELECT distinct r FROM Right r  WHERE r.profileId = :profileId OR (r.profileId = :profileId AND r.projectCode = :projectCode)")
	List<Right> findByProfileIdAndProjectCode(@Param("profileId") Long profileId,@Param("projectCode")String projectCode);

	List<Right> findByProfileIdAndProjectCodeAndFunctionalityLike(Long profileId, String projectCode, String functionality);

	void deleteByProfileId(Long profileId);
	
}

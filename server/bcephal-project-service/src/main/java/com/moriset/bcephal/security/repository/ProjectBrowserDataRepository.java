/**
 * 
 */
package com.moriset.bcephal.security.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.security.domain.ProjectBrowserData;

/**
 * @author
 *
 */
@Repository
public interface ProjectBrowserDataRepository extends JpaRepository<ProjectBrowserData, Long>, JpaSpecificationExecutor<ProjectBrowserData> {

	public Optional<ProjectBrowserData> findByCode(String code);

	public List<ProjectBrowserData> findByNameIgnoreCase(String name);

	@Query("SELECT p FROM com.moriset.bcephal.security.domain.ProjectBrowserData p LEFT JOIN com.moriset.bcephal.security.domain.ProjectProfile pf ON p.id = pf.projectId "
			+ "WHERE p.subscriptionId = :subscriptionId "
			+ "AND p.defaultProject = :defaultProject "
			+ "AND pf.profileId = :profileId")
	public List<ProjectBrowserData> findBySubscriptionIdAndDefaultProject(
			@Param("subscriptionId") Long subscriptionId, 
			@Param("profileId") Long profileId, 
			@Param("defaultProject") boolean defaultProject);

	// @Query("SELECT p FROM ProjectBrowserData p WHERE p.subscriptionId =
	// :subscriptionId AND UPPER(p.name) = UPPER(:projectNames)")
	public List<ProjectBrowserData> findBySubscriptionIdAndNameIgnoreCase(Long subscriptionId, String name);

	// @Query("SELECT p FROM ProjectBrowserData p WHERE p.subscriptionId =
	// :subscriptionId")
	public List<ProjectBrowserData> findBySubscriptionId(Long subscriptionId);
	
	@Query("SELECT p FROM com.moriset.bcephal.security.domain.ProjectBrowserData p LEFT JOIN com.moriset.bcephal.security.domain.ProjectProfile pf ON p.id = pf.projectId WHERE p.subscriptionId = :subscriptionId "
			+ "AND pf.profileId = :profileId")
	public List<ProjectBrowserData> getBySubscriptionIdAndProfileId(
			@Param("subscriptionId") Long subscriptionId, 
			@Param("profileId") Long profileId);
	
	@Query("SELECT p FROM com.moriset.bcephal.security.domain.ProjectBrowserData p LEFT JOIN com.moriset.bcephal.security.domain.ProjectProfile pf ON p.id = pf.projectId WHERE p.subscriptionId = :subscriptionId "
			+ "AND pf.profileId = :profileId AND p.name = :projectName")
	public List<ProjectBrowserData> getBySubscriptionIdAndProfileIdAndProjectName(
			@Param("subscriptionId") Long subscriptionId, 
			@Param("profileId") Long profileId,
			@Param("projectName") String projectName);

	// @Query("SELECT p FROM ProjectBrowserData p WHERE p.subscriptionId =
	// :subscriptionId AND p.code = :projectCode")
	public Optional<ProjectBrowserData> findBySubscriptionIdAndCode(Long subscriptionId, String code);

	public Optional<ProjectBrowserData> findBySubscriptionIdAndId(Long subscriptionId, Long id);

//    @Query("SELECT p.code FROM ProjectBrowserData  p WHERE p.subscriptionId = :subscriptionId AND p.name = :projectName")
//	public String getCode(@Param("subscriptionId")Long subscriptionId, @Param("projectName") String projectName);
}

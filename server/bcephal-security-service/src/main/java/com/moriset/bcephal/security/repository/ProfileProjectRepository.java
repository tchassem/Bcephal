/**
 * 
 */
package com.moriset.bcephal.security.repository;

import java.util.List;

import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.security.domain.ProfileProject;

/**
 * @author Moriset
 *
 */
public interface ProfileProjectRepository extends PersistentRepository<ProfileProject> {

	List<ProfileProject> findByProfileId(Long profileId);
	
	List<ProfileProject> findByProjectId(Long projectId);
	
	List<ProfileProject> findByProjectCode(String projectId);
	
	void deleteByProfileIdAndProjectId(Long profileId, Long projectId);
	
	void deleteByProfileIdAndProjectCode(Long profileId, String projectCode);
	
	void deleteByProfileId(Long profileId);
	
	void deleteByProjectId(Long projectId);
	
	void deleteByProjectCode(String projectCode);
	
}
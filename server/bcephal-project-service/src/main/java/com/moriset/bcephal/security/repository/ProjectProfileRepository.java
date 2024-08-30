/**
 * 
 */
package com.moriset.bcephal.security.repository;

import java.util.List;

import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.security.domain.ProjectProfile;

/**
 * @author Moriset
 *
 */
public interface ProjectProfileRepository extends PersistentRepository<ProjectProfile> {

	List<ProjectProfile> findByProjectId(Long projectId);
	
	List<ProjectProfile> findByProfileId(Long profileId);
		
	void deleteByProjectId(Long projectId);
	
	void deleteByProjectCode(String projectCode);
	
}
/**
 * 
 */
package com.moriset.bcephal.task.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.task.domain.TaskAudience;

@Repository
public interface TaskAudienceRepository extends PersistentRepository<TaskAudience> {
	
	
}

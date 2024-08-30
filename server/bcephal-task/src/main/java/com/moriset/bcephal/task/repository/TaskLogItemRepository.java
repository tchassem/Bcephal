/**
 * 
 */
package com.moriset.bcephal.task.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.repository.MainObjectRepository;
import com.moriset.bcephal.task.domain.TaskLogItem;

@Repository
public interface TaskLogItemRepository extends MainObjectRepository<TaskLogItem> {
	
	
}

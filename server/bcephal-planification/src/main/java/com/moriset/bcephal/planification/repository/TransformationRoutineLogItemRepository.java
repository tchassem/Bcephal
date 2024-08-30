/**
 * 
 */
package com.moriset.bcephal.planification.repository;

import com.moriset.bcephal.planification.domain.routine.TransformationRoutineLogItem;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Moriset
 *
 */
public interface TransformationRoutineLogItemRepository extends PersistentRepository<TransformationRoutineLogItem> {

	public void deleteByLogId(Long logId);
	
}

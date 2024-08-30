package com.moriset.bcephal.repository.routine;

import java.util.List;

import com.moriset.bcephal.domain.routine.RoutineExecutor;
import com.moriset.bcephal.repository.PersistentRepository;

public interface RoutineExecutorReopository extends PersistentRepository<RoutineExecutor> {

	List<RoutineExecutor> findByObjectIdAndObjectType(Long id, String type);
	
}

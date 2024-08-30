package com.moriset.bcephal.scheduler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.repository.MainObjectRepository;
import com.moriset.bcephal.scheduler.domain.SchedulerPlanner;

public interface SchedulerPlannerRepository extends MainObjectRepository<SchedulerPlanner> {

	List<SchedulerPlanner> findByActiveAndScheduledAndCronExpressionIsNotNull(boolean active, boolean scheduled);
	
	@Query("SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.scheduler.domain.SchedulerPlanner model ORDER BY name")
	List<Nameable> findAllAsNameables();

}

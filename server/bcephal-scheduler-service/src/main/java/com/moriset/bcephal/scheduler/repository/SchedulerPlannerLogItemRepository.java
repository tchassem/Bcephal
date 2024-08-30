package com.moriset.bcephal.scheduler.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.scheduler.domain.SchedulerPlannerLogItem;

public interface SchedulerPlannerLogItemRepository extends PersistentRepository<SchedulerPlannerLogItem> {

	@Modifying
	@Query(value = "DELETE FROM BCP_SCHEDULER_PLANNER_LOG_ITEM WHERE logId = :logId", nativeQuery = true)
	void deleteByLogId(@Param("logId") Long logId);
}

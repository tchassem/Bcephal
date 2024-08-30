package com.moriset.bcephal.scheduler.repository.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.moriset.bcephal.scheduler.domain.api.SchedulerLogResponse;

public interface SchedulerLogResponseRepository extends JpaRepository<SchedulerLogResponse, Long>, JpaSpecificationExecutor<SchedulerLogResponse> {

	List<SchedulerLogResponse> findByOperationCode(String operationCode);
	
}

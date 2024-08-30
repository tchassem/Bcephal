package com.moriset.bcephal.etl.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.moriset.bcephal.etl.domain.JobDataExecution;

public interface JobDataExecutionRepository extends JpaRepository<JobDataExecution, Long>,JpaSpecificationExecutor<JobDataExecution> {

	@Query(value = "select item from com.moriset.bcephal.etl.domain.JobDataExecution item order by id limit 1", nativeQuery = false)
	List<JobDataExecution> findByLimit(int count);
	
	List<JobDataExecution> findByVersion(long version);
	
	List<JobDataExecution> findByStatus(String status);
	
	List<JobDataExecution> findAllByInstanceId(long instanceId);
	
	List<JobDataExecution> findByExitCode(String code);
	
//	List<JobDataExecution> findByMessage();

	List<JobDataExecution> findByCreationDate(Date creationDate);
	
	List<JobDataExecution> findByStartDate(Date startDate);
	
	List<JobDataExecution> findByEndDate(Date endDate);
	
	List<JobDataExecution> findByModificationDate(Date modificatioDate);

}

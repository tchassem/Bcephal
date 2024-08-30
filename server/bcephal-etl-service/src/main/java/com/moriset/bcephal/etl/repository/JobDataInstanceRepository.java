package com.moriset.bcephal.etl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moriset.bcephal.etl.domain.JobDataInstance;

public interface JobDataInstanceRepository extends JpaRepository<JobDataInstance, Long> {
	
	public  List<JobDataInstance> findByName(String name);
	
	public List<JobDataInstance> findByVersion(long version);

}

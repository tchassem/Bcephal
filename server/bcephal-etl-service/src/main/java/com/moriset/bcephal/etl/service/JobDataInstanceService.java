package com.moriset.bcephal.etl.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.etl.domain.JobDataInstance;
import com.moriset.bcephal.etl.repository.JobDataInstanceRepository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Service
public class JobDataInstanceService {
	
	@Autowired
	private JobDataInstanceRepository instanceRepository;
	
	
	
	public List<JobDataInstance> getAllInstances(){
		
		log.trace("try to find all instances");
	   
		return instanceRepository.findAll();
	}
	
	public Optional<JobDataInstance> getInstanceById(long id) {
		
		Optional<JobDataInstance> instance = instanceRepository.findById(id);
		if(instance.isEmpty()) {
			 log.trace("this instance does not existe");
			return null;
		}
		log.trace("try to find instance by id");
		return instance;
	}
	
	public List<JobDataInstance> getAllInstancesByName(String name){
		log.trace("try to find all instances");
		
		return instanceRepository.findByName(name);
	}
	
	public List<JobDataInstance> getInstanceByVersion(long version) {
		log.trace("try to find instance by version");
		
		return instanceRepository.findByVersion( version);
	}
		
	public JobDataInstance saveInstance(JobDataInstance job) {

		log.trace("try to save job instance");
		
		return instanceRepository.save(job);
		
	}
	

}

package com.moriset.bcephal.integration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.integration.domain.PontoSynchronization;
import com.moriset.bcephal.repository.PersistentRepository;

public interface PontoSynchronizationRepository extends PersistentRepository<PontoSynchronization> {

	@Query("SELECT s FROM PontoSynchronization s WHERE s.id = (SELECT MAX(id) FROM PontoSynchronization WHERE connectEntityId = :entityId AND resourceId = :resourceId)")
	PontoSynchronization  findBySynchronization(@Param("entityId") Long entityId, @Param("resourceId")String resourceId);
	
	List<PontoSynchronization>  findAllByConnectEntityIdAndResourceIdOrderByIdDesc(Long connectEntityId, String resourceId);
	
	

}

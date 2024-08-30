package com.moriset.bcephal.scheduler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.repository.MainObjectRepository;
import com.moriset.bcephal.scheduler.domain.PresentationTemplate;

public interface PresentationTemplateRepository extends MainObjectRepository<PresentationTemplate> {

	@Query("SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.scheduler.domain.PresentationTemplate model ORDER BY name")
	List<Nameable> findAllAsNameables();
	
	@Query(value = "SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.scheduler.domain.PresentationTemplate model where model.id NOT IN :excludedIds ORDER BY name")
	List<Nameable> findAllAsNameablesExcludeIds(@Param("excludedIds")List<Long> excludedIds);
	
}

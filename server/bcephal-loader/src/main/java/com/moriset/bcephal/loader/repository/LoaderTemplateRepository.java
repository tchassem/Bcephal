/**
 * 3 juin 2024 - LoaderTemplateRepository.java
 *
 */
package com.moriset.bcephal.loader.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.loader.domain.LoaderTemplate;
import com.moriset.bcephal.repository.MainObjectRepository;

/**
 * @author Emmanuel Emmeni
 *
 */
@Repository
public interface LoaderTemplateRepository extends MainObjectRepository<LoaderTemplate> {
	
	LoaderTemplate findByCode(String code);
	
	@Query("SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.loader.domain.LoaderTemplate model ORDER BY name")
	List<Nameable> findAllAsNameables();

}

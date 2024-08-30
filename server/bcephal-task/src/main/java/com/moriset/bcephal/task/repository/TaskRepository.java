/**
 * 
 */
package com.moriset.bcephal.task.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.repository.MainObjectRepository;
import com.moriset.bcephal.task.domain.Task;
import com.moriset.bcephal.task.domain.TaskCategory;

@Repository
public interface TaskRepository extends MainObjectRepository<Task> {
	
	@Query("SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.task.domain.Task model ORDER BY name")
	List<Nameable> findAllAsNameables();

	@Query(value = "SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.task.domain.Task model where model.id NOT IN :excludedIds ORDER BY name")
	List<Nameable> findAllAsNameablesExcludeIds(@Param("excludedIds")List<Long> excludedIds);
	
	@Query(value = "SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.task.domain.Task model where model.category = :category ORDER BY name")
	List<Nameable> findAllAsNameablesByCategory(@Param("category")TaskCategory category);
}

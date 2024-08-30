/**
 * 
 */
package com.moriset.bcephal.alarm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.alarm.domain.Alarm;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.repository.MainObjectRepository;

/**
 * @author Moriset
 *
 */
public interface AlarmRepository extends MainObjectRepository<Alarm> {

	List<Alarm> findByActiveAndScheduledAndCronExpressionIsNotNull(boolean active, boolean scheduled);

	@Query("SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.alarm.domain.Alarm model ORDER BY name")
	List<Nameable> findAllAsNameables();
	
	@Query(value = "SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.alarm.domain.Alarm model where model.id NOT IN :excludedIds ORDER BY name")
	List<Nameable> findAllAsNameablesExcludeIds(@Param("excludedIds")List<Long> excludedIds);
}

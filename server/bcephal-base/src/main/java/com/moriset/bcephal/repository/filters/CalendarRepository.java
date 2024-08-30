/**
 * 
 */
package com.moriset.bcephal.repository.filters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.dimension.CalendarCategory;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface CalendarRepository extends PersistentRepository<CalendarCategory> {

	CalendarCategory findByNameIgnoreCase(String name);

	@Query("SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.domain.dimension.CalendarCategory model ORDER BY name")
	List<Nameable> findAllAsNameables();
}

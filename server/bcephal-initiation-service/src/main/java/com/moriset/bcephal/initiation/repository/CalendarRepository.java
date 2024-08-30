/**
 * 
 */
package com.moriset.bcephal.initiation.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.initiation.domain.CalendarCategory;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface CalendarRepository extends PersistentRepository<CalendarCategory> {

	CalendarCategory findByNameIgnoreCase(String name);
}

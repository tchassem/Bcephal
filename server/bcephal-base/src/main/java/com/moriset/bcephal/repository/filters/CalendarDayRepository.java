/**
 * 
 */
package com.moriset.bcephal.repository.filters;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.dimension.CalendarDay;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface CalendarDayRepository extends PersistentRepository<CalendarDay> {

}

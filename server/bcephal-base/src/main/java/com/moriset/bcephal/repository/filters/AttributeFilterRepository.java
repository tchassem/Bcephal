/**
 * 
 */
package com.moriset.bcephal.repository.filters;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.filters.AttributeFilter;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface AttributeFilterRepository extends PersistentRepository<AttributeFilter> {

}

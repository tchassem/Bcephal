/**
 * 
 */
package com.moriset.bcephal.initiation.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface ParameterRepository extends PersistentRepository<Parameter> {

}

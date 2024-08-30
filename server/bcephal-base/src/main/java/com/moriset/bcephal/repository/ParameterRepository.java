/**
 * 
 */
package com.moriset.bcephal.repository;

import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;

/**
 * @author Moriset
 *
 */
public interface ParameterRepository extends PersistentRepository<Parameter> {

	Parameter findByCodeAndParameterType(String code, ParameterType parameterType);

}

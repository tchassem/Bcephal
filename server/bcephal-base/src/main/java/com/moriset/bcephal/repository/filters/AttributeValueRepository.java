/**
 * 
 */
package com.moriset.bcephal.repository.filters;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.AttributeValue;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface AttributeValueRepository extends PersistentRepository<AttributeValue> {

	AttributeValue findByNameIgnoreCase(String name);

	AttributeValue findByAttributeAndNameIgnoreCase(Attribute attribute, String name);

}

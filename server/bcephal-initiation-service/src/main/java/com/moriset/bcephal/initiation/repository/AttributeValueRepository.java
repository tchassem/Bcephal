package com.moriset.bcephal.initiation.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.initiation.domain.Attribute;
import com.moriset.bcephal.initiation.domain.AttributeValue;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * 
 * @author MORISET-004
 *
 */
@Repository
public interface AttributeValueRepository extends PersistentRepository<AttributeValue> {

	List<AttributeValue> findByAttribute(Attribute attribute);

}

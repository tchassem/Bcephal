package com.moriset.bcephal.initiation.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.initiation.domain.Attribute;
import com.moriset.bcephal.initiation.domain.Entity;

/**
 * 
 * @author MORISET-004
 *
 */
@Repository
public interface AttributeRepository extends DimensionRepository<Attribute> {

	List<Attribute> findByEntity(Entity entity);

}

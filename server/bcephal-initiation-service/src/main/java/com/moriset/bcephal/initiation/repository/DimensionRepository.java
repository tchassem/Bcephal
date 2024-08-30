package com.moriset.bcephal.initiation.repository;

import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;

import com.moriset.bcephal.initiation.domain.Dimension;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * 
 * @author MORISET-004
 *
 * @param <P>
 */
@NoRepositoryBean
public interface DimensionRepository<P extends Dimension> extends PersistentRepository<P> {

	P findByName(String name);
	
	P findByNameIgnoreCase(String name);

	//P findByParent(Long parent);

	List<P> findByParentIsNull();

}

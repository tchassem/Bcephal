package com.moriset.bcephal.repository.filters;

import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;

import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * 
 * @author MORISET-004
 *
 * @param <P>
 */
@NoRepositoryBean
public interface DimensionRepository<P extends Dimension> extends PersistentRepository<P> {

	List<P> findByParentIsNull();

	P findByNameIgnoreCase(String name);

	P findByName(String name);

}

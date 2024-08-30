package com.moriset.bcephal.security.repository;

import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;

import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.security.domain.MainObject;

/**
 * 
 * @author MORISET-004
 *
 * @param <P>
 */
@NoRepositoryBean
public interface MainObjectRepository<P extends MainObject> extends PersistentRepository<P> {

	List<P> findByName(String name);

}

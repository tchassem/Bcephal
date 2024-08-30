package com.moriset.bcephal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import com.moriset.bcephal.domain.BGroup;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.Nameable;

/**
 * 
 * @author MORISET-004
 *
 * @param <P>
 */
@NoRepositoryBean
public interface MainObjectRepository<P extends MainObject> extends PersistentRepository<P> {

	List<P> findByName(String name);

	P findByGroup(BGroup group);
	
	@Query("SELECT new com.moriset.bcephal.domain.Nameable(P.id, P.name) FROM #{#entityName} P ORDER BY name")
	List<Nameable> findGenericAllAsNameables();

}

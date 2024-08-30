package com.moriset.bcephal.license.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import com.moriset.bcephal.license.domain.MainObject;
import com.moriset.bcephal.license.domain.Nameable;

@NoRepositoryBean
public interface MainObjectRepository<P extends MainObject> extends PersistentRepository<P> {

	List<P> findByName(String name);
	
	@Query("SELECT new com.moriset.bcephal.license.domain.Nameable(P.id, P.name) FROM #{#entityName} P ORDER BY name")
	List<Nameable> findGenericAllAsNameables();

}

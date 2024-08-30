/**
 * 
 */
package com.moriset.bcephal.grid.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.SmartGrille;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * @author Joseph Wambo
 *
 */
public interface SmartGrilleRepository extends PersistentRepository<SmartGrille> {

	@Query("SELECT new com.moriset.bcephal.domain.Nameable(id, name) FROM SmartGrille where type = ?1")
	public List<Nameable> findByType(GrilleType type);
	
	@Query("SELECT grid FROM SmartGrille grid where grid.type IN ?1")
	public List<SmartGrille> findByTypes(List<GrilleType> types);

	@Query("SELECT grid FROM SmartGrille grid where grid.type IN :types AND grid.id NOT IN :excludedIds")
	public List<SmartGrille> findByTypesAllExclude(@Param("types")List<GrilleType> types, @Param("excludedIds")List<Long> excludedIds);
	
	@Query("SELECT grid.name FROM SmartGrille grid where grid.id = :id")
	public String findNameById(@Param("id")Long id);
	
}

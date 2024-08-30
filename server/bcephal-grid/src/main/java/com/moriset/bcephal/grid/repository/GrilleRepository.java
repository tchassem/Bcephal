package com.moriset.bcephal.grid.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleStatus;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.repository.MainObjectRepository;

/**
 * 
 * @author MORISET-004
 *
 * @param <P>
 */
@Repository
public interface GrilleRepository extends MainObjectRepository<Grille> {

	@Modifying
	@Query("UPDATE Grille SET status = ?2 where id = ?1")
	int changeStatus(Long id, GrilleStatus status);
	
	@Query("SELECT new com.moriset.bcephal.domain.Nameable(id, name) FROM Grille where type = ?1")
	public List<Nameable> findByType(GrilleType type);

	@Query("SELECT new com.moriset.bcephal.domain.Nameable(id, name) FROM Grille where type = :types AND id NOT IN :excludedIds")
	public List<Nameable> findByTypeExclude(@Param("types")GrilleType type, @Param("excludedIds")List<Long> excludedIds);
	
	List<Grille> findByNameAndType(String name,GrilleType type);
	
	
	
}

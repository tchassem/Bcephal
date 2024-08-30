package com.moriset.bcephal.grid.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.repository.PersistentRepository;

/**
 * 
 * @author MORISET-004
 *
 * @param <P>
 */
public interface GrilleColumnRepository extends PersistentRepository<GrilleColumn> {

	List<GrilleColumn> findByGrid(Grille gridId);
	
	@Query("SELECT col FROM com.moriset.bcephal.grid.domain.GrilleColumn col WHERE col.grid.id = :gridId")
	List<GrilleColumn> findByGrid(@Param("gridId")Long gridId);
	
	@Query("SELECT id FROM com.moriset.bcephal.grid.domain.GrilleColumn WHERE grid.id = :gridId")
	List<Long> getColumnIds(@Param("gridId") Long gridId);


}

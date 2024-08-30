/**
 * 
 */
package com.moriset.bcephal.reconciliation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.reconciliation.domain.AutoReco;
import com.moriset.bcephal.repository.MainObjectRepository;

/**
 * @author MORISET-004
 *
 */
@Repository
public interface AutoRecoRepository extends MainObjectRepository<AutoReco> {

	List<AutoReco> findByActiveAndScheduledAndCronExpressionIsNotNull(boolean active,boolean scheduled);
	
	@Query(value = "SELECT id, name FROM BCP_TRANSFORMATION_ROUTINE ORDER BY name", nativeQuery = true)
	List<Object[]> findRoutines();
	
	@Query("SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.reconciliation.domain.AutoReco model ORDER BY name")
	List<Nameable> findAllAsNameables();

	@Query(value = "SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.reconciliation.domain.AutoReco model where model.id NOT IN :excludedIds ORDER BY name")
	List<Nameable> findAllAsNameablesExcludeIds(@Param("excludedIds")List<Long> excludedIds);
}

/**
 * 
 */
package com.moriset.bcephal.reconciliation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModel;
import com.moriset.bcephal.repository.MainObjectRepository;

/**
 * @author MORISET-004
 *
 */
@Repository
public interface ReconciliationModelRepository extends MainObjectRepository<ReconciliationModel> {

	@Query("SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM ReconciliationModel model ORDER BY name")
	List<Nameable> findAllAsNameables();
	
}

/**
 * 4 avr. 2024 - ReconciliationUnionModelRepository.java
 *
 */
package com.moriset.bcephal.reconciliation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionModel;
import com.moriset.bcephal.repository.MainObjectRepository;

/**
 * @author Emmanuel Emmeni
 *
 */
@Repository
public interface ReconciliationUnionModelRepository extends MainObjectRepository<ReconciliationUnionModel> {

	@Query("SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM ReconciliationUnionModel model ORDER BY name")
	List<Nameable> findAllAsNameables();

}

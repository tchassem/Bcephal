package com.moriset.bcephal.billing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.billing.domain.BillingModel;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.repository.MainObjectRepository;

/**
 * 
 * @author ROLAND
 *
 * @param <P>
 */
@Repository
public interface BillingModelRepository extends MainObjectRepository<BillingModel> {

	@Query("SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.billing.domain.BillingModel model ORDER BY name")
	List<Nameable> findAllAsNameables();
	
	@Query(value = "SELECT new com.moriset.bcephal.domain.Nameable(model.id, model.name) FROM com.moriset.bcephal.billing.domain.BillingModel model where model.id NOT IN :excludedIds ORDER BY name")
	List<Nameable> findAllAsNameablesExcludeIds(@Param("excludedIds")List<Long> excludedIds);
	
}
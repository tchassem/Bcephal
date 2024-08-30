/**
 * 
 */
package com.moriset.bcephal.billing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moriset.bcephal.billing.domain.BillingRunOutcome;
import com.moriset.bcephal.repository.MainObjectRepository;

@Repository
public interface BillingRunOutcomeRepository extends MainObjectRepository<BillingRunOutcome> {

	@Query("SELECT runNumber FROM com.moriset.bcephal.billing.domain.BillingRunOutcome WHERE id In :ids")
	List<String> findRunNbrsByIds(@Param("ids") List<Long> ids);

}

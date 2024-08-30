package com.moriset.bcephal.billing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.moriset.bcephal.billing.domain.BillingTemplateLabel;
import com.moriset.bcephal.repository.PersistentRepository;

public interface BillingTemplateLabelRepository extends PersistentRepository<BillingTemplateLabel>{
	
	@Query(value = "SELECT id, billing, filter, code, position FROM BCP_BILLING_MODEL_LABEL WHERE billing = ?1 ORDER BY id", nativeQuery = true)
	List<BillingTemplateLabel> findAllByTemplate(Long templateId);

}

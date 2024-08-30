package com.moriset.bcephal.reconciliation.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.reconciliation.domain.ReconciliationModelEnrichment;
import com.moriset.bcephal.repository.PersistentRepository;

@Repository
public interface ReconciliationModelEnrichmentRepository extends PersistentRepository<ReconciliationModelEnrichment> {

}

package com.moriset.bcephal.reconciliation.repository;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.reconciliation.domain.AutoRecoRankingItem;
import com.moriset.bcephal.repository.PersistentRepository;

@Repository
public interface AutoRecoRankingItemRepository extends PersistentRepository<AutoRecoRankingItem> {

}

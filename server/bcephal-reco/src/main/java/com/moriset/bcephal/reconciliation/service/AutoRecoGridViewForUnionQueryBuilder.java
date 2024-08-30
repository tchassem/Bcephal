/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import com.moriset.bcephal.reconciliation.domain.AutoReco;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModelSide;

import jakarta.persistence.EntityManager;

public class AutoRecoGridViewForUnionQueryBuilder  extends AutoRecoGridViewQueryBuilder {
	
	
	public AutoRecoGridViewForUnionQueryBuilder(AutoReco reco, boolean forSecondaryGrid, EntityManager entityManager, String viewName) {
		super(reco, forSecondaryGrid, entityManager, viewName);
	}
	
	public AutoRecoGridViewForUnionQueryBuilder(AutoReco reco, boolean forSecondaryGrid, EntityManager entityManager, String viewName, ReconciliationModelSide side, boolean forPartial) {
		super(reco, forSecondaryGrid, entityManager, viewName, side, forPartial);
	}
	
	
	
	
}

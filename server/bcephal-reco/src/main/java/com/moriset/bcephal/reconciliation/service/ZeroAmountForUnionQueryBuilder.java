/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.reconciliation.domain.AutoReco;

import jakarta.persistence.EntityManager;


public class ZeroAmountForUnionQueryBuilder  extends ZeroAmountQueryBuilder {
	
	
	/**
	 * @param userSession
	 */
	public ZeroAmountForUnionQueryBuilder(AutoReco reco, boolean forSecondaryGrid, EntityManager entityManager) {
		super(reco, forSecondaryGrid, entityManager);
	}
		
	
	@Override
	protected String buildSelectPart() {
		String selectPart = "SELECT " + UniverseParameters.ID + "::TEXT AS id";		
		return selectPart;
	}
		
}

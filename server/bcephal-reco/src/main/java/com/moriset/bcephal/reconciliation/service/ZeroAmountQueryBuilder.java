/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.reconciliation.domain.AutoReco;

import jakarta.persistence.EntityManager;


/**
 * @author Joseph Wambo
 *
 */
public class ZeroAmountQueryBuilder  extends AutoRecoGridQueryBuilder {
	
	
	/**
	 * @param userSession
	 */
	public ZeroAmountQueryBuilder(AutoReco reco, boolean forSecondaryGrid, EntityManager entityManager) {
		super(reco, forSecondaryGrid, entityManager);
	}
		
	
	@Override
	protected String buildSelectPart() {
		String selectPart = "SELECT DISTINCT " + UniverseParameters.ID;		
		return selectPart;
	}
		
}

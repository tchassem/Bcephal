/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import com.moriset.bcephal.grid.domain.UnionGridItem;
import com.moriset.bcephal.reconciliation.domain.AutoReco;

import jakarta.persistence.EntityManager;


public class ZeroAmountUnionQueryBuilder  extends AutoRecoUnionQueryBuilder {
	
	
	/**
	 * @param userSession
	 */
	public ZeroAmountUnionQueryBuilder(AutoReco reco, boolean forSecondaryGrid, EntityManager entityManager) {
		super(reco, forSecondaryGrid, entityManager);
	}
		
	
	@Override
	protected String buildSelectPart(UnionGridItem unionGridItem) {
		String selectPart = "SELECT id::TEXT || '_' ||  " + unionGridItem.getGrid().getId() + "::TEXT as id";
		return selectPart;
	}
			
}

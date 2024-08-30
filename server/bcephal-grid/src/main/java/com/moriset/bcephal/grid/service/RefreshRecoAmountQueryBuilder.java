/**
 * 
 */
package com.moriset.bcephal.grid.service;

import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;

/**
 * @author Moriset
 *
 */
public class RefreshRecoAmountQueryBuilder extends ReconciliationGridQueryBuilder {

	public RefreshRecoAmountQueryBuilder(GrilleDataFilter filter) {
		super(filter);
	}
		
	
	/**
	 * Build query
	 * @param filter
	 * @return
	 */
	@Override
	public String buildQuery() {	
		String amountCol = new Measure(getFilter().getRecoData().getAmountMeasureId()).getUniverseTableColumnName();
		String recopnciliatedCol = new Measure(getFilter().getRecoData().getReconciliatedMeasureId()).getUniverseTableColumnName();
		String remainningCol = new Measure(getFilter().getRecoData().getRemainningMeasureId()).getUniverseTableColumnName();
		String sql = "UPDATE " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME + " SET "
				+ remainningCol + " = " + amountCol + ", "
				+ recopnciliatedCol + " = 0 ";
		String where = buildWherePart();
		sql += where;
		
		return sql;
	}
		

}

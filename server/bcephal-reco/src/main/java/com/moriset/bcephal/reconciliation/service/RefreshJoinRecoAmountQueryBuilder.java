/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import com.moriset.bcephal.grid.domain.AbstractSmartGrid;
import com.moriset.bcephal.grid.domain.AbstractSmartGridColumn;
import com.moriset.bcephal.grid.service.JoinFilter;
import com.moriset.bcephal.grid.service.JoinRecoQueryBuilder;

public class RefreshJoinRecoAmountQueryBuilder extends JoinRecoQueryBuilder {

	private String gridDbName;
	private AbstractSmartGrid<?> grid;
	
	public RefreshJoinRecoAmountQueryBuilder(JoinFilter filter, AbstractSmartGrid<?> grid, String gridDbName) {
		super(filter, null, null);
		this.gridDbName = gridDbName;
		this.grid = grid;
	}
	
	
	@Override
	public String buildQuery() throws Exception {	
		String sql = null;
		AbstractSmartGridColumn reconciliated = grid.getColumnById(getFilter().getRecoData().getReconciliatedMeasureId());
		AbstractSmartGridColumn remaining = grid.getColumnById(getFilter().getRecoData().getRemainningMeasureId());	
		AbstractSmartGridColumn amount = grid.getColumnById(getFilter().getRecoData().getAmountMeasureId());	
		
		sql = "UPDATE " + gridDbName + " SET "
				+ remaining.getDbColumnName() + " = " + amount.getDbColumnName() + ", "
				+ reconciliated.getDbColumnName() + " = 0 ";
		String where = buildWherePart();
		sql += where;
		
		return sql;
	}
		
	@Override
	protected String buildWherePart() throws Exception {
		AbstractSmartGridColumn reco = grid.getColumnById(getFilter().getRecoData().getRecoAttributeId());
		AbstractSmartGridColumn preco = grid.getColumnById(getFilter().getRecoData().getPartialRecoAttributeId());
		String sql = "WHERE (" + reco.getDbColumnName() + " IS NULL OR " + reco.getDbColumnName() + " = '')"
				+ " AND (" + preco.getDbColumnName() + " IS NULL OR " + preco.getDbColumnName() + " = '')";
			
		return sql;
	}
	
	@Override
	public JoinFilter getFilter() {
		return (JoinFilter)super.getFilter();
	}
	
}
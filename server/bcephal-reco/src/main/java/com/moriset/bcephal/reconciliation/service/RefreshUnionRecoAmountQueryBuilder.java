package com.moriset.bcephal.reconciliation.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.grid.domain.AbstractSmartGridColumn;
import com.moriset.bcephal.grid.domain.SmartMaterializedGrid;
import com.moriset.bcephal.grid.domain.UnionGrid;
import com.moriset.bcephal.grid.domain.UnionGridItem;
import com.moriset.bcephal.grid.service.UnionGridFilter;
import com.moriset.bcephal.grid.service.UnionGridQueryBuilder;

public class RefreshUnionRecoAmountQueryBuilder  extends UnionGridQueryBuilder {

	private UnionGrid unionGrid;
	
	public RefreshUnionRecoAmountQueryBuilder(UnionGridFilter filter, UnionGrid unionGrid) {
		super(filter, null, null);
		this.unionGrid = unionGrid;
	}
	
	
	public List<String> buildQueries() throws Exception {	
		List<String> sqls = new ArrayList<>();
		for(UnionGridItem item : unionGrid.getItemListChangeHandler().getItems()) {
			String sql = buildQuery(item);
			if(StringUtils.hasText(sql)) {
				sqls.add(sql);
			}
		}		
		return sqls;
	}
	
	@Override
	public String buildQuery(UnionGridItem item) throws Exception {	
		SmartMaterializedGrid grid = item.getGrid();
		String sql = null;
		AbstractSmartGridColumn reconciliated = item.getGridColumn(getFilter().getRecoData().getReconciliatedMeasureId());
		AbstractSmartGridColumn remaining = item.getGridColumn(getFilter().getRecoData().getRemainningMeasureId());	
		AbstractSmartGridColumn amount = item.getGridColumn(getFilter().getRecoData().getAmountMeasureId());	
		
		sql = "UPDATE " + grid.getDbTableName() + " SET "
				+ remaining.getDbColumnName() + " = " + amount.getDbColumnName() + ", "
				+ reconciliated.getDbColumnName() + " = 0 ";
		String where = buildWherePart(item);
		sql += where;
		
		return sql;
	}
		
	@Override
	protected String buildWherePart(UnionGridItem item) throws Exception {
		AbstractSmartGridColumn reco = item.getGridColumn(getFilter().getRecoData().getRecoAttributeId());
		AbstractSmartGridColumn preco = item.getGridColumn(getFilter().getRecoData().getPartialRecoAttributeId());
		String sql = "WHERE (" + reco.getDbColumnName() + " IS NULL OR " + reco.getDbColumnName() + " = '')"
				+ " AND (" + preco.getDbColumnName() + " IS NULL OR " + preco.getDbColumnName() + " = '')";
			
		return sql;
	}
	
	
	
}

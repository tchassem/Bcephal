package com.moriset.bcephal.grid.service;

import java.util.HashMap;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.grid.domain.GrilleRowType;
import com.moriset.bcephal.grid.domain.JoinColumn;
import com.moriset.bcephal.grid.domain.JoinGrid;
import com.moriset.bcephal.service.filters.ISpotService;

public class JoinRecoPublishedQueryBuilder extends JoinPublishedQueryBuilder {

	public JoinRecoPublishedQueryBuilder(JoinFilter filter,
			com.moriset.bcephal.grid.repository.JoinColumnRepository JoinColumnRepository, ISpotService spotService) {
		super(filter, JoinColumnRepository, spotService);
	}
	
	@Override
	public String buildCountQuery() throws Exception {
		parameters = new HashMap<String, Object>();
		String sql = "SELECT 1";
		sql += buildFromPart();
		String wherePart = buildWherePart();
		if (StringUtils.hasText(wherePart)) {
			sql += wherePart;
		}
		String groupBy = buildGroupByPart();
		if (StringUtils.hasText(groupBy)) {
			sql += groupBy;
		}
		
		String recoWherePart = buildRecoWherePart();
		if (StringUtils.hasText(recoWherePart)) {
			sql = "SELECT 1 FROM (" + sql + ") AS items1" + recoWherePart;
		}
		
		return "SELECT COUNT(1) FROM (" + sql + ") AS items";
		
//		String sql = super.buildCountQuery();
//		if (StringUtils.hasText(sql)) {
//			String recoWherePart = buildRecoWherePart();
//			if (StringUtils.hasText(recoWherePart)) {
//				sql += recoWherePart;
//			}
//		}
//		return sql;
	}
	
	@Override
	public String buildQuery() throws Exception {
		parameters = new HashMap<String, Object>();
		String sql = buildSelectPart();
		if (StringUtils.hasText(sql)) {
			sql += buildFromPart();
			String wherePart = buildWherePart();
			if (StringUtils.hasText(wherePart)) {
				sql += wherePart;
			}

			String groupBy = buildGroupByPart();
			if (StringUtils.hasText(groupBy)) {
				sql += groupBy;
			}
			
			String recoWherePart = buildRecoWherePart();
			if (StringUtils.hasText(recoWherePart)) {
				sql = "SELECT * FROM (" + sql + ") AS items" + recoWherePart;
			}

			String orderBy = buildOrderPart();
			if (StringUtils.hasText(orderBy)) {
				sql += orderBy;
			}
		}
		
		return sql;
		
//		String sql = super.buildQuery();
//		if (StringUtils.hasText(sql)) {
//			String recoWherePart = buildRecoWherePart();
//			if (StringUtils.hasText(recoWherePart)) {
//				sql = "SELECT * FROM (" + sql + ") AS items" + recoWherePart;
//			}
//		}
//		return sql;
	}

	
	protected String buildRecoWherePart() throws Exception {
		String coma = " WHERE ";
		String sql = "";
		if(getFilter().getRecoAttributeId() != null && getFilter().getRowType() != null 
				&& getFilter().getRowType() != GrilleRowType.ALL && getFilter().getRowType() != GrilleRowType.ON_HOLD) {
			
			String col = getMainGridCol(getFilter().getRecoAttributeId(), DimensionType.ATTRIBUTE);
			if (getFilter().getRowType().isPositive()) {
				sql += coma + "((" + col + " IS NOT NULL AND " + col + " != '')";
				coma = " AND ";
			}
			else {
				sql += coma + "((" + col + " IS NULL OR " + col + " = '')";
				coma = " AND ";
			}
			
			if(getFilter().getRecoData() != null && getFilter().getRecoData().isAllowPartialReco() 
					&& getFilter().getRecoData().getPartialRecoAttributeId() != null
					&& getFilter().getRecoData().getRemainningMeasureId() != null) {
				
				String partialRecocol = getMainGridCol(getFilter().getRecoData().getPartialRecoAttributeId(), DimensionType.ATTRIBUTE);
				String remainningCol = getMainGridCol(getFilter().getRecoData().getRemainningMeasureId(), DimensionType.MEASURE);
				if (getFilter().getRowType().isPositive()) {
					sql += " AND (" + partialRecocol + " IS NOT NULL AND (" + remainningCol + " = 0))";		
				}
				else {
					sql += " OR (" + partialRecocol + " IS NOT NULL AND (" + remainningCol + " != 0))";		
				}
			}
			sql += ")";
		}	
		
		if(getFilter().getFreezeAttributeId() != null && getFilter().getRowType() != null 
				&& getFilter().getRowType() != GrilleRowType.ALL) {
			String col = getMainGridCol(getFilter().getFreezeAttributeId(), DimensionType.ATTRIBUTE);
			if (getFilter().getRowType() == GrilleRowType.ON_HOLD) {
				sql += coma + "(" + col + " IS NOT NULL AND " + col + " != '')";
				coma = " AND ";
			}
			else {
				sql += coma + "(" + col + " IS NULL OR " + col + " = '')";
				coma = " AND ";
			}
		}			
		return sql;
	}
	
	protected String getMainGridCol(Long columnOid, DimensionType dimensionType) {
		JoinGrid mainGrid = getFilter().getJoin().getMainGrid();	
		JoinColumn column = getFilter().getJoin().getColumn(columnOid, dimensionType, mainGrid.getGridType());
		return column.getDbColAliasName();
	}

}

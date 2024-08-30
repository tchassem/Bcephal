package com.moriset.bcephal.reconciliation.service;

import java.util.HashMap;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.grid.domain.SmartMaterializedGridColumn;
import com.moriset.bcephal.grid.domain.UnionGridColumn;
import com.moriset.bcephal.grid.domain.UnionGridItem;
import com.moriset.bcephal.grid.service.UnionGridFilter;
import com.moriset.bcephal.grid.service.UnionGridQueryBuilder;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UnionSelectAllRowsQueryBuilder extends UnionGridQueryBuilder {
	
	public UnionSelectAllRowsQueryBuilder(UnionGridFilter filter) {
		super(filter, null, null);
	}
	
	
	public String buildIdsQuery() throws Exception {		
		parameters = new HashMap<String, Object>();
		String sql = "";
		String coma = "";
		for (UnionGridItem item : filter.getUnionGrid().getItems()) {
			String itemsql = buildIdsQuery(item);
			if (StringUtils.hasText(itemsql)) {
				sql += coma + itemsql;
				coma = " UNION ";
			}
		}		
		return sql;		
	}
	
	protected String buildIdsQuery(UnionGridItem item) throws Exception {				
		String sql = buildIdsSelectPart(item);
		if (StringUtils.hasText(sql)) {
			sql += buildFromPart(item);
			String wherePart = buildWherePart(item);
			if (StringUtils.hasText(wherePart)) {
				sql += wherePart;
			}

			String groupBy = buildGroupByPart(item);
			if (StringUtils.hasText(groupBy)) {
				sql += groupBy;
			}

			String orderBy = buildOrderPart();
			if (StringUtils.hasText(orderBy)) {
				sql += orderBy;
			}
		}		
		return sql;
	}
		
	
	protected String buildIdsSelectPart(UnionGridItem unionGridItem) {
		String selectPart =  "SELECT CAST(id AS text) || '_" + unionGridItem.getGrid().getId() + "' as id";
		return selectPart;
	}
	
	
	
	@Override
	protected String buildSelectPart(UnionGridItem unionGridItem) {
		String selectPart = "SELECT ";
		String coma = "";
		
		if(getFilter().getRecoData().isAllowPartialReco()) {
			UnionGridColumn column = filter.getUnionGrid().getColumnById(getFilter().getRecoData().getRemainningMeasureId());
			SmartMaterializedGridColumn c = column.getColumnByGridId(unionGridItem.getGrid().getId());
			String col = c != null ? c.getDbColumnName() : "0";
			if (StringUtils.hasText(col)) {
				selectPart += coma + col;
				coma = ", ";
			}
		}
		else if(getFilter().getRecoData().getAmountMeasureId() != null) {
			UnionGridColumn column = filter.getUnionGrid().getColumnById(getFilter().getRecoData().getAmountMeasureId());
			SmartMaterializedGridColumn c = column.getColumnByGridId(unionGridItem.getGrid().getId());
			String col = c != null ? c.getDbColumnName() : "0";
			if (StringUtils.hasText(col)) {
				selectPart += coma + col;
				coma = ", ";
			}						
		}
//		if(getFilter().getColumnFilters() != null){			
//			String col = buildSelectPart(getFilter().getColumnFilters(), getFilter());
//			if (StringUtils.hasText(col)) {						
//				selectPart += coma  + col;
//				coma = ", ";
//			}
//		}
		boolean addDD = getFilter().getDebitCreditAttribute() != null && getFilter().getDebitCreditAttribute().getId() != null;
		if(addDD) {
			String col = getFilter().getDebitCreditAttribute().getUniverseTableColumnName();
			selectPart += coma + col;
			coma = ", ";
		}
		else {
			selectPart += coma + "''";
		}		
		selectPart += coma + "" + unionGridItem.getGrid().getId() + " as gridid, id as id";
		return selectPart;
	}
	
	
	
	
}

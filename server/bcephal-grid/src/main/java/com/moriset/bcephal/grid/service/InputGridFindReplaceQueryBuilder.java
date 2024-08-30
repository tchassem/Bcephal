/**
 * 
 */
package com.moriset.bcephal.grid.service;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.grid.domain.FindReplaceFilter;

/**
 * @author Joseph Wambo
 *
 */
public class InputGridFindReplaceQueryBuilder extends InputGridQueryBuilder {

	FindReplaceFilter criteria;
	
	public InputGridFindReplaceQueryBuilder(FindReplaceFilter criateria) {
		super(criateria.getFilter());
		this.criteria = criateria;
	}
	
	
	public String buildCountQuery() {
		String col = criteria.getColumn().getUniverseTableColumnName();
		String sql = "SELECT COUNT(1) FROM " + UniverseParameters.UNIVERSE_TABLE_NAME;			
		String wherePart = buildWherePart(col);
		if(StringUtils.hasText(wherePart)){
			sql += wherePart;
		}			
		return sql;
	}
	
	
	/**
	 * Build query
	 * @param filter
	 * @return
	 */
	public String buildQuery() {
		String col = criteria.getColumn().getUniverseTableColumnName();
		String sql = "UPDATE " + UniverseParameters.UNIVERSE_TABLE_NAME;
		String setpart = buildSetPart(col);
		if(StringUtils.hasText(setpart)){
			sql += setpart;
		}		
		String wherePart = buildWherePart(col);
		if(StringUtils.hasText(wherePart)){
			sql += wherePart;
		}			
		return sql;
	}
	
	protected String buildSetPart(String col) {
		String sql = " SET " + col + " = :value ";
		if(criteria.isReplaceOnlyValuesCorrespondingToCondition() && criteria.getColumn().isAttribute()) {
			sql = " SET " + col + " = REPLACE ("+ col + ",'" + criteria.getCriteria() + "', :value) ";
		}
		return sql;
	}
	
	protected String buildWherePart(String col) {
		String sql = buildWherePart();
		ColumnFilter filter = new ColumnFilter();
		filter.setDimensionId(criteria.getColumn().getDimensionId());
		filter.setDimensionType(criteria.getColumn().getType());
		filter.setOperation(criteria.getOperation());
		boolean isMeasure = criteria.getColumn().isMeasure();
		filter.setValue(criteria.getCriteria());		
		if(isMeasure) {
			filter.setValue(criteria.getMeasureCriteria() != null ? criteria.getMeasureCriteria().toString() : null);		
		}		
		String subSql = getSqlOperationForFilter(filter, criteria.getFilter());
		if(StringUtils.hasText(subSql)) {
			sql += " AND " + subSql;
		}		
		return sql;
	}

}

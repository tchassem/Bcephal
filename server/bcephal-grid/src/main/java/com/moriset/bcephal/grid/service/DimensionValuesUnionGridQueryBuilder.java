/**
 * 
 */
package com.moriset.bcephal.grid.service;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.grid.domain.SmartMaterializedGridColumn;
import com.moriset.bcephal.grid.domain.UnionGridColumn;
import com.moriset.bcephal.grid.domain.UnionGridItem;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.service.QueryBuilder;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author MORISET-004
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class DimensionValuesUnionGridQueryBuilder extends UnionGridQueryBuilder {

	private DimensionDataFilter dimensionFilter;
	
	protected ParameterRepository parameterRepository;
	
	public DimensionValuesUnionGridQueryBuilder(UnionGridFilter filter, DimensionDataFilter dimensionFilter) {
		super(filter, null, null);
		this.dimensionFilter = dimensionFilter;
	}
		
	@Override
	public String buildCountQuery() throws Exception {
		String sql = "SELECT COUNT(1) FROM (" + buildQuery() + ") AS A";		
		return sql;
	}
	
	@Override
	protected String buildSelectPart(UnionGridItem unionGridItem) {
		String selectPart = "SELECT DISTINCT ";
		UnionGridColumn column = filter.getUnionGrid().getColumnById(dimensionFilter.getDimensionId());		
		SmartMaterializedGridColumn c = column != null ? column.getColumnByGridId(unionGridItem.getGrid().getId()) : null;
		String col = c != null ? c.getDbColumnName() : "null";		
		selectPart += col + " AS " + column.getDbColAliasName();
		return selectPart;
	}
			
	@Override
	protected String buildOrderPart() {
		String sql = "";
		UnionGridColumn column = filter.getUnionGrid().getColumnById(dimensionFilter.getDimensionId());		
		if (column != null) {
			String col = column.getDbColAliasName();
			sql = col + " ASC";
		}
		if (!sql.isEmpty()){
			sql = " ORDER BY " + sql;
		}
		return sql;
	}
	
	@Override
	protected String buildWherePart(UnionGridItem unionGridItem) throws Exception {	
		String wherePart = super.buildWherePart(unionGridItem);
		String coma = StringUtils.hasText(wherePart) ? " AND " : " WHERE ";		
		if(dimensionFilter.getAdminFilter() != null) {
			String userFilterSql = new QueryBuilder().buildUniverseFilterWherePart(dimensionFilter.getAdminFilter());
			if (StringUtils.hasText(userFilterSql)) {
				wherePart += coma + "(" + userFilterSql + ")";
				coma = " AND ";	
			}
		}
		if(dimensionFilter.getUserFilter() != null) {
			String userFilterSql = new QueryBuilder().buildUniverseFilterWherePart(dimensionFilter.getUserFilter());
			if (StringUtils.hasText(userFilterSql)) {
				wherePart += coma + "(" + userFilterSql + ")";
				coma = " AND ";	
			}
		}	
		return wherePart;
	}
		
}

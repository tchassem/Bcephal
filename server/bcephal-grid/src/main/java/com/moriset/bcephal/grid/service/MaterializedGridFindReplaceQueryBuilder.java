package com.moriset.bcephal.grid.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.grid.domain.MaterializedGridFindReplaceFilter;

public class MaterializedGridFindReplaceQueryBuilder extends MaterializedGridQueryBuilder {

	MaterializedGridFindReplaceFilter criteria;
	
	public MaterializedGridFindReplaceQueryBuilder(MaterializedGridFindReplaceFilter criteria) {
		super(criteria.getFilter());
		this.criteria = criteria;
	}
	
	@Override
	public String buildCountQuery() {
		String table = criteria.getFilter().getGrid().getMaterializationTableName();
		String col = criteria.getColumn().getDbColumnName();
		String sql = "SELECT COUNT(1) FROM " + table;			
		String wherePart = buildWherePart(col);
		if(StringUtils.hasText(wherePart)){
			sql += wherePart;
		}			
		return sql;
	}
	
	@Override
	public String buildQuery() {
		String table = criteria.getFilter().getGrid().getMaterializationTableName();
		String col = criteria.getColumn().getDbColumnName();
		String sql = "UPDATE " + table;
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
		if(criteria.isReplaceOnlyValuesCorrespondingToCondition()) {
			sql = " SET " + col + " = REPLACE ("+ col + ",'" + criteria.getCriteria() + "', :value) ";
		}
		return sql;
	}
	
	protected String buildWherePart(String col) {
		String sql = buildWherePart();
		String coma = StringUtils.hasText(sql) ? " AND " : " WHERE ";
		ColumnFilter filter = new ColumnFilter();
		filter.setDimensionId(criteria.getColumn().getId());
		filter.setDimensionType(criteria.getColumn().getType());
		filter.setOperation(criteria.getOperation());
		filter.setValue(criteria.getCriteria());			
		if(criteria.getColumn().isMeasure()) {
			filter.setValue(criteria.getMeasureCriteria() != null ? criteria.getMeasureCriteria().toString() : null);		
		}		
		String subSql = getSqlOperationForColumn(filter, criteria.getFilter());
		sql += coma + subSql;
		return sql;
	}
	
	public Date buildPeriodVal(String value){
		Date date = null;
		SimpleDateFormat[] Formats = new SimpleDateFormat[]{				 
				new SimpleDateFormat("yyyy/MM/dd"), new SimpleDateFormat("yyyy/MM/dd hh:mm:ss"),
				new SimpleDateFormat("yyyy-MM-dd"), new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"),				
				new SimpleDateFormat("dd-MM-yyyy"), new SimpleDateFormat("dd-MM-yyyy hh:mm:ss"),
				new SimpleDateFormat("dd/MM/yyyy"), new SimpleDateFormat("dd/MM/yyyy hh:mm:ss")};
		int i = Formats.length;
		while (date == null && i > 0) {
			SimpleDateFormat f = Formats[i-1];
			try{				
				date = f.parse(value);
			}
			catch(Exception e){}
			i--;
		}
		
		return date;
	}
	
	
}


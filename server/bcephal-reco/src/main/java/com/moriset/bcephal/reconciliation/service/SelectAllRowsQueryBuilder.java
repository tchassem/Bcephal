package com.moriset.bcephal.reconciliation.service;

import java.util.HashMap;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.domain.filters.MeasureFunctions;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.service.ReconciliationGridQueryBuilder;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SelectAllRowsQueryBuilder extends ReconciliationGridQueryBuilder {
	
	public SelectAllRowsQueryBuilder(GrilleDataFilter filter) {
		super(filter);
	}
	
	@Override
	protected String buildSelectPart() {
		String selectPart = "SELECT distinct ";
		boolean isReport = isReport();
		if(!isReport) {
			selectPart = "SELECT ";
		}
		String coma = "";
		if(getFilter().getRecoData().isAllowPartialReco()) {
			Measure measure = new Measure(getFilter().getRecoData().getRemainningMeasureId(), "");			
			String col = measure.getUniverseTableColumnName();
			this.measures.put(measure.getId(), measure);
			selectPart += coma + col;
			coma = ", ";
		}
		else if(getFilter().getRecoData().getAmountMeasureId() != null) {
			Measure measure = new Measure(getFilter().getRecoData().getAmountMeasureId(), "");			
			String col = measure.getUniverseTableColumnName();
			this.measures.put(measure.getId(), measure);
			selectPart += coma + col;
			coma = ", ";
		}
		if(getFilter().getColumnFilters() != null){			
			String col = buildSelectPart(getFilter().getColumnFilters(), getFilter());
			if (StringUtils.hasText(col)) {						
				selectPart += coma  + col;
				coma = ", ";
			}
		}
		boolean addDD = getFilter().getDebitCreditAttribute() != null && getFilter().getDebitCreditAttribute().getId() != null 
				&& this.attributes.get(getFilter().getDebitCreditAttribute().getId()) == null;
		if(addDD) {
			String col = getFilter().getDebitCreditAttribute().getUniverseTableColumnName();
			this.attributes.put(getFilter().getDebitCreditAttribute().getId(), getFilter().getDebitCreditAttribute());
			selectPart += coma + col;
			coma = ", ";
		}
		else {
			selectPart += coma + "''";
		}
		return selectPart;
	}
	
	
	protected String buildSelectPart(ColumnFilter columnFilter, GrilleDataFilter grilleFilter){
		String sql = "";
		String coma = "";
		if(columnFilter != null){
			if(columnFilter.isGrouped()) {
				for(ColumnFilter item : columnFilter.getItems()) {
					String query = buildSelectPart(item, grilleFilter);
					if (StringUtils.hasText(query)) {						
						sql += coma  + query;
						coma = ", ";
					}
				}
			}
			else {
				sql = buildSelectCol(columnFilter, grilleFilter); 
			}
		}
		
		return sql;
	}
	
	protected String buildSelectCol(ColumnFilter columnFilter, GrilleDataFilter grilleFilter){
		GrilleColumn column = new GrilleColumn();
		column.setType(columnFilter.getDimensionType());
		column.setDimensionId(columnFilter.getDimensionId());
		String col = column.getUniverseTableColumnName();
		boolean isMeasure = column.isMeasure();		
		if(getFilter().getGrid().isReport() && isMeasure) {
			String functions = MeasureFunctions.SUM.code;
			col = col + "_" + functions;
		}
		
		if(column.isAttribute()) {
			attributes.put(column.getDimensionId(), new Attribute(column.getDimensionId()));
		}
		else if(column.isPeriod()) {
			if(periods.containsKey(column.getDimensionId())) {
				return null;
			}
			periods.put(column.getDimensionId(), new Period(column.getDimensionId()));
		}
		else if(column.isMeasure()) {
			measures.put(column.getDimensionId(), new Measure(column.getDimensionId()));
		}
		
		return col;
	}
	
	
	public String buildIdsQuery() {
		addOidOfEachRow = !getFilter().getGrid().isConsolidated();
		attributes = new HashMap<>();
		measures = new HashMap<>();
		periods = new HashMap<>();
		String table = "A";
		String sql = "SELECT id as id";		
		String where = buildWherePart();
		String whereOperationForFilter = buildWherePartOperationForFilter();
		String from = buildFromPart(table);	
				
		if(StringUtils.hasText(whereOperationForFilter)) {
			whereOperationForFilter = whereOperationForFilter.replace(" WHERE ", "");
			//sql = "SELECT id FROM ( "+ sql + " ) as AAA "+ whereOperationForFilter;
			if(StringUtils.hasText(where)) {
				where += " AND " + whereOperationForFilter;
			}
			else {
				where = whereOperationForFilter;
			}
		}	
		sql += from + " " + where;
		return sql;
	}
	
	@Override
	protected boolean isContainsId(String sql) {
		return true;
	}
	
}

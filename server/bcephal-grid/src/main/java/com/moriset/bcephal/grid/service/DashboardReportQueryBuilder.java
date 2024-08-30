/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.util.List;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.CalculatedMeasureExcludeFilter;
import com.moriset.bcephal.domain.dimension.CalculatedMeasureItem;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.MeasureFunctions;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;

/**
 * @author MORISET-004
 *
 */
public class DashboardReportQueryBuilder extends ReportGridQueryBuilder {
	
	public DashboardReportQueryBuilder(GrilleDataFilter filter) {
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
		boolean isEmptyGrid = true;
		this.measureColumnCount = 0;
		this.measureColumn = null;
		String tableA = "A";
		String tableB = "B";
//		String from = buildFromPart(tableA);	
		String fromB = buildFromPart(tableB);	
		
		for (GrilleColumn column : getFilter().getGrid().getColumns()) {			
			if(column.isCalculatedMeasure()) {					
				for(CalculatedMeasureItem item : column.getCalculatedMeasure().getItems()) {					
					if(item.hasActiveExcludeFilter() && isFilterContainsExcludedDimensions(item.getExcludeFilters())) {
						String where = buildWherePart(item.getExcludeFilters(), tableA, tableB);
						String colWhere = buildWherePartForColumns(item.getExcludeFilters(), tableA, tableB);
						item.setFromPart(fromB);	
						String sepa = StringUtils.hasText(where) ? " AND " : " WHERE ";
						where = StringUtils.hasText(where) ? where : "";
						if(StringUtils.hasText(colWhere)) {
							where += sepa + colWhere;
						}
						item.setWherePart(where);
					}
				}
			}
			
			String col = column.getUniverseTableColumnName();
			if (col == null) continue;
			
			if (isReport && column.isMeasure()) {				
				String measurePart = buildMeasureColumSelectPart(column, col);
				selectPart += coma + measurePart;
				this.measureColumnCount++;
				this.measureColumn = column;
				Measure persistent = (Measure)column.getDimension();
				this.measures.put(persistent.getId(), persistent);
			}			
			else if (column.isPeriod()) {
				Period persistent = (Period)column.getDimension();
				this.periods.put(persistent.getId(), persistent);
				String periodPart = buildPeriodColumSelectPart(column, col);
				selectPart += coma + periodPart;
				
			}
			else {
				if (column.isAttribute()) {
					Attribute persistent = (Attribute)column.getDimension();
					this.attributes.put(persistent.getId(), persistent);
				}
				else if (column.isMeasure()) {
					Measure persistent = (Measure)column.getDimension();
					this.measures.put(persistent.getId(), persistent);
				}
				
				selectPart += coma + col;
			}
			coma = ", ";
			if(isEmptyGrid) {
				isEmptyGrid = false;
			}
		}
		if(isEmptyGrid) {
			return null;
		}
		if(isReport) {
			if(addOidOfEachRow) {
				selectPart += coma + UniverseParameters.ID;
			}
			else{
				selectPart += coma + "1";
			}
		}
		else {
			selectPart += coma + UniverseParameters.ID;
		}
		return selectPart;
	}
	
	protected boolean isFilterContainsExcludedDimensions(List<CalculatedMeasureExcludeFilter> excludeFilters) {	
		if (getFilter().getGrid() != null && getFilter().getGrid().getUserFilter() != null && !ignoreUserFilter) {
			boolean response = getFilter().getGrid().getUserFilter().containsExcludedDimensions(excludeFilters);
			if(response) {
				return true;
			}
		}
		if (getFilter().getGrid() != null && getFilter().getGrid().getAdminFilter() != null) {
			boolean response = getFilter().getGrid().getAdminFilter().containsExcludedDimensions(excludeFilters);
			if(response) {
				return true;
			}		
		}
		
		if (getFilter().getGrid() != null && getFilter().getGrid().getGridUserFilter() != null) {
			boolean response = getFilter().getGrid().getGridUserFilter().containsExcludedDimensions(excludeFilters);
			if(response) {
				return true;
			}	
		}
		if (getFilter().getGrid() != null && getFilter().getGrid().getGridAdminFilter() != null) {
			boolean response = getFilter().getGrid().getGridAdminFilter().containsExcludedDimensions(excludeFilters);
			if(response) {
				return true;
			}
		}		
		if (getFilter().getFilter() != null) {
			boolean response = getFilter().getFilter().containsExcludedDimensions(excludeFilters);
			if(response) {
				return true;
			}
		}
		
		for (GrilleColumn column : getFilter().getGrid().getColumns()) {
			if(!column.isCalculatedMeasure()) {
				for(CalculatedMeasureExcludeFilter filter : excludeFilters) {
					if(filter.getType() == column.getType()  && filter.getDimensionId() != null && filter.getDimensionId().equals(column.getDimensionId())) {
						return true;
					}
				}
			}			
		}
		
		if (getFilter().getGrid() != null && getFilter().getGrid().getUserFilter() != null && !ignoreUserFilter) {
			boolean response = getFilter().getGrid().getUserFilter().containsExcludedDimensions(excludeFilters);
			if(response) {
				return true;
			}
		}
		
		return false;
	}
	
	
	protected String buildWherePart(List<CalculatedMeasureExcludeFilter> excludeFilters, String tableA, String tableB) {	
		boolean checkIsReady = (getFilter().getGrid() != null && getFilter().getGrid().getDataSourceType() != null && !getFilter().getGrid().getDataSourceType().isMaterializedGrid())
				|| (getFilter().getDataSourceType() != null && !getFilter().getDataSourceType().isMaterializedGrid());
		
		String sql = "";
		String where = " WHERE ";
			
		if(checkIsReady) {
			sql += where + UniverseParameters.ISREADY + " = true";
			where = " AND ";	
		}
		
		String source = buildWhereSourcePart();
		if (StringUtils.hasText(source)) {
			sql += where + source;
			where = " AND ";
		}
				
		boolean isReport = isReport();
		if(isReport && this.measureColumnCount == 1 && this.measureColumn != null) {
			String functions = StringUtils.hasText(this.measureColumn.getDimensionFunction()) ? this.measureColumn.getDimensionFunction() : MeasureFunctions.SUM.code;
			@SuppressWarnings("unused")
			String name = this.measureColumn.getUniverseTableColumnName().concat("_").concat(functions);
			name = this.measureColumn.getUniverseTableColumnName();
		}
		
		if(getFilter().getGrid() == null || !getFilter().getGrid().isReport()) {			
			String status = buildWhereStatusPart();
			if (StringUtils.hasText(status)) {
				sql += where + status;
				where = " AND ";
			}			
		}

		if (getFilter().getGrid() != null && getFilter().getGrid().getUserFilter() != null && !ignoreUserFilter) {
			String userFilterSql = buildUniverseFilterWherePart(getFilter().getGrid().getUserFilter(), excludeFilters);
			if (StringUtils.hasText(userFilterSql)) {
				sql += where + "(" + userFilterSql + ")";
				where = " AND ";
			}
		}
		if (getFilter().getGrid() != null && getFilter().getGrid().getAdminFilter() != null) {
			String adminFilterSql = buildUniverseFilterWherePart(getFilter().getGrid().getAdminFilter(), excludeFilters);
			if (StringUtils.hasText(adminFilterSql)) {
				sql += where + "(" + adminFilterSql + ")";
				where = " AND ";
			}
		}
		
		if (getFilter().getGrid() != null && getFilter().getGrid().getGridUserFilter() != null) {
			String userFilterSql = buildUniverseFilterWherePart(getFilter().getGrid().getGridUserFilter(), excludeFilters);
			if (StringUtils.hasText(userFilterSql)) {
				sql += where + "(" + userFilterSql + ")";
				where = " AND ";
			}
		}
		if (getFilter().getGrid() != null && getFilter().getGrid().getGridAdminFilter() != null) {
			String adminFilterSql = buildUniverseFilterWherePart(getFilter().getGrid().getGridAdminFilter(), excludeFilters);
			if (StringUtils.hasText(adminFilterSql)) {
				sql += where + "(" + adminFilterSql + ")";
				where = " AND ";
			}
		}
		
		if (getFilter().getFilter() != null) {
			String filterSql = buildUniverseFilterWherePart(getFilter().getFilter(), excludeFilters);
			if (StringUtils.hasText(filterSql)) {
				sql += where + "(" + filterSql + ")";
				where = " AND ";
			}
		}
		
		return sql;
	}
	
	protected String buildWherePartForColumns(List<CalculatedMeasureExcludeFilter> excludeFilters, String tableA, String tableB) {	
		String sql = "";	
		String where = "";
		for (GrilleColumn column : getFilter().getGrid().getColumns()) {
			if(!column.isCalculatedMeasure() && !column.isMeasure()) {
				if(!isExcluded(excludeFilters, column)) {
					String col = column.getUniverseTableColumnName();
					if (col == null) continue;
					sql += where + tableA + "." + col + " = " + tableB + "." +  col;
					where = " AND ";
				}				
			}			
		}	
		
		return sql;
	}
	
	private boolean isExcluded(List<CalculatedMeasureExcludeFilter> excludeFilters, GrilleColumn column) {
		for(CalculatedMeasureExcludeFilter filter : excludeFilters) {
			if(filter.getType() == column.getType()  && filter.getDimensionId() != null && filter.getDimensionId().equals(column.getDimensionId())) {
				return true;
			}
		}
		return false;
	}
	
	

}

package com.moriset.bcephal.grid.service;

import java.util.List;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.dimension.CalculatedMeasureExcludeFilter;
import com.moriset.bcephal.domain.dimension.CalculatedMeasureItem;
import com.moriset.bcephal.domain.filters.MeasureFunctions;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.domain.MaterializedGridDataFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DashboardReportMaterializedGridQueryBuilder extends MaterializedGridQueryBuilder {

			
	public DashboardReportMaterializedGridQueryBuilder(MaterializedGridDataFilter filter) {
		super(filter);
	}	

	@Override
	protected String buildSelectPart() {
		String selectPart = "SELECT ";
		String coma = "";
		String tableA = "A";
		String tableB = "B";
//		String from = buildFromPart() + " " + tableA;
		String fromB = buildFromPart() + " " + tableB;
			
		for (MaterializedGridColumn column : getFilter().getGrid().getColumns()) {
			String col = null;			
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
			col = column.getId() != null && column.isPublished() ? column.getDbColumnName() : "null as " + column.getDbColumnName();
			
			if (!StringUtils.hasText(col)) {
				continue;
			}
			if(column.isMeasure()) {
				String functions = column.getDimensionFunction();
				if(isConsolidate() && !StringUtils.hasText(functions)) {
					col = "SUM(" + col + ")";
				}
				else if(StringUtils.hasText(functions)){
					functions = functions.toUpperCase().equals(MeasureFunctions.AVERAGE.name().toUpperCase()) ? MeasureFunctions.AVERAGE.code : functions;
					boolean isCount = functions.toUpperCase().equals(MeasureFunctions.COUNT.code.toUpperCase());
					if(isCount){
						col = !StringUtils.hasText(col) ? "1" : col;
						col = functions.concat("(").concat(col).concat(") AS ").concat(col);
					}
					else {
						col = functions.concat("(").concat(col).concat(") AS ").concat(col);
					}
				}
			}
			else if (column.isPeriod() && column.isPublished()) {
				col = buildPeriodColumSelectPart(column, col);
			}
			
			selectPart += coma + col;
			coma = ", ";
		}
//		if(isConsolidate()) {
//			selectPart += coma + '1';
//		}
//		else {
//			selectPart += coma + "id";
//		}
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
		
		for (MaterializedGridColumn column : getFilter().getGrid().getColumns()) {
			if(!column.isCalculatedMeasure()) {
				for(CalculatedMeasureExcludeFilter filter : excludeFilters) {
					if(filter.getType() == column.getType()  && filter.getDimensionId() != null && filter.getDimensionId().equals(column.getDimensionId())) {
						return true;
					}
				}
			}			
		}
		
		return false;
	}
	
	protected String buildWherePart(List<CalculatedMeasureExcludeFilter> excludeFilters, String tableA, String tableB) {
		String wherePart = "";
		String coma = " WHERE ";
			
		if (getFilter().getGrid() != null && getFilter().getGrid().getUserFilter() != null && !ignoreUserFilter) {
			String userFilterSql = buildUniverseFilterWherePart(getFilter().getGrid().getUserFilter(), excludeFilters);
			if (StringUtils.hasText(userFilterSql)) {
				wherePart += coma + "(" + userFilterSql + ")";
				coma = " AND ";
			}
		}
		if (getFilter().getGrid() != null && getFilter().getGrid().getAdminFilter() != null) {
			String adminFilterSql = buildUniverseFilterWherePart(getFilter().getGrid().getAdminFilter(), excludeFilters);
			if (StringUtils.hasText(adminFilterSql)) {
				wherePart += coma + "(" + adminFilterSql + ")";
				coma = " AND ";
			}
		}
		
		if (getFilter().getGrid() != null && getFilter().getGrid().getGridUserFilter() != null) {
			String userFilterSql = buildUniverseFilterWherePart(getFilter().getGrid().getGridUserFilter(), excludeFilters);
			if (StringUtils.hasText(userFilterSql)) {
				wherePart += coma + "(" + userFilterSql + ")";
				coma = " AND ";
			}
		}
		if (getFilter().getGrid() != null && getFilter().getGrid().getGridAdminFilter() != null) {
			String adminFilterSql = buildUniverseFilterWherePart(getFilter().getGrid().getGridAdminFilter(), excludeFilters);
			if (StringUtils.hasText(adminFilterSql)) {
				wherePart += coma + "(" + adminFilterSql + ")";
				coma = " AND ";
			}
		}
		
		if (getFilter().getFilter() != null) {
			String filterSql = buildUniverseFilterWherePart(getFilter().getFilter(), excludeFilters);
			if (StringUtils.hasText(filterSql)) {
				wherePart += coma + "(" + filterSql + ")";
				coma = " AND ";
			}
		}		
				
		return wherePart;
	}
	
	protected String buildWherePartForColumns(List<CalculatedMeasureExcludeFilter> excludeFilters, String tableA, String tableB) {	
		String sql = "";	
		String where = "";
		for (MaterializedGridColumn column : getFilter().getGrid().getColumns()) {
			if(!column.isCalculatedMeasure() && !column.isMeasure()) {
				if(!isExcluded(excludeFilters, column)) {
					String col = column.getDbColumnName();
					if (col == null) continue;
					sql += where + tableA + "." + col + " = " + tableB + "." +  col;
					where = " AND ";
				}				
			}			
		}	
		
		return sql;
	}
	
	private boolean isExcluded(List<CalculatedMeasureExcludeFilter> excludeFilters, MaterializedGridColumn column) {
		for(CalculatedMeasureExcludeFilter filter : excludeFilters) {
			if(filter.getType() == column.getType()  && filter.getDimensionId() != null && filter.getDimensionId().equals(column.getDimensionId())) {
				return true;
			}
		}
		return false;
	}
		

}

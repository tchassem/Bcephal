/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.CalculatedMeasureExcludeFilter;
import com.moriset.bcephal.domain.dimension.CalculatedMeasureItem;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.MeasureFunctions;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;

public class DashboardReportQueryBuilderV2 extends ReportGridQueryBuilder {
	
	String baseTable = "BASE";
	String baseVar = baseTable + ".";
	
	public DashboardReportQueryBuilderV2(GrilleDataFilter filter) {
		super(filter);
	}
	
	
	@Override
	public String buildQuery() {	
		addOidOfEachRow = !getFilter().getGrid().isConsolidated();
		attributes = new HashMap<>();
		measures = new HashMap<>();
		periods = new HashMap<>();
		
		String sql = "";
		String coma = "WITH ";
		
		String baseSql = buildBaseTable();
		if(StringUtils.hasText(baseSql)) {
			sql = coma + baseTable + " AS (" + baseSql + ")";
			coma = ", ";
		}
				
		String calSql = buildCalculatedTables();
		if(StringUtils.hasText(calSql)) {
			sql += coma + calSql;
			coma = ", ";
		}
		
		String selectSql = buildSelectPart();
		sql += " " + selectSql;		
		String whereOperationForFilter = buildWherePartOperationForFilter();
		String from = buildGlobalFrom();		
		String group = buildGroupByPart(getFilter().getGrid().getColumns(), baseTable);
		
		if(org.springframework.util.StringUtils.hasText(whereOperationForFilter)) {
			sql = buildCustomForReport(sql + from  + " " + group, whereOperationForFilter);
		}
		else {				
			sql +=", row_number() OVER() as id";
			sql += from + " " + group;
			String order = buildOrderPart(baseTable);
			if (order.isEmpty() && isContainsId(sql)){
				order = " ORDER BY ID DESC";
			}
			else {
				sql = sql + " " + order;
			}
		}	
		return sql;
	}
	
	protected String buildGlobalFrom() {
		String sql = " FROM " + baseTable;
		String coma = "";
		for (GrilleColumn column : getFilter().getGrid().getColumns()) {
			if(column.isCalculatedMeasure()) {					
				for(CalculatedMeasureItem item : column.getCalculatedMeasure().getItems()) {					
					if(item.hasActiveExcludeFilter() && isFilterContainsExcludedDimensions(item.getExcludeFilters())) {
						String join = buildCalculatedTableJoin(column, item);
						if(StringUtils.hasText(join)) {
							sql += coma + join;
							coma = ", ";
						}
					}
					else if(item.getType() == DimensionType.MEASURE && item.getMeasureId() != null){
						
					}
				}
			}
		}		
		
		return sql;
	}
	
	private String buildCalculatedTableJoin(GrilleColumn column, CalculatedMeasureItem item) {
		String table = item.getTableName();
		String colOn = buildWherePartForColumns(item.getExcludeFilters(), baseTable, table);
		String sql = " JOIN " + table;
		if(StringUtils.hasText(colOn)) {
			sql += " ON " + colOn;
		}
		return sql;
	}

	
	@Override
	protected String buildGroupByPart(List<GrilleColumn> columns, String table) {		
		String groupBy = "";
		String coma = "";
		String var = StringUtils.hasText(table) ? table + "." : "";
		for(GrilleColumn column : columns){
			if (!column.isMeasure()) {
				if(column.isCalculatedMeasure()) {
					if(column.getCalculatedMeasure() != null) {
						Set<String> cols = column.getCalculatedMeasure().getMeasureNamesForGroupBy();
						for(String col : cols) {
							groupBy += coma + col;
							coma = ", ";
						}
					}
				}
				else {
					boolean hasAlias = org.springframework.util.StringUtils.hasText(column.getAlias());
					String col = hasAlias ? var + column.getAlias() : var + column.getUniverseTableColumnName();
					groupBy += coma + col;
					coma = ", ";
				}
			}
		}
		if(addOidOfEachRow || !getFilter().getGrid().isConsolidated()) {
			groupBy += coma + var + UniverseParameters.ID;
			coma = ", ";
		}
		if(!groupBy.isEmpty()) {
			groupBy = " GROUP BY " + groupBy;
		}		
		return groupBy;
	}
	
	
	private String buildBaseTable() {
		String sql = buildSelectPart("", false, null, null);
		String where = buildWherePart();
		String whereOperationForFilter = buildWherePartOperationForFilter();
		String table = "";
		String from = buildFromPart(table);		
		String group = buildGroupByPart(getFilter().getGrid().getColumns(), table);
		if(StringUtils.hasText(whereOperationForFilter)) {
			sql = buildCustomForReport(sql + from  + where + " " + group, whereOperationForFilter);
		}
		else {
			sql += from + " " + where + " " + group;
		}		
		return sql;
	}


	private String buildCalculatedTables() {
		String sqls = "";
		String coma = "";
		for (GrilleColumn column : getFilter().getGrid().getColumns()) {
			if(column.isCalculatedMeasure()) {					
				for(CalculatedMeasureItem item : column.getCalculatedMeasure().getItems()) {					
					if(item.hasActiveExcludeFilter() && isFilterContainsExcludedDimensions(item.getExcludeFilters())) {
						String sql = buildCalculatedTable(column, item);
						if(StringUtils.hasText(sql)) {							
							sqls += coma + item.getTableName() + " AS (" + sql + ")";
							coma = ", ";
						}
					}
					else if(item.getType() == DimensionType.MEASURE && item.getMeasureId() != null){
						
					}
				}
			}
		}	
		return sqls;
	}
	
	
	private String buildCalculatedTable(GrilleColumn column, CalculatedMeasureItem item) {
		item.setTableName("ITEM" + item.getId());		
		String sql = buildSelectPart("", true, column, item);		
		String where = buildWherePart(item.getExcludeFilters(), "", "");
		String whereOperationForFilter = buildWherePartOperationForFilter();
		String table = "";
		String from = buildFromPart(table);		
		String group = buildGroupByPart(getFilter().getGrid().getColumns(), table);
		if(StringUtils.hasText(whereOperationForFilter)) {
			sql = buildCustomForReport(sql + from  + where + " " + group, whereOperationForFilter);
		}
		else {
			sql += from + " " + where + " " + group;
		}		
		return sql;
	}


	protected String buildSelectPart(String varName, boolean forCalculate, GrilleColumn sourceColumn, CalculatedMeasureItem sourceItem) {
		String selectPart = "SELECT ";		
		String coma = "";
		boolean isEmptyGrid = true;
		this.measureColumnCount = 0;
		this.measureColumn = null;
		
		for (GrilleColumn column : getFilter().getGrid().getColumns()) {
			String col = null;
			if(column.isCalculatedMeasure()) {	
				for(CalculatedMeasureItem item : column.getCalculatedMeasure().getItems()) {					
					if(item.hasActiveExcludeFilter() && isFilterContainsExcludedDimensions(item.getExcludeFilters())) {
						if(forCalculate && item.getId().equals(sourceItem.getId())) {
							col = item.buildDbColumnName(column.getDataSourceType());
							selectPart += coma + col;
							
							coma = ", ";
							if(isEmptyGrid) {
								isEmptyGrid = false;
							}
						}
						
					}
					else if(item.getType() == DimensionType.MEASURE && item.getMeasureId() != null){
						if(forCalculate) continue;						
						col = item.buildDbColumnName(column.getDataSourceType());
						selectPart += coma + col;
						
						coma = ", ";
						if(isEmptyGrid) {
							isEmptyGrid = false;
						}
					}
				}
			}
			else {
				col = column.getUniverseTableColumnName();
				if (col == null) continue;
				column.setAlias(col);
				
				if (column.isMeasure()) {		
					if(forCalculate) continue;
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
				else if (column.isAttribute()) {
					Attribute persistent = (Attribute)column.getDimension();
					this.attributes.put(persistent.getId(), persistent);
					selectPart += coma + col;
				}	
				coma = ", ";
				if(isEmptyGrid) {
					isEmptyGrid = false;
				}
			}			
		}
		if(isEmptyGrid) {
			return null;
		}
		return selectPart;
	}
	
	
	
	


	@Override
	protected String buildSelectPart() {
		String selectPart = "SELECT ";
		String coma = "";
		boolean isEmptyGrid = true;
		this.measureColumnCount = 0;
		this.measureColumn = null;
		
		for (GrilleColumn column : getFilter().getGrid().getColumns()) {			
			if(column.isCalculatedMeasure()) {	
				if(column.getCalculatedMeasure() != null) {
					String col = "";
					for(CalculatedMeasureItem item : column.getCalculatedMeasure().getItems()) {							
						String var = item.hasActiveExcludeFilter() && isFilterContainsExcludedDimensions(item.getExcludeFilters()) ?
								item.getTableName() + "." : baseTable + ".";
						String itemSql = null;
						String open = !StringUtils.hasText(item.getOpeningBracket()) ? "" : item.getOpeningBracket();
						String close = !StringUtils.hasText(item.getClosingBracket()) ? "" : item.getClosingBracket();
						itemSql = "SUM(" + var + item.getAlias() + ")";
						itemSql = open.concat(itemSql).concat(close);
						
						if (StringUtils.hasText(itemSql)) {
							String sign = item.getArithmeticOperator();
							boolean isFirst = item.getPosition() == 0;
							sign = StringUtils.hasText(sign) ? " " + sign + " " : " + ";
							sign = isFirst ? "" : sign;
							col = col.concat(sign).concat(itemSql);
						}						
					}
					
					selectPart += coma + col;
					coma = ", ";
					if(isEmptyGrid) {
						isEmptyGrid = false;
					}
				}
			}
			else {			
				String col = column.getAlias();
				if (col == null) col = column.getUniverseTableColumnName();
				if (col == null) continue;
				
				if (column.isMeasure()) {				
					selectPart += coma + "SUM(" + baseVar + col + ")";
				}			
				else {
					selectPart += coma + baseVar + col;
					
				}
				coma = ", ";
				if(isEmptyGrid) {
					isEmptyGrid = false;
				}
			}
		}
		if(isEmptyGrid) {
			return null;
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

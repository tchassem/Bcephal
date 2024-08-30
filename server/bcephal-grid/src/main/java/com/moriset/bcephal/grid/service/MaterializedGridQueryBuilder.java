package com.moriset.bcephal.grid.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.dimension.CalculatedMeasure;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.domain.filters.GridFilterOperator;
import com.moriset.bcephal.domain.filters.MeasureFunctions;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.domain.MaterializedGridDataFilter;
import com.moriset.bcephal.repository.dimension.CalculatedMeasureRepository;
import com.moriset.bcephal.service.QueryBuilder;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MaterializedGridQueryBuilder extends QueryBuilder {

	private MaterializedGridDataFilter filter;
	
	private boolean consolidate;
	boolean ignoreUserFilter;
	
	protected CalculatedMeasureRepository calculatedMeasureRepository;
		
	public MaterializedGridQueryBuilder(MaterializedGridDataFilter filter) {
		super();
		this.filter = filter;
		ignoreUserFilter = false;
	}	

	@Override
	public String buildCountQuery() {
		String sql = "SELECT COUNT(1)";
		sql += buildFromPart();
		String wherePart = buildWherePart();
		if(StringUtils.hasText(wherePart)){
			sql += wherePart;
		}
		if(consolidate) {
			String groupBy = buildGroupByPart();			
			if(StringUtils.hasText(groupBy)){
				sql += groupBy;
			}	
		}
		return sql;
	}

	@Override
	public String buildQuery() {
		String sql = buildSelectPart();
		if(StringUtils.hasText(sql)){
			sql += buildFromPart();
			String wherePart = buildWherePart();
			if(StringUtils.hasText(wherePart)){
				sql += wherePart;
			}	
			if(consolidate) {
				String groupBy = buildGroupByPart();			
				if(StringUtils.hasText(groupBy)){
					sql += groupBy;
				}	
			}
			String orderBy = buildOrderPart();			
			if(StringUtils.hasText(orderBy)){
				sql += orderBy;
			}			
		}
		return sql;
	}
	
	public String buildDeleteQuery() {
		String sql = "DELETE ";
		sql += buildFromPart();
		String wherePart = buildWherePart();
		if(StringUtils.hasText(wherePart)){
			sql += wherePart;
		}
		return sql;
	}
	
	public String buildColumnCountDetailsQuery() {	
		if(this.filter.getColumns().size() == 0) {
			return null;
		}
		MaterializedGridColumn column = this.filter.getColumns().get(0);
		boolean isMeasure = column.isMeasure();		
		String col = getColumnNameForDetails(column);
		if (col == null) {
			return null;
		}	
				
		String parentSql = buildQuery();
		String sql = isMeasure ? 
				"SELECT COUNT(1), SUM(" + col + "), MAX(" + col + "),  MIN(" + col + "), AVG(" + col + ") FROM (" + parentSql + ") AS B"
				: "SELECT COUNT(1), 0 as somme, 0 as maxi, 0 as mini, 0 as moyen FROM (" + parentSql + ") AS B";
		
		return sql;		
	}
	
	public String buildColumnDuplicateCountQuery() {	
		if(this.filter.getColumns().size() == 0) {
			return null;
		}
		String parentSql = buildQuery();
		String sql = "SELECT COUNT(1) FROM (" + parentSql + ") AS B";	
		String coma = " GROUP BY ";
		for (MaterializedGridColumn column : this.filter.getColumns()) {
			String col = getColumnNameForDetails(column);
			if (col == null) {
				continue;
			}
			sql += coma + col;
			coma = ", ";
		}
		sql += " HAVING count(*) > 1";		
		sql = "SELECT count(*) FROM (" + sql + ") as BB";
		return sql;
		
	}
	
	public String buildColumnDuplicateQuery() {	
		if(this.filter.getColumns().size() == 0) {
			return null;
		}
		String parentSql = buildQuery();
		String sql = "SELECT ";	
		String coma = "";
		for (MaterializedGridColumn column : this.filter.getColumns()) {
			String col = getColumnNameForDetails(column);
			if (col == null) {
				continue;
			}
			sql += coma + col;
			coma = ", ";
		}
		sql += coma + "COUNT(1) FROM (" + parentSql + ") AS B";	
		
		coma = " GROUP BY ";
		for (MaterializedGridColumn column : this.filter.getColumns()) {
			String col = getColumnNameForDetails(column);
			if (col == null) {
				continue;
			}
			sql += coma + col;
			coma = ", ";
		}
		sql += " HAVING count(*) > 1";	
		return sql;
	}
	
	protected String getColumnNameForDetails(MaterializedGridColumn column) {
		return column.getDbColumnName();
	}

	@Override
	protected String buildSelectPart() {
		String selectPart = "SELECT ";
		String coma = "";		
		for (MaterializedGridColumn column : filter.getGrid().getColumns()) {
			String col = column.getId() != null && column.isPublished() ? column.getDbColumnName() : "null as " + column.getDbColumnName();
			if (!StringUtils.hasText(col)) {
				continue;
			}
			if(column.isMeasure()) {
				String functions = column.getDimensionFunction();
				if(consolidate && !StringUtils.hasText(functions)) {
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
//			if(column.isMeasure() && consolidate) {
//				col = "SUM(" + col + ")";
//			}
			else if (column.isPeriod() && column.isPublished()) {
				col = buildPeriodColumSelectPart(column, col);
			}
			
			selectPart += coma + col;
			coma = ", ";
		}
		if(consolidate) {
			selectPart += coma + '1';
		}
		else {
			selectPart += coma + "id";
		}
		return selectPart;
	}
	
	protected String buildPeriodColumSelectPart(MaterializedGridColumn column, String col) {
		if(column != null && column.getGroupBy() != null) {
			String sql = null;
			
			if(column.getGroupBy().isDayOfMonth()){
				return col;
			}
			else if(column.getGroupBy().isYear()){		
				sql = "cast(date_trunc('year', ".concat(col).concat(") as date)");
			}
			else if(column.getGroupBy().isDayOfWeek()){
				sql = "to_char(".concat(col).concat(", 'Day')");
			}	
			else if(column.getGroupBy().isQuarter()){
				sql = "cast(date_trunc('quarter', ".concat(col).concat(") as date)");
			}	
			else if(column.getGroupBy().isMonth()){		
				sql = "cast(date_trunc('month', ".concat(col).concat(") as date)");
			}
			else if(column.getGroupBy().isWeek()){
				sql = "cast(date_trunc('week', ".concat(col).concat(") as date)");
			}
			
			if(StringUtils.hasText(sql)) {	
				String alias = column.buildDbColumnAlias();
				sql = "CASE WHEN ".concat(col).concat(" IS NOT NULL THEN ").concat(sql).concat(" ELSE NULL END AS ").concat(alias);
				column.setAlias(alias);
			}
			return sql;
		}
		return col;
	}
	
	@Override
	protected String buildFromPart() {
		String fromPart = " FROM " + filter.getGrid().getMaterializationTableName();		
		return fromPart;
	}

	@Override
	protected String buildWherePart() {
		String wherePart = "";
		String coma = " WHERE ";
			
		if (getFilter().getGrid() != null && getFilter().getGrid().getUserFilter() != null && !ignoreUserFilter) {
			String userFilterSql = buildUniverseFilterWherePart(getFilter().getGrid().getUserFilter());
			if (StringUtils.hasText(userFilterSql)) {
				wherePart += coma + "(" + userFilterSql + ")";
				coma = " AND ";
			}
		}
		if (getFilter().getGrid() != null && getFilter().getGrid().getAdminFilter() != null) {
			String adminFilterSql = buildUniverseFilterWherePart(getFilter().getGrid().getAdminFilter());
			if (StringUtils.hasText(adminFilterSql)) {
				wherePart += coma + "(" + adminFilterSql + ")";
				coma = " AND ";
			}
		}
		
		if (getFilter().getGrid() != null && getFilter().getGrid().getGridUserFilter() != null) {
			String userFilterSql = buildUniverseFilterWherePart(getFilter().getGrid().getGridUserFilter());
			if (StringUtils.hasText(userFilterSql)) {
				wherePart += coma + "(" + userFilterSql + ")";
				coma = " AND ";
			}
		}
		if (getFilter().getGrid() != null && getFilter().getGrid().getGridAdminFilter() != null) {
			String adminFilterSql = buildUniverseFilterWherePart(getFilter().getGrid().getGridAdminFilter());
			if (StringUtils.hasText(adminFilterSql)) {
				wherePart += coma + "(" + adminFilterSql + ")";
				coma = " AND ";
			}
		}
		
		if (getFilter().getFilter() != null) {
			String filterSql = buildUniverseFilterWherePart(getFilter().getFilter());
			if (StringUtils.hasText(filterSql)) {
				wherePart += coma + "(" + filterSql + ")";
				coma = " AND ";
			}
		}		
		
		if(filter.getColumnFilters() != null){			
			String query = getSqlOperationForFilter(filter.getColumnFilters(), filter);
			if (StringUtils.hasText(query)) {
				wherePart += coma + "(" + query + ")";
				coma = " AND ";
			}
		}	
		if(filter.getIds() != null && filter.getIds().size() > 0) {
			String values = "";
			String virg = "";
			for(Long id : filter.getIds()) {
				values += virg + id;
				virg = ",";
			}
			wherePart += coma + " id in (" + values + ")";
			coma = " AND ";
		}
		return wherePart;
	}
		
	@Override
	protected String buildOrderPart() {
		String sql = "";
		String coma = "";
		if(filter.isOrderById()) {
			sql = "ID";
		}
		else {
			for (MaterializedGridColumn column : filter.getGrid().getColumns()) {
				if (column.getId() != null && column.isPublished() && column.getOrderAsc() != null) {
					String col = org.springframework.util.StringUtils.hasText(column.getAlias()) ? column.getAlias() : column.getDbColumnName();
					String o = column.getOrderAsc() ? " ASC" : " DESC";
					sql += coma + col + o;
					coma = ", ";
				}
			}
		}
		if (!sql.isEmpty()){
			sql = " ORDER BY " + sql;
		}
		return sql;
	}
	
	protected String buildGroupByPart() {		
		String groupBy = "";
		String coma = "";
		for (MaterializedGridColumn column : filter.getGrid().getColumns()) {			
			if (column.getId() != null && column.isPublished() && !column.isMeasure()) {
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
					String col = org.springframework.util.StringUtils.hasText(column.getAlias()) ? column.getAlias() : column.getDbColumnName();
					groupBy += coma + col;
					coma = ", ";
				}
			}
		}
		if(!groupBy.isEmpty()) {
			groupBy = " GROUP BY " + groupBy;
		}		
		return groupBy;
	}
	
	
	protected String getSqlOperationForFilter(ColumnFilter columnFilter, MaterializedGridDataFilter grilleFilter){
		String sql = "";
		String coma = "";
		if(columnFilter != null){
			if(columnFilter.isGrouped()) {
				for(ColumnFilter item : columnFilter.getItems()) {
					String query = getSqlOperationForFilter(item, grilleFilter);
					if (StringUtils.hasText(query)) {
						if(item.isGrouped()) {
							if(item.getOperation() != null) {
								item.getOperation();
							}
							query =  " (" + query + ") ";
						} 
						sql += coma  + query;
						coma = " " + columnFilter.getOperation() + " ";
					}
				}
			}
			else {
				sql = getSqlOperationForColumn(columnFilter, grilleFilter); 
			}
		}
		
		return sql;
	}
	
	protected String getSqlOperationForColumn(ColumnFilter columnFilter, MaterializedGridDataFilter grilleFilter){
		String sql = null;
		MaterializedGridColumn column = new MaterializedGridColumn();
		column.setType(columnFilter.getDimensionType());
		column.setId(columnFilter.getDimensionId());
		String col = column.getDbColumnName();
		String operator = columnFilter.getOperation();
		String value = columnFilter.getValue();
		boolean isMeasure = column.isMeasure();
		boolean isPeriod = column.isPeriod();
			
		if(!StringUtils.hasText(operator)) {
			operator = "StartsWith";		
		}
		GridFilterOperator gridFilterOperator = new GridFilterOperator();		
		if(gridFilterOperator.isStartsWith(operator)){
			if(isPeriod) {
				String periodValue = buildPeriodValue(value);
				if(periodValue != null) {
					sql = col + " = " + periodValue;
				}
				else {
					if(StringUtils.hasText(value)) {
						sql = col + " LIKE '" + value.toUpperCase() + "%'";
					}
				}
			}
			else
				if(StringUtils.hasText(value)) {
					sql = "UPPER(" + col + ") LIKE '" + value.toUpperCase() + "%'";
				}
		}
		else if(gridFilterOperator.isEndsWith(operator)){
			if(isPeriod) {
				String periodValue = buildPeriodValue(value);
				if(periodValue != null) sql = col + " = " + periodValue;
			}
			else
				if(StringUtils.hasText(value)) {
					sql = "UPPER(" + col + ") LIKE '%" + value.toUpperCase() + "'";
				}
		}
		else if(gridFilterOperator.isContains(operator) || GridFilterOperator.LIKE_OPERATOR.equalsIgnoreCase(operator) || GridFilterOperator.LIKE_OPERATOR1.equalsIgnoreCase(operator)){
			if(isPeriod) {
				String periodValue = buildPeriodValue(value);
				if(periodValue != null) sql = col + " = " + periodValue;
			}			
			else {
				if(StringUtils.hasText(value)) {
					if(gridFilterOperator.isContains(operator)) value = "%" + value + "%";
					sql = "UPPER(" + col + ") LIKE '" + value.toUpperCase() + "'";
				}				
			}
		}
		else if(gridFilterOperator.isNotContains(operator) 
				|| GridFilterOperator.NOT_LIKE_OPERATOR.equalsIgnoreCase(operator) 
				|| GridFilterOperator.NOT_LIKE_OPERATOR1.equalsIgnoreCase(operator)){	
			if(isPeriod) {
				String periodValue = buildPeriodValue(value);
				if(periodValue != null) sql = col + " <> " + periodValue;
			}
			else {
				if(StringUtils.hasText(value)) {
					if(gridFilterOperator.isNotContains(operator)) value = "%" + value + "%";			
					sql = "UPPER(" + col + ") NOT LIKE '" + value.toUpperCase() + "'";
				}
			}
		}
		else if(gridFilterOperator.isEquals(operator)){
			if(isMeasure) {
				BigDecimal measure = buildDecimalValue(value);
				if(measure != null) sql = col + " = " + measure;
			} 
			else if(isPeriod) {
				String periodValue = buildPeriodValue(value);
				if(periodValue != null) sql = col + " = " + periodValue;
			}
			else 
				if(StringUtils.hasText(value)) {
					sql = "UPPER(" + col + ") = '" + value.toUpperCase() + "'";
				}
			
		}
		else if(gridFilterOperator.isNotEquals(operator)){
			if(isMeasure) {
				BigDecimal measure = buildDecimalValue(value);
				if(measure != null) sql = col + " <> " + measure;
			} 
			else if(isPeriod) {
				String periodValue = buildPeriodValue(value);
				if(periodValue != null) sql = col + " <> " + periodValue;
			} 
			else
				if(StringUtils.hasText(value)) {
					sql = "UPPER(" + col + ") != '" + value.toUpperCase() + "'";	
				}		
		}
		else if(gridFilterOperator.isGreaterOrEquals(operator)){
			if(isMeasure) {
				BigDecimal measure = buildDecimalValue(value);
				if(measure != null) sql = col + " >= " + measure;
			} 
			else if(isPeriod) {
				String periodValue = buildPeriodValue(value);
				if(periodValue != null) sql = col + " >= " + periodValue;
			}  
			else 
				if(StringUtils.hasText(value)) {
					sql = "UPPER(" + col + ") >= '" + value.toUpperCase() + "'";	
				}
		}
		else if(gridFilterOperator.isLessOrEquals(operator)){
			if(isMeasure) {
				BigDecimal measure = buildDecimalValue(value);
				if(measure != null) sql = col + " <= " + measure;
			} 
			else if(isPeriod) {
				String periodValue = buildPeriodValue(value);
				if(periodValue != null) sql = col + " <= " + periodValue;
			}  
			else
				if(StringUtils.hasText(value)) {
					sql = "UPPER(" + col + ") <= '" + value.toUpperCase() + "'";
				}
		}
		else if(gridFilterOperator.isLess(operator) ){
			if(isMeasure) {
				BigDecimal measure = buildDecimalValue(value);
				if(measure != null) sql = col + " < " + measure;
			} 
			else if(isPeriod) {
				String periodValue = buildPeriodValue(value);
				if(periodValue != null) sql = col + " < " + periodValue;
			}  
			else
				if(StringUtils.hasText(value)) {
					sql = "UPPER(" + col + ") < '" + value.toUpperCase() + "'";
				}
		}
		else if(gridFilterOperator.isGreater(operator) ){
			if(isMeasure) {
				BigDecimal measure = buildDecimalValue(value);
				if(measure != null) sql = col + " > " + measure;
			} 
			else if(isPeriod) {
				String periodValue = buildPeriodValue(value);
				if(periodValue != null) sql = col + " > " + periodValue;
			}
			else
				if(StringUtils.hasText(value)) {
					sql = "UPPER(" + col + ") > '" + value.toUpperCase() + "'";
				}
		}
		else if(gridFilterOperator.isNull(operator) || gridFilterOperator.isNullOrEmpty(operator)){
			if(isMeasure || isPeriod)sql = col + " IS NULL";
			else sql = "(" + col + " IS NULL OR " + col + " = '')";
		}
		else if(gridFilterOperator.isNotNull(operator) 
				|| gridFilterOperator.isNotNullOrEmpty(operator)){
			if(isMeasure || isPeriod)sql = col + " IS NOT NULL";
			else sql = "(" + col + " IS NOT NULL AND " + col + " <> '')";
		}
		
		return sql;
	}
	
	protected String buildPeriodValue(String value){
		if(StringUtils.hasText(value) && value.contains(":")) {
			return buildPeriodValue_(value);
		}
		String periodValue = null;
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
				if(date != null) {
					String sDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
					periodValue = "'" +  sDate + "'";
				}
			}catch(Exception e){}
			i--;
		}
		
		return periodValue;
	}
	
	protected String buildPeriodValue_(String value){
		String periodValue = null;
		Date date = null;
		SimpleDateFormat[] Formats = new SimpleDateFormat[]{				 
				new SimpleDateFormat("yyyy/MM/dd hh:mm:ss"),
				new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"),				
				new SimpleDateFormat("dd-MM-yyyy hh:mm:ss"),
				new SimpleDateFormat("dd/MM/yyyy hh:mm:ss")};
		int i = Formats.length;
		while (date == null && i > 0) {
			SimpleDateFormat f = Formats[i-1];
			try{				
				date = f.parse(value);
				if(date != null) {
					String sDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
					periodValue = "'" +  sDate + "'";
				}
			}catch(Exception e){}
			i--;
		}
		
		return periodValue;
	}
	
	
	protected BigDecimal buildDecimalValue(String value){
		BigDecimal decimalValue = null;
		try{			
			decimalValue = new BigDecimal(value.trim().replaceAll(",", "."));
		}catch(Exception e){}
		return decimalValue;
	}
	
	protected CalculatedMeasure getCalculatedMeasure(Long id) {
		Optional<CalculatedMeasure> result = calculatedMeasureRepository.findById(id);
		return result.isPresent() ? result.get() : null;
	}

}

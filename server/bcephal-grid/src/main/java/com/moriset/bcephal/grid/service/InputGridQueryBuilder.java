/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.dimension.CalculatedMeasure;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.domain.filters.GridFilterOperator;
import com.moriset.bcephal.domain.filters.MeasureFunctions;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.repository.dimension.CalculatedMeasureRepository;
import com.moriset.bcephal.service.QueryBuilder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class InputGridQueryBuilder extends QueryBuilder {

	private GrilleDataFilter filter;
	
	private UniverseSourceType sourceType;
	private Long sourceId;
	boolean ignoreUserFilter;
	
	protected CalculatedMeasureRepository calculatedMeasureRepository;
	
	
	public InputGridQueryBuilder(GrilleDataFilter filter) {
		super();
		this.filter = filter;
		this.sourceType = UniverseSourceType.INPUT_GRID;	
		this.sourceId = filter != null && filter.getGrid() != null ? filter.getGrid().getId() : null;
		if(filter != null && filter.getGrid() != null && filter.getGrid().getDataSourceType() != null) {
			if(filter.getGrid().getDataSourceType().isMaterializedGrid()) {
				setTableName(new MaterializedGrid(filter.getGrid().getDataSourceId()).getMaterializationTableName());
			}	
			else if(filter.getGrid().getDataSourceType().isJoin()) {
				setTableName(new Join(filter.getGrid().getDataSourceId()).getMaterializationTableName());
			}	
		}		
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
		GrilleColumn column = this.filter.getColumns().get(this.filter.getColumns().size() - 1);
		column.setDataSourceId(this.filter.getGrid().getDataSourceId());
		column.setDataSourceType(this.filter.getGrid().getDataSourceType());
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
		for (GrilleColumn column : this.filter.getColumns()) {
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
		for (GrilleColumn column : this.filter.getColumns()) {
			String col = getColumnNameForDetails(column);
			if (col == null) {
				continue;
			}
			sql += coma + col;
			coma = ", ";
		}
		sql += coma + "COUNT(1) FROM (" + parentSql + ") AS B";	
		
		coma = " GROUP BY ";
		for (GrilleColumn column : this.filter.getColumns()) {
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
	
	protected String getColumnNameForDetails(GrilleColumn column) {
		return buildDbColName(column);
	}
	
//	public String buildColumnDuplicateQuery() {	
//		this.filter.getGrid().setColumns(this.filter.getGrid().getColumnListChangeHandler().getItems());
//		if(this.filter.getGrid().getColumns().size() == 0) {
//			return null;
//		}
//		String sql = "SELECT ";
//		String coma = "";
//		for (GrilleColumn column : this.filter.getGrid().getColumns()) {
//			String col = buildDbColName(column);
//			if (col == null) {
//				continue;
//			}
//			//sql += coma + col;
//			//coma = ", ";
//		}
//				
//		if(StringUtils.hasText(sql)){
//			sql += coma + "count(1)";
//			sql += buildFromPart();
//			String wherePart = buildWherePart();
//			if(StringUtils.hasText(wherePart)){
//				sql += wherePart;
//			}
//			coma = " GROUP BY ";
//			for (GrilleColumn column : this.filter.getGrid().getColumns()) {
//				String col = buildDbColName(column);
//				if (col == null) {
//					continue;
//				}
//				sql += coma + col;
//				coma = ", ";
//			}
//			sql += " HAVING count(*) > 1";
//		}
//		sql = "SELECT count(*) FROM (" + sql + ") as A";
//		return sql;
//	}
	
	
	@Override
	protected String buildSelectPart() {
		log.debug("Build select part...");
		String selectPart = "SELECT DISTINCT ";
		selectPart = "SELECT ";
		String coma = "";		
		for (GrilleColumn column : this.filter.getGrid().getColumns()) {
			String col = buildDbColName(column);
			if (col == null) {
				continue;
			}
			selectPart += coma + col;
			coma = ", ";
		}
		selectPart += coma + UniverseParameters.ID;
		return selectPart;
	}

	@Override
	protected String buildOrderPart() {
		String sql = "";
		String coma = "";
		for (GrilleColumn column : this.filter.getGrid().getColumns()) {
			if (column.getOrderAsc() != null) {
				String col = buildDbColName(column);
				String o = column.getOrderAsc() ? " ASC" : " DESC";
				sql += coma + col + o;
				coma = ", ";
			}
		}
		if (!sql.isEmpty()){
			sql = " ORDER BY " + sql;
		}
		return sql;
	}
	
	@Override
	protected String buildWherePart() {
		String sql = "";
		String where = " WHERE ";
		if (sourceType != null) {
			sql += where + UniverseParameters.SOURCE_TYPE + " = '" + sourceType + "'";
			where = " AND ";
			if(sourceId != null) {
				sql += where + UniverseParameters.SOURCE_ID + " = " + sourceId;
			}
			where = " AND ";
		}
		else {
			//sql += where + UniverseParameters.ISREADY +  " = true";
			//where = " AND ";
		} 
		
		if(filter.getIds() != null && filter.getIds().size() > 0){
			sql += where + UniverseParameters.ID + " IN (";
			String coma = "";
			for(Long id : filter.getIds()){
				sql = sql.concat(coma).concat(id.toString());
				coma = ", ";
			}
			sql += ")";
			where = " AND ";
		}

		if (filter.getGrid().getUserFilter() != null && !ignoreUserFilter)  {
			String userFilterSql = buildUniverseFilterWherePart(filter.getGrid().getUserFilter());
			if (StringUtils.hasText(userFilterSql)) {
				sql += where + "(" + userFilterSql + ")";
				where = " AND ";
			}
		}
		if (filter.getGrid().getAdminFilter() != null) {
			String adminFilterSql = buildUniverseFilterWherePart(filter.getGrid().getAdminFilter());
			if (StringUtils.hasText(adminFilterSql)) {
				sql += where + "(" + adminFilterSql + ")";
				where = " AND ";
			}
		}
		
		if(filter.getColumnFilters() != null){			
			String query = getSqlOperationForFilter(filter.getColumnFilters(), filter);
			if (StringUtils.hasText(query)) {
				sql += where + "(" + query + ")";
				where = " AND ";
			}
		}
		return sql;
	}
	
	protected String getSqlOperationForFilter(ColumnFilter columnFilter, GrilleDataFilter grilleFilter){
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
							query = " (" + query + ")";
						} 
						sql += coma  + query;
						coma = " " + columnFilter.getOperation() + " ";
					}
				};
			}
			else {
				sql = getSqlOperationForColumn(columnFilter, grilleFilter); 
			}
		}
		
		return sql;
	}
	
	protected String getSqlOperationForColumn(ColumnFilter columnFilter, GrilleDataFilter grilleFilter){
		String sql = null;
		GrilleColumn column = new GrilleColumn();
		column.setType(columnFilter.getDimensionType());
		column.setDimensionId(columnFilter.getDimensionId());
		
		if(grilleFilter.getGrid() != null) {
			column.setDataSourceType(grilleFilter.getGrid().getDataSourceType());
			column.setDataSourceId(grilleFilter.getGrid().getDataSourceId());
		}
		String col = buildDbColName(column);
		String operator = columnFilter.getOperation();
		String value = columnFilter.getValue();
		boolean isMeasure = column.isMeasure();
		boolean isPeriod = column.isPeriod();
		
		if(getFilter().getGrid().isReport() && isMeasure && getFilter().getGrid().isConsolidated() &&
				grilleFilter.getGrid() != null && (grilleFilter.getGrid().getDataSourceType().isMaterializedGrid() || grilleFilter.getGrid().getDataSourceType().isUniverse() || grilleFilter.getGrid().getDataSourceType().isReportGrid())) {
			String functions = MeasureFunctions.SUM.code;
			col = col + "_" + functions;
		}
		
//		if(isMeasure && grilleFilter.getGrid().isReport() && !grilleFilter.getGrid().isReconciliation()) {
//			String functions = StringUtils.hasText(columnFilter.column.getFunctions()) ? columnFilter.column.getFunctions() : Functions.SUM.code;
//			col = col.concat("_").concat(functions);
//		}
				
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
	
	protected Date buildPeriodDate(String value){		
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
			}catch(Exception e){}
			i--;
		}		
		return date;
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
	
	protected String buildDbColName(GrilleColumn column) {
		return column.getUniverseTableColumnName();
	}
	
	protected CalculatedMeasure getCalculatedMeasure(Long id) {
		Optional<CalculatedMeasure> result = calculatedMeasureRepository.findById(id);
		return result.isPresent() ? result.get() : null;
	}

}

/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.filters.AttributeFilterItem;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.filters.GridFilterOperator;
import com.moriset.bcephal.domain.filters.MeasureFilterItem;
import com.moriset.bcephal.domain.filters.PeriodFilterItem;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.JoinColumn;
import com.moriset.bcephal.grid.domain.JoinCondition;
import com.moriset.bcephal.grid.domain.JoinGrid;
import com.moriset.bcephal.grid.domain.JoinKey;
import com.moriset.bcephal.grid.domain.SmartGrille;
import com.moriset.bcephal.grid.repository.JoinColumnRepository;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.service.QueryBuilder;
import com.moriset.bcephal.service.filters.ISpotService;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class JoinQueryBuilder {

	protected JoinFilter filter;

	protected Map<String, Object> parameters;

	protected List<SmartGrille> grids;

	protected boolean addMainGridOid;

	protected JoinColumnRepository JoinColumnRepository;
	
	protected ParameterRepository parameterRepository;
	
	protected ISpotService spotService;
		
	public JoinQueryBuilder(JoinFilter filter,JoinColumnRepository JoinColumnRepository,ISpotService spotService) {
		this.filter = filter;
		this.addMainGridOid = filter != null ? !filter.getJoin().isConsolidated() : false;
		this.grids = new ArrayList<SmartGrille>();
		this.JoinColumnRepository = JoinColumnRepository;
		this.spotService = spotService;		
	}

	public String buildCountQuery() throws Exception {
		parameters = new HashMap<String, Object>();
		String sql = "SELECT 1";
		if(filter.getColumnFilters() != null){
			sql = buildSelectPart();
		}
		sql += buildFromPart();
		String wherePart = buildWherePart();
		if (StringUtils.hasText(wherePart)) {
			sql += wherePart;
		}
		String groupBy = buildGroupByPart();
		if (StringUtils.hasText(groupBy)) {
			sql += groupBy;
		}
		sql = "SELECT COUNT(1) FROM (" + sql + ") AS items";
		if(filter.getColumnFilters() != null){
			String headerPart = buildWherePartForHeader();
			if (StringUtils.hasText(headerPart)) {
				sql += headerPart;
			}
		}		
		return sql;
	}

	public String buildQuery() throws Exception {
		parameters = new HashMap<String, Object>();
		String sql = buildSelectPart();
		if (StringUtils.hasText(sql)) {
			sql += buildFromPart();
			String wherePart = buildWherePart();
			if (StringUtils.hasText(wherePart)) {
				sql += wherePart;
			}

			String groupBy = buildGroupByPart();
			if (StringUtils.hasText(groupBy)) {
				sql += groupBy;
			}

			String orderBy = buildOrderPart();
			if (StringUtils.hasText(orderBy)) {
				sql += orderBy;
			}
		}
		
		String coma = " WHERE ";
		String wherePart = "";
		
		if(filter.getColumnFilters() != null){
			String headerPart = buildWherePartForHeader();
			if (StringUtils.hasText(headerPart)) {
//				sql = "SELECT * FROM (" + sql + ") AS items" + headerPart;
				wherePart += coma + "(" + headerPart + ")";
				coma = " AND ";
			}
		}
		if(filter.getFilter() != null) {
			String userFilterSql = new QueryBuilder().buildUniverseFilterWherePart(filter.getFilter());
			if (StringUtils.hasText(userFilterSql)) {
				wherePart += coma + "(" + userFilterSql + ")";
				coma = " AND ";	
			}
		}
		
		if (StringUtils.hasText(wherePart)) {
			sql = "SELECT * FROM (" + sql + ") AS items" + wherePart;
		}
		
		return sql;
	}

	public String buildColumnCountDetailsQuery() throws Exception {	
		if(this.filter.getJoinColumns().size() == 0) {
			return null;
		}
		JoinColumn column = this.getFilter().getJoinColumns().get(0);
		boolean isMeasure = column.isMeasure();		
		String col = column.getDbColAliasName();
		if (col == null) {
			return null;
		}	
				
		String parentSql = buildQuery();
		String sql = isMeasure ? 
				"SELECT COUNT(1), SUM(" + col + "), MAX(" + col + "),  MIN(" + col + "), AVG(" + col + ") FROM (" + parentSql + ") AS B"
				: "SELECT COUNT(1), 0 as somme, 0 as maxi, 0 as mini, 0 as moyen FROM (" + parentSql + ") AS B";
		
		return sql;		
	}
	
	public String buildColumnDuplicateCountQuery() throws Exception {	
		if(this.filter.getJoinColumns().size() == 0) {
			return null;
		}
		String parentSql = buildQuery();
		String sql = "SELECT COUNT(1) FROM (" + parentSql + ") AS B";	
		String coma = " GROUP BY ";
		for (JoinColumn column : this.getFilter().getJoinColumns()) {
			String col = column.getDbColAliasName();
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
	
	public String buildColumnDuplicateQuery() throws Exception {	
		if(this.filter.getJoinColumns().size() == 0) {
			return null;
		}
		String parentSql = buildQuery();
		String sql = "SELECT ";	
		String coma = "";
		for (JoinColumn column : this.getFilter().getJoinColumns()) {
			String col = column.getDbColAliasName();
			if (col == null) {
				continue;
			}
			sql += coma + col;
			coma = ", ";
		}
		sql += coma + "COUNT(1) FROM (" + parentSql + ") AS B";	
		
		coma = " GROUP BY ";
		for (JoinColumn column : this.getFilter().getJoinColumns()) {
			String col = column.getDbColAliasName();
			if (col == null) {
				continue;
			}
			sql += coma + col;
			coma = ", ";
		}
		sql += " HAVING count(*) > 1";	
		return sql;
	}
	
	
	
	
	protected String buildSelectPart() {
		String selectPart = "SELECT ";
		String coma = "";
		for (JoinColumn column : filter.getJoin().getColumns()) {
			String col = column.isCustom() ? column.getCustomCol(true, parameters, filter.getJoin(),spotService, filter.getVariableValues())
					: column.getDbColName(true, true);
			if (!StringUtils.hasText(col)) {
				continue;
			}
			selectPart += coma + col;
			coma = ", ";
		}
		if (addMainGridOid) {
			String col = getMainGridOidCol();
			if (col != null) {
				selectPart += coma + col;
				coma = ", ";
			}
		}
		return selectPart;
	}

	protected String buildFromPart() {
		String fromPart = "";
		String coma = " FROM ";
		boolean isFirst = true;
		List<JoinGrid> previousItems = new ArrayList<>();
		JoinGrid previousItem = null;
		for (JoinGrid item : filter.getJoin().getGrids()) {
			String table = item.getPublicationTableName(true);
			if (!StringUtils.hasText(table)) {
				continue;
			}
			fromPart += coma + table;
			if (!isFirst) {
				String onPart = buildOnPart(item, previousItems);
				if (StringUtils.hasText(onPart)) {
					fromPart += onPart;
				}
			}
			isFirst = false;
			coma = " LEFT JOIN ";
			previousItem = item;
			previousItems.add(previousItem);
		}
		return fromPart;
	}

	protected String buildOnPart(JoinGrid item, List<JoinGrid> previousItems) {
		String onPart = "";
		String coma = " ON (";
		for (JoinKey key : filter.getJoin().getKeys()) {
			if (key.compatibleTo(item, previousItems)) {
				String sql = key.getSql(true);
				if (!StringUtils.hasText(sql)) {
					continue;
				}
				onPart += coma + sql;
				coma = " AND ";
			}
		}
		if (!StringUtils.hasText(onPart)) {
			onPart += coma + "true";
		}
		onPart += ")";
		return onPart;
	}
	
	
	protected String buildWherePartForHeader() throws Exception {
		String wherePart = "";
		String coma = "";	
						
		if(filter.getColumnFilters() != null){			
			String query = getSqlOperationForFilter(filter.getColumnFilters(), filter);
			if (StringUtils.hasText(query)) {
				wherePart += coma + "(" + query + ")";
				coma = " AND ";
			}
		}
		
		return wherePart;
	}
	
	protected String getSqlOperationForFilter(ColumnFilter columnFilter, GrilleDataFilter grilleFilter) {
		String sql = "";
		String coma = "";
		if (columnFilter != null) {
			if (columnFilter.isGrouped()) {
				for (ColumnFilter item : columnFilter.getItems()) {
					String query = getSqlOperationForFilter(item, grilleFilter);
					if (StringUtils.hasText(query)) {
						if (item.isGrouped()) {
							if (item.getOperation() != null) {
								item.getOperation();
							}
							query =  " (" + query + ") ";
						}
						sql += coma + query;
						coma = " " + columnFilter.getOperation() + " ";
					}
				}
			} else {
				sql = getSqlOperationForColumn(columnFilter, grilleFilter);
			}
		}

		return sql;
	}

	protected String getSqlOperationForColumn(ColumnFilter columnFilter, GrilleDataFilter grilleFilter) {
		String sql = null;
		if(columnFilter.getJoinColumnId() != null) {
		Optional<JoinColumn> optional = JoinColumnRepository.findById(columnFilter.getJoinColumnId());
		if (optional.isPresent()) {
			JoinColumn column = optional.get();
			if (!column.isMeasure()) {
				column.setType(columnFilter.getDimensionType());
				column.setDimensionId(columnFilter.getDimensionId());
				String col = column.getDbColAliasName();;
				String operator = columnFilter.getOperation();
				String value = columnFilter.getValue();
				boolean isMeasure = column.isMeasure();
				boolean isPeriod = column.isPeriod();

//		if(isMeasure && grilleFilter.getGrid().isReport() && !grilleFilter.getGrid().isReconciliation()) {
//			String functions = StringUtils.hasText(columnFilter.column.getFunctions()) ? columnFilter.column.getFunctions() : Functions.SUM.code;
//			col = col.concat("_").concat(functions);
//		}

				if (!StringUtils.hasText(operator)) {
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
							if(org.springframework.util.StringUtils.hasText(value)) {
								sql = col + " LIKE '" + value.toUpperCase() + "%'";
							}
						}
					}
					else
						if(org.springframework.util.StringUtils.hasText(value)) {
							sql = "UPPER(" + col + ") LIKE '" + value.toUpperCase() + "%'";
						}
				}
				else if(gridFilterOperator.isEndsWith(operator)){
					if(isPeriod) {
						String periodValue = buildPeriodValue(value);
						if(periodValue != null) sql = col + " = " + periodValue;
					}
					else
						if(org.springframework.util.StringUtils.hasText(value)) {
							sql = "UPPER(" + col + ") LIKE '%" + value.toUpperCase() + "'";
						}
				}
				else if(gridFilterOperator.isContains(operator) || GridFilterOperator.LIKE_OPERATOR.equalsIgnoreCase(operator) || GridFilterOperator.LIKE_OPERATOR1.equalsIgnoreCase(operator)){
					if(isPeriod) {
						String periodValue = buildPeriodValue(value);
						if(periodValue != null) sql = col + " = " + periodValue;
					}			
					else {
						if(org.springframework.util.StringUtils.hasText(value)) {
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
						if(org.springframework.util.StringUtils.hasText(value)) {
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
						if(org.springframework.util.StringUtils.hasText(value)) {
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
						if(org.springframework.util.StringUtils.hasText(value)) {
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
						if(org.springframework.util.StringUtils.hasText(value)) {
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
						if(org.springframework.util.StringUtils.hasText(value)) {
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
						if(org.springframework.util.StringUtils.hasText(value)) {
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
						if(org.springframework.util.StringUtils.hasText(value)) {
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
			}
			}
		}
		return sql;
	}

	protected String buildWherePart() throws Exception {		
		List<JoinCondition> conditions = buildConditions();
		String wherePart = "";
		String coma = " WHERE ";
		for (JoinCondition item : conditions) {
			String part = item.asSql(parameters, filter.getJoin(), filter.getVariableValues());
			if (!StringUtils.hasText(part)) {
				continue;
			}
//			if (item.getPosition() > 0) {
//				coma = " " + item.getVerb() + " ";
//			}
//			wherePart += coma + part;
//			coma = " AND ";			

			boolean isFirst = item.getPosition() == 0;
			String verb = item.getVerb() != null ? item.getVerb() : FilterVerb.AND.name();
			boolean isAndNot = FilterVerb.ANDNO.name().equals(verb);
			boolean isOrNot = FilterVerb.ORNO.name().equals(verb);
			String verbString = " " + verb + " ";
			
			if(isAndNot) {
				verbString = isFirst ? " NOT " : " AND NOT ";
			}
			else if(isOrNot) {
				verbString = isFirst ? " NOT " : " OR NOT ";
			}
			else if(isFirst){
				verbString = coma;
				coma = " AND ";	
			}
			
			if(isAndNot || isOrNot) {
				part = "(" + part + ")";
			}			
			wherePart += verbString + part;				
		}
				
		return wherePart;
	}

	protected String buildOrderPart() {
		String sql = "";
		String coma = "";
		for (JoinColumn column : this.filter.getJoin().getColumns()) {
			if (column.getOrderAsc() != null && !column.isCustom()) {
				String col = column.getDbColName(true, false);
				String o = column.getOrderAsc() ? " ASC" : " DESC";
				sql += coma + col + o;
				coma = ", ";
			}
		}
		if (!sql.isEmpty()){
			sql = " ORDER BY " + sql;
		}
		else if (filter.getJoin().getColumns().size() > 0) {
			JoinColumn column = filter.getJoin().getColumns().get(0);
			if (!column.isCustom()) {
				sql = " ORDER BY " + column.getDbColName(true, false);
			}
		}
		return sql;
	}

	protected String buildGroupByPart() {
		String selectPart = "";
		String coma = " GROUP BY ";
		boolean hasMeasure = false;
		for (JoinColumn column : filter.getJoin().getColumns()) {
			if (column.isMeasure()) {
				hasMeasure = true;
			} else {								
				String col = column.getGroupByCols(filter.getJoin());
				if (col == null || col.equals("null")) {
					continue;
				}
				selectPart += coma + col;
				coma = ", ";
								
//				if (column.canGroupBy()) {
//					String col = column.isCustom() ? column.getCustomCol(false, parameters, filter.getJoin(),spotService)
//							: column.getDbColName(true, false);
//					if (col == null || col.equals("null")) {
//						continue;
//					}
//					selectPart += coma + col;
//					coma = ", ";
//				}
			}
		}
		if (addMainGridOid) {
			String col = getMainGridOidCol();
			if (col != null) {
				selectPart += coma + col;
				coma = ", ";
			}
		}
		return hasMeasure ? selectPart : null;
	}

	protected String getMainGridOidCol() {
		JoinGrid grid = filter.getJoin().getMainGrid();
		if (grid != null) {
			return grid.getDbGridVarName() + ".id";
		}
		return null;
	}
	
	private List<JoinCondition> buildConditions(){
		List<JoinCondition> conditions = new ArrayList<>(filter.getJoin().getConditions());
		int position = conditions.size();
		if(filter.getFilter() != null) {
			if(filter.getFilter().getAttributeFilter() != null) {
				List<AttributeFilterItem> items = filter.getFilter().getAttributeFilter().getSortedItems();		
				for(AttributeFilterItem item : items) {	
					JoinCondition condition = new JoinCondition(item, filter.getJoin());
					condition.setPosition(position++);
					conditions.add(condition);
				}
			}
			if(filter.getFilter().getPeriodFilter() != null) {
				List<PeriodFilterItem> items = filter.getFilter().getPeriodFilter().getSortedItems();		
				for(PeriodFilterItem item : items) {	
					JoinCondition condition = new JoinCondition(item, filter.getJoin());
					condition.setPosition(position++);
					conditions.add(condition);
				}
			}
			if(filter.getFilter().getMeasureFilter() != null) {
				List<MeasureFilterItem> items = filter.getFilter().getMeasureFilter().getSortedItems();		
				for(MeasureFilterItem item : items) {	
					JoinCondition condition = new JoinCondition(item, filter.getJoin());
					condition.setPosition(position++);
					conditions.add(condition);
				}
			}
		}
		return conditions;
	}
	
	
	protected String buildPeriodValue(String value) {
		if (StringUtils.hasText(value) && value.contains(":")) {
			return buildPeriodValue_(value);
		}
		String periodValue = null;
		Date date = null;
		SimpleDateFormat[] Formats = new SimpleDateFormat[] { new SimpleDateFormat("yyyy/MM/dd"),
				new SimpleDateFormat("yyyy/MM/dd hh:mm:ss"), new SimpleDateFormat("yyyy-MM-dd"),
				new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"), new SimpleDateFormat("dd-MM-yyyy"),
				new SimpleDateFormat("dd-MM-yyyy hh:mm:ss"), new SimpleDateFormat("dd/MM/yyyy"),
				new SimpleDateFormat("dd/MM/yyyy hh:mm:ss") };
		int i = Formats.length;
		while (date == null && i > 0) {
			SimpleDateFormat f = Formats[i - 1];
			try {
				date = f.parse(value);
				if (date != null) {
					String sDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
					periodValue = "'" + sDate + "'";
				}
			} catch (Exception e) {
			}
			i--;
		}

		return periodValue;
	}

	protected String buildPeriodValue_(String value) {
		String periodValue = null;
		Date date = null;
		SimpleDateFormat[] Formats = new SimpleDateFormat[] { new SimpleDateFormat("yyyy/MM/dd hh:mm:ss"),
				new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"), new SimpleDateFormat("dd-MM-yyyy hh:mm:ss"),
				new SimpleDateFormat("dd/MM/yyyy hh:mm:ss") };
		int i = Formats.length;
		while (date == null && i > 0) {
			SimpleDateFormat f = Formats[i - 1];
			try {
				date = f.parse(value);
				if (date != null) {
					String sDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
					periodValue = "'" + sDate + "'";
				}
			} catch (Exception e) {
			}
			i--;
		}

		return periodValue;
	}

	protected BigDecimal buildDecimalValue(String value) {
		BigDecimal decimalValue = null;
		try {
			decimalValue = new BigDecimal(value.trim().replaceAll(",", "."));
		} catch (Exception e) {
		}
		return decimalValue;
	}
	
	
}

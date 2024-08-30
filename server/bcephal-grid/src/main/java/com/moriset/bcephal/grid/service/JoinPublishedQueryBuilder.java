/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.VariableValue;
import com.moriset.bcephal.domain.filters.AttributeFilter;
import com.moriset.bcephal.domain.filters.AttributeFilterItem;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.filters.GridFilterOperator;
import com.moriset.bcephal.domain.filters.MeasureFilter;
import com.moriset.bcephal.domain.filters.MeasureFilterItem;
import com.moriset.bcephal.domain.filters.PeriodFilter;
import com.moriset.bcephal.domain.filters.PeriodFilterItem;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.JoinColumn;
import com.moriset.bcephal.grid.repository.JoinColumnRepository;
import com.moriset.bcephal.service.filters.ISpotService;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
public class JoinPublishedQueryBuilder extends JoinQueryBuilder {

		
	public JoinPublishedQueryBuilder(JoinFilter filter, JoinColumnRepository JoinColumnRepository,ISpotService spotService) {
		super(filter, JoinColumnRepository, spotService);
		addMainGridOid = true;
	}

	@Override
	public String buildCountQuery() throws Exception {
		parameters = new HashMap<String, Object>();
		String sql = "SELECT 1";
		sql += buildFromPart();
		String wherePart = buildWherePart();
		if (StringUtils.hasText(wherePart)) {
			sql += wherePart;
		}
		String groupBy = buildGroupByPart();
		if (StringUtils.hasText(groupBy)) {
			sql += groupBy;
		}
		return "SELECT COUNT(1) FROM (" + sql + ") AS items";
	}

	@Override
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
		return sql;
	}

	@Override
	protected String buildSelectPart() {
		String selectPart = "SELECT ";
		String coma = "";
		for (JoinColumn column : filter.getJoin().getColumns()) {
			String col = column.getDbColAliasName();
			if (!StringUtils.hasText(col)) {
				continue;
			}
			String alias = " AS " + col;
			if (column.isMeasure()) {
				String func = org.springframework.util.StringUtils.hasText(column.getDimensionFunction()) ? column.getDimensionFunction() : "SUM";
				col = "CASE WHEN " + col + " IS NULL THEN 0 ELSE " + col + " END";
				col = func + "(" + col + ")";
			}			
			selectPart += coma + col + alias;
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

	@Override
	protected String buildFromPart() {
		String fromPart = " FROM " + filter.getJoin().getMaterializationTableName();
		return fromPart;
	}

	@Override
	protected String buildWherePart() throws Exception {
		String wherePart = "";
		String coma = " WHERE ";	
		if (filter.getJoin().getFilter() != null) {
			String userFilterSql = buildUniverseFilterWherePart(filter.getJoin().getFilter());
			if (StringUtils.hasText(userFilterSql)) {
				wherePart += coma + "(" + userFilterSql + ")";
				coma = " AND ";
			}
		}
		if (filter.getJoin().getAdminFilter() != null) {
			String adminFilterSql = buildUniverseFilterWherePart(filter.getJoin().getAdminFilter());
			if (StringUtils.hasText(adminFilterSql)) {
				wherePart += coma + "(" + adminFilterSql + ")";
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
		
		return wherePart;
	}

	protected String buildOrderPart() {
		String sql = "";
		String coma = "";
		for (JoinColumn column : this.filter.getJoin().getColumns()) {
			if (column.getOrderAsc() != null && !column.isCustom()) {
				String col = column.getDbColAliasName();
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
			if (!column.isCustom() && !column.isMeasure()) {
				sql = " ORDER BY " + column.getDbColAliasName();
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
				String col = column.getDbColAliasName();
				if (col == null || col.equals("null")) {
					continue;
				}
				selectPart += coma + col;
				coma = ", ";
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

	protected String buildUniverseFilterWherePart(UniverseFilter universeFilter) {
		String sql = "";
		String and = "";
		if (universeFilter.getMeasureFilter() != null) {
			String measureSql = buildMeasureFilterWherePart(universeFilter.getMeasureFilter(), universeFilter.getVariableValues());
			if (StringUtils.hasText(measureSql)) {
				sql = sql.concat(and).concat(measureSql);
				and = " AND ";
			}
		}
		if (universeFilter.getAttributeFilter() != null) {
			String scopeSql = buildScopeFilterWherePart(universeFilter.getAttributeFilter(), universeFilter.getVariableValues());
			if (StringUtils.hasText(scopeSql)) {
				sql = sql.concat(and).concat(scopeSql);
				and = " AND ";
			}
		}
		if (universeFilter.getPeriodFilter() != null) {
			String periodSql = buildPeriodFilterWherePart(universeFilter.getPeriodFilter(), universeFilter.getVariableValues());
			if (StringUtils.hasText(periodSql)) {
				sql = sql.concat(and).concat(periodSql);
				and = " AND ";
			}
		}
		return sql;
	}

	protected String buildMeasureFilterWherePart(MeasureFilter measureFilter, List<VariableValue> variableValues) {
		log.trace("Try to build MeasureFilter sql : {}", measureFilter);
		String sql = "";
		List<MeasureFilterItem> items = measureFilter.getSortedItems();		
		for(MeasureFilterItem item : items) {			
			String itemSql = item.toSql(variableValues, new ArrayList<>());
			if (StringUtils.hasText(itemSql)) {
				boolean isFirst = item.getPosition() == 0;
				FilterVerb verb = item.getFilterVerb() != null ? item.getFilterVerb() : FilterVerb.AND;
				boolean isAndNot = FilterVerb.ANDNO == verb;
				boolean isOrNot = FilterVerb.ORNO == verb;
				String verbString = " " + verb.name() + " ";
				
				if(isAndNot) {
					verbString = isFirst ? " NOT " : " AND NOT ";
				}
				else if(isOrNot) {
					verbString = isFirst ? " NOT " : " OR NOT ";
				}
				else if(isFirst){
					verbString = "";
				}
				
				if(isAndNot || isOrNot) {
					itemSql = "(" + itemSql + ")";
				}
				sql = sql.concat(verbString).concat(itemSql);
//				this.measures.put(item.getDimensionId(), new Measure(item.getDimensionId()));
			}
		};
		log.trace("MeasureFilter sql builded : {}", sql);
		return sql;
	}

	protected String buildScopeFilterWherePart(AttributeFilter attributeFilter, List<VariableValue> variableValues) {
		log.trace("Try to build AttributeFilter sql : {}", attributeFilter);
		String sql = "";
		List<AttributeFilterItem> items = attributeFilter.getSortedItems();		
		for(AttributeFilterItem item : items) {			
			String itemSql = item.toSql(variableValues, new ArrayList<>());
			if (StringUtils.hasText(itemSql)) {
				
				boolean isFirst = item.getPosition() == 0;
				FilterVerb verb = item.getFilterVerb() != null ? item.getFilterVerb() : FilterVerb.AND;
				boolean isAndNot = FilterVerb.ANDNO == verb;
				boolean isOrNot = FilterVerb.ORNO == verb;
				String verbString = " " + verb.name() + " ";
				
				if(isAndNot) {
					verbString = isFirst ? " NOT " : " AND NOT ";
				}
				else if(isOrNot) {
					verbString = isFirst ? " NOT " : " OR NOT ";
				}
				else if(isFirst){
					verbString = "";
				}
				
				if(isAndNot || isOrNot) {
					itemSql = "(" + itemSql + ")";
				}
				sql = sql.concat(verbString).concat(itemSql);					
//				this.attributes.put(item.getDimensionId(), new Attribute(item.getDimensionId()));
			}
		};
		log.trace("AttributeFilter sql builded : {}", sql);
		return sql;
	}
	
	
	protected String buildPeriodFilterWherePart(PeriodFilter periodFilter, List<VariableValue> variableValues) {
		log.trace("Try to build PeriodFilter sql : {}", periodFilter);
		String sql = "";
		List<PeriodFilterItem> items = periodFilter.getSortedItems();		
		for(PeriodFilterItem item : items) {			
			String itemSql = item.toSql(variableValues, new ArrayList<>());
			if (StringUtils.hasText(itemSql)) {
				
				boolean isFirst = item.getPosition() == 0;
				FilterVerb verb = item.getFilterVerb() != null ? item.getFilterVerb() : FilterVerb.AND;
				boolean isAndNot = FilterVerb.ANDNO == verb;
				boolean isOrNot = FilterVerb.ORNO == verb;
				String verbString = " " + verb.name() + " ";
				
				if(isAndNot) {
					verbString = isFirst ? " NOT " : " AND NOT ";
				}
				else if(isOrNot) {
					verbString = isFirst ? " NOT " : " OR NOT ";
				}
				else if(isFirst){
					verbString = "";
				}
				
				if(isAndNot || isOrNot) {
					itemSql = "(" + itemSql + ")";
				}
				sql = sql.concat(verbString).concat(itemSql);					
//				this.periods.put(item.getDimensionId(), new Period(item.getDimensionId()));
			}
		};
		log.trace("PeriodFilter sql builded : {}", sql);
		return sql;
	}

//	protected String buildPeriodFilterWherePart(PeriodFilter periodFilter) {
//		log.trace("Try to build PeriodFilter sql : {}", periodFilter);
//		String sql = "";
//		String and = "";
//		List<PeriodFilterItem> items = periodFilter.getSortedItems();		
//		for(PeriodFilterItem item : items) {			
//			String itemSql = item.toSql();
//			if (StringUtils.hasText(itemSql)) {
//				if(item.getPosition() > 0) {
//					and = item.getFilterVerb() != null ? " " + item.getFilterVerb().toString() + " " : " AND ";
//				}
//				sql = sql.concat(and).concat(itemSql);	
////				this.periods.put(item.getDimensionId(), new Period(item.getDimensionId()));
//			}
//		};
//		log.trace("PeriodFilter sql builded : {}", sql);
//		return sql;
//	}

	@Override
	protected String getMainGridOidCol() {
		return "id";
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

	

}

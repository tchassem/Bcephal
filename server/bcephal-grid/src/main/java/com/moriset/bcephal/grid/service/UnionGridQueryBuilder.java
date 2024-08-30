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

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.ReconciliationParameterCodes;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.filters.GridFilterOperator;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleRowType;
import com.moriset.bcephal.grid.domain.SmartGrille;
import com.moriset.bcephal.grid.domain.SmartMaterializedGridColumn;
import com.moriset.bcephal.grid.domain.UnionGridColumn;
import com.moriset.bcephal.grid.domain.UnionGridCondition;
import com.moriset.bcephal.grid.domain.UnionGridItem;
import com.moriset.bcephal.grid.repository.UnionGridColumnRepository;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.service.QueryBuilder;
import com.moriset.bcephal.service.filters.ISpotService;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UnionGridQueryBuilder {

	protected UnionGridFilter filter;

	protected Map<String, Object> parameters;

	protected List<SmartGrille> grids;

	protected boolean addMainGridOid;

	protected UnionGridColumnRepository unionGridColumnRepository;
	
	protected ParameterRepository parameterRepository;
	
	protected ISpotService spotService;
		
	public UnionGridQueryBuilder(UnionGridFilter filter, UnionGridColumnRepository unionGridColumnRepository, ISpotService spotService) {
		this.filter = filter;
		this.addMainGridOid = true;
		this.grids = new ArrayList<SmartGrille>();
		this.unionGridColumnRepository = unionGridColumnRepository;
		this.spotService = spotService;
	}

	public String buildCountQuery() throws Exception {
		parameters = new HashMap<String, Object>();
		String sql = "";
		String coma = "";
		for (UnionGridItem item : filter.getUnionGrid().getItems()) {
			String itemsql = buildQuery(item);
			if (StringUtils.hasText(itemsql)) {
				sql += coma + itemsql;
				coma = " UNION ";
			}
		}		
		
		sql = "SELECT COUNT(1) FROM (" + sql + ") AS items";		
		return sql;
	}

	public String buildQuery() throws Exception {
		parameters = new HashMap<String, Object>();
		String sql = "";
		String coma = "";
		for (UnionGridItem item : filter.getUnionGrid().getItems()) {
			String itemsql = buildQuery(item);
			if (StringUtils.hasText(itemsql)) {
				sql += coma + itemsql;
				coma = " UNION ";
			}
		}	
		
		String orderBy = buildOrderPart();
		if (StringUtils.hasText(orderBy)) {
			sql = "SELECT * FROM (" + sql + ") AS items " + orderBy;
		}		
		
		return sql;
	}
	
	

	protected String buildQuery(UnionGridItem item) throws Exception {				
		String sql = buildSelectPart(item);
		if (StringUtils.hasText(sql)) {
			sql += buildFromPart(item);
			String wherePart = buildWherePart(item);
			if (StringUtils.hasText(wherePart)) {
				sql += wherePart;
			}

			String groupBy = buildGroupByPart(item);
			if (StringUtils.hasText(groupBy)) {
				sql += groupBy;
			}

//			String orderBy = buildOrderPart();
//			if (StringUtils.hasText(orderBy)) {
//				sql += orderBy;
//			}
		}		
		return sql;
	}
		
	
	protected String buildSelectPart(UnionGridItem unionGridItem) {
		String selectPart = "SELECT ";
		String coma = "";
		for (UnionGridColumn column : filter.getUnionGrid().getColumns()) {
			SmartMaterializedGridColumn c = column.getColumnByGridId(unionGridItem.getGrid().getId());
			String col = c != null ? c.getDbColumnName() : "null";
			if (!StringUtils.hasText(col)) {
				continue;
			}
			selectPart += coma + col + " AS " + column.getDbColAliasName();
			coma = ", ";
		}		
		selectPart += coma + "" + unionGridItem.getGrid().getId() + " as gridid, id as id";
		return selectPart;
	}

	protected String buildFromPart(UnionGridItem unionGridItem) {
		String fromPart = " FROM " + unionGridItem.getGrid().getDbTableName();		
		return fromPart;
	}
	
	protected String buildWherePart(UnionGridItem unionGridItem) throws Exception {		
		List<UnionGridCondition> conditions = buildConditions();
		String wherePart = "";
		String coma = " WHERE ";
		String recoPart = buildRecoPart(unionGridItem);
		if (StringUtils.hasText(recoPart)) {
			wherePart += coma + "(" + recoPart + ")";
			coma = " AND ";
		}
		
		for (UnionGridCondition item : conditions) {
			String part = item.asSql(parameters, unionGridItem);
			if (!StringUtils.hasText(part)) {
				continue;
			}

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
		
		
		if(filter.getColumnFilters() != null){
			String headerPart = buildWherePartForHeader(unionGridItem);
			if (StringUtils.hasText(headerPart)) {
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
		
		if((getFilter().getIds() != null && getFilter().getIds().size() > 0) || (getFilter().getRecoValues() != null && !getFilter().getRecoValues().isEmpty())){
			if(getFilter().isConterpart()) {
				if(getFilter().getRecoAttributeId() != null) {
					if(getFilter().getRecoValues() != null && !getFilter().getRecoValues().isEmpty()) {
						SmartMaterializedGridColumn column = unionGridItem.getGridColumn(getFilter().getRecoAttributeId());			
						String col = column != null ? column.getDbColumnName() : "null";						
						String part = col + " IN (";
						String c = "";
						for(String value : getFilter().getRecoValues()){
							part += c  + "'" + value + "'";
							c = ", ";
						}
						part += ")";
						wherePart += coma + part;
						coma = " AND ";
					}
				}
			}
			else if(getFilter().getIds() != null && getFilter().getIds().size() > 0){
				wherePart += coma + UniverseParameters.ID + " IN (";
				String c = "";
				for(Long oid : getFilter().getIds()){
					wherePart += c + oid;
					c = ", ";
				}
				wherePart += ")";
				coma = " AND ";
			}
		}
						
		return wherePart;
	}
	
	
	protected String buildRecoPart(UnionGridItem unionGridItem) {
		
		String sql = "";
		String coma = "";
		
		if(getFilter().getRecoAttributeId() != null && getFilter().getRowType() != null 
				&& getFilter().getRowType() != GrilleRowType.ALL && getFilter().getRowType() != GrilleRowType.ON_HOLD) {	
			
			SmartMaterializedGridColumn c = unionGridItem.getGridColumn(getFilter().getRecoAttributeId());			
			String col = c != null ? c.getDbColumnName() : "null";
			
			if (getFilter().getRowType().isPositive()) {
				sql += coma + "((" + col + " IS NOT NULL AND " + col + " != '')";
				coma = " AND ";
			}
			else {
				sql += coma + "((" + col + " IS NULL OR " + col + " = '')";
				coma = " AND ";
			}
							
			if(getFilter().getRecoData() != null && getFilter().getRecoData().isAllowPartialReco() 
					&& getFilter().getRecoData().getPartialRecoAttributeId() != null
					&& getFilter().getRecoData().getRemainningMeasureId() != null) {
				String partialRecocol = unionGridItem.getGridColumn(getFilter().getRecoData().getPartialRecoAttributeId()).getDbColumnName();
				String remainningCol = unionGridItem.getGridColumn(getFilter().getRecoData().getRemainningMeasureId()).getDbColumnName();
				if (getFilter().getRowType().isPositive()) {
					sql += " AND (" + partialRecocol + " IS NOT NULL AND (" + remainningCol + " = 0))";		
				}
				else {
					sql += " OR (" + partialRecocol + " IS NOT NULL AND (" + remainningCol + " != 0))";		
				}
			}
			sql += ")";
			if(getFilter().getNeutralizationAttributeId() != null && getFilter().getRecoData().isAllowNeutralization()
					&& !getFilter().getRowType().isPositive()) {
				col = unionGridItem.getGridColumn(getFilter().getNeutralizationAttributeId()).getDbColumnName();
				sql += coma + "(" + col + " IS NULL OR " + col + " = '')";
				coma = " AND ";
			}
		}	
		
		if(getFilter().getFreezeAttributeId() != null && getFilter().getRowType() != null 
				&& getFilter().getRowType() != GrilleRowType.ALL) {
			String col = unionGridItem.getGridColumn(getFilter().getFreezeAttributeId()).getDbColumnName();
			if (getFilter().getRowType() == GrilleRowType.ON_HOLD) {
				sql += coma + "(" + col + " IS NOT NULL AND " + col + " != '')";
				coma = " AND ";
			}
			else {
				sql += coma + "(" + col + " IS NULL OR " + col + " = '')";
				coma = " AND ";
			}
		}			
		
		if(getFilter().getRecoData() != null && getFilter().getRecoData().getDebitCreditAttributeId() != null && getFilter().getRecoData().isUseDebitCredit() && (getFilter().isCredit() || getFilter().isDebit())) {
			if(getFilter().getDebitCreditAttribute() == null) {
				getFilter().setDebitCreditAttribute(new Attribute(getFilter().getRecoData().getDebitCreditAttributeId()));
				Parameter parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_DEBIT_VALUE, ParameterType.ATTRIBUTE_VALUE);
				if(parameter != null && parameter.getStringValue() != null) {
					 getFilter().setDebitValue(parameter.getStringValue());
				}
				else {
					getFilter().setDebitValue("D");
				}
				parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_CREDIT_VALUE, ParameterType.ATTRIBUTE_VALUE);
				if(parameter != null && parameter.getStringValue() != null) {
					 getFilter().setCreditValue(parameter.getStringValue());
				}
				else {
					getFilter().setCreditValue("C");
				}
			}
			if(getFilter().getDebitCreditAttribute() != null) {
				SmartMaterializedGridColumn c = unionGridItem.getGridColumn(getFilter().getRecoData().getDebitCreditAttributeId());			
				String col = c != null ? c.getDbColumnName() : "null";												
				if(getFilter().isCredit() && getFilter().isDebit()){
					sql += coma + "(" + col + " = '" + getFilter().getCreditValue() + "' OR " + col + " = '" + getFilter().getDebitValue() + "')";
				}
				else if(getFilter().isCredit()) {
					sql += coma + col + " = '" + getFilter().getCreditValue() + "'";
				}
				else if(getFilter().isDebit()) {
					sql += coma + col + " = '" + getFilter().getDebitValue() + "'";
				}
			}
		}
		return sql;
	}

	protected String buildGroupByPart(UnionGridItem item) {
		String selectPart = "";
//		String coma = " GROUP BY ";
		boolean hasMeasure = false;
		for (UnionGridColumn column : filter.getUnionGrid().getColumns()) {
			if (column.isMeasure()) {
				hasMeasure = true;
			} 
			else {								
//				String col = column.getGroupByCols(filter.getUnionGrid());
//				if (col == null || col.equals("null")) {
//					continue;
//				}
//				selectPart += coma + col;
//				coma = ", ";
					
			}
		}
		if (addMainGridOid) {
//			String col = getMainGridOidCol();
//			if (col != null) {
//				selectPart += coma + col;
//				coma = ", ";
//			}
		}
		return hasMeasure ? selectPart : null;
	}
	
	

	protected String buildSelectPart() {
		String selectPart = "SELECT ";
//		String coma = "";
//		for (UnionGridColumn column : filter.getUnionGrid().getColumns()) {
//			String col = column.getDbColName(true, true);
//			if (!StringUtils.hasText(col)) {
//				continue;
//			}
//			selectPart += coma + col;
//			coma = ", ";
//		}
		if (addMainGridOid) {
//			String col = getMainGridOidCol();
//			if (col != null) {
//				selectPart += coma + col;
//				coma = ", ";
//			}
		}
		return selectPart;
	}

	protected String buildFromPart() {
		String fromPart = "";
//		String coma = " FROM ";
//		boolean isFirst = true;
//		List<UnionGridItem> previousItems = new ArrayList<>();
//		UnionGridItem previousItem = null;
//		for (UnionGridItem item : filter.getUnionGrid().getItems()) {
//			String table = item.getPublicationTableName(true);
//			if (!StringUtils.hasText(table)) {
//				continue;
//			}
//			fromPart += coma + table;
//			if (!isFirst) {
//				String onPart = buildOnPart(item, previousItems);
//				if (StringUtils.hasText(onPart)) {
//					fromPart += onPart;
//				}
//			}
//			isFirst = false;
//			coma = " LEFT JOIN ";
//			previousItem = item;
//			previousItems.add(previousItem);
//		}
		return fromPart;
	}
	
	
	protected String buildWherePartForHeader(UnionGridItem unionGridItem) throws Exception {
		String wherePart = "";
		String coma = "";	
						
		if(filter.getColumnFilters() != null){			
			String query = getSqlOperationForFilter(unionGridItem, filter.getColumnFilters(), filter);
			if (StringUtils.hasText(query)) {
				wherePart += coma + "(" + query + ")";
				coma = " AND ";
			}
		}
		
		return wherePart;
	}
	
	protected String getSqlOperationForFilter(UnionGridItem unionGridItem, ColumnFilter columnFilter, GrilleDataFilter grilleFilter) {
		String sql = "";
		String coma = "";
		if (columnFilter != null) {
			if (columnFilter.isGrouped()) {
				for (ColumnFilter item : columnFilter.getItems()) {
					String query = getSqlOperationForFilter(unionGridItem, item, grilleFilter);
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
				sql = getSqlOperationForColumn(unionGridItem, columnFilter, grilleFilter);
			}
		}

		return sql;
	}

	protected String getSqlOperationForColumn(UnionGridItem unionGridItem, ColumnFilter columnFilter,
			GrilleDataFilter grilleFilter) {
		String sql = null;
		if (columnFilter.getDimensionId() != null) {
			UnionGridColumn unionGridColumn = filter.getUnionGrid().getColumnById(columnFilter.getDimensionId());			
			if (unionGridColumn != null) {
				SmartMaterializedGridColumn column = unionGridColumn.getColumnByGridId(unionGridItem.getGrid().getId());
				if(column == null) {
					return sql;
				}
				if (!column.isMeasure()) {
					String col = column.getDbColumnName();
					String operator = columnFilter.getOperation();
					String value = columnFilter.getValue();
					boolean isMeasure = column.isMeasure();
					boolean isPeriod = column.isPeriod();

					if (!StringUtils.hasText(operator)) {
						operator = "StartsWith";
					}
					GridFilterOperator gridFilterOperator = new GridFilterOperator();
					if (gridFilterOperator.isStartsWith(operator)) {
						if (isPeriod) {
							String periodValue = buildPeriodValue(value);
							if (periodValue != null) {
								sql = col + " = " + periodValue;
							} else {
								if (org.springframework.util.StringUtils.hasText(value)) {
									sql = col + " LIKE '" + value.toUpperCase() + "%'";
								}
							}
						} else if (org.springframework.util.StringUtils.hasText(value)) {
							sql = "UPPER(" + col + ") LIKE '" + value.toUpperCase() + "%'";
						}
					} else if (gridFilterOperator.isEndsWith(operator)) {
						if (isPeriod) {
							String periodValue = buildPeriodValue(value);
							if (periodValue != null)
								sql = col + " = " + periodValue;
						} else if (org.springframework.util.StringUtils.hasText(value)) {
							sql = "UPPER(" + col + ") LIKE '%" + value.toUpperCase() + "'";
						}
					} else if (gridFilterOperator.isContains(operator)
							|| GridFilterOperator.LIKE_OPERATOR.equalsIgnoreCase(operator)
							|| GridFilterOperator.LIKE_OPERATOR1.equalsIgnoreCase(operator)) {
						if (isPeriod) {
							String periodValue = buildPeriodValue(value);
							if (periodValue != null)
								sql = col + " = " + periodValue;
						} else {
							if (org.springframework.util.StringUtils.hasText(value)) {
								if (gridFilterOperator.isContains(operator))
									value = "%" + value + "%";
								sql = "UPPER(" + col + ") LIKE '" + value.toUpperCase() + "'";
							}
						}
					} else if (gridFilterOperator.isNotContains(operator)
							|| GridFilterOperator.NOT_LIKE_OPERATOR.equalsIgnoreCase(operator)
							|| GridFilterOperator.NOT_LIKE_OPERATOR1.equalsIgnoreCase(operator)) {
						if (isPeriod) {
							String periodValue = buildPeriodValue(value);
							if (periodValue != null)
								sql = col + " <> " + periodValue;
						} else {
							if (org.springframework.util.StringUtils.hasText(value)) {
								if (gridFilterOperator.isNotContains(operator))
									value = "%" + value + "%";
								sql = "UPPER(" + col + ") NOT LIKE '" + value.toUpperCase() + "'";
							}
						}
					} else if (gridFilterOperator.isEquals(operator)) {
						if (isMeasure) {
							BigDecimal measure = buildDecimalValue(value);
							if (measure != null)
								sql = col + " = " + measure;
						} else if (isPeriod) {
							String periodValue = buildPeriodValue(value);
							if (periodValue != null)
								sql = col + " = " + periodValue;
						} else if (org.springframework.util.StringUtils.hasText(value)) {
							sql = "UPPER(" + col + ") = '" + value.toUpperCase() + "'";
						}

					} else if (gridFilterOperator.isNotEquals(operator)) {
						if (isMeasure) {
							BigDecimal measure = buildDecimalValue(value);
							if (measure != null)
								sql = col + " <> " + measure;
						} else if (isPeriod) {
							String periodValue = buildPeriodValue(value);
							if (periodValue != null)
								sql = col + " <> " + periodValue;
						} else if (org.springframework.util.StringUtils.hasText(value)) {
							sql = "UPPER(" + col + ") != '" + value.toUpperCase() + "'";
						}
					} else if (gridFilterOperator.isGreaterOrEquals(operator)) {
						if (isMeasure) {
							BigDecimal measure = buildDecimalValue(value);
							if (measure != null)
								sql = col + " >= " + measure;
						} else if (isPeriod) {
							String periodValue = buildPeriodValue(value);
							if (periodValue != null)
								sql = col + " >= " + periodValue;
						} else if (org.springframework.util.StringUtils.hasText(value)) {
							sql = "UPPER(" + col + ") >= '" + value.toUpperCase() + "'";
						}
					} else if (gridFilterOperator.isLessOrEquals(operator)) {
						if (isMeasure) {
							BigDecimal measure = buildDecimalValue(value);
							if (measure != null)
								sql = col + " <= " + measure;
						} else if (isPeriod) {
							String periodValue = buildPeriodValue(value);
							if (periodValue != null)
								sql = col + " <= " + periodValue;
						} else if (org.springframework.util.StringUtils.hasText(value)) {
							sql = "UPPER(" + col + ") <= '" + value.toUpperCase() + "'";
						}
					} else if (gridFilterOperator.isLess(operator)) {
						if (isMeasure) {
							BigDecimal measure = buildDecimalValue(value);
							if (measure != null)
								sql = col + " < " + measure;
						} else if (isPeriod) {
							String periodValue = buildPeriodValue(value);
							if (periodValue != null)
								sql = col + " < " + periodValue;
						} else if (org.springframework.util.StringUtils.hasText(value)) {
							sql = "UPPER(" + col + ") < '" + value.toUpperCase() + "'";
						}
					} else if (gridFilterOperator.isGreater(operator)) {
						if (isMeasure) {
							BigDecimal measure = buildDecimalValue(value);
							if (measure != null)
								sql = col + " > " + measure;
						} else if (isPeriod) {
							String periodValue = buildPeriodValue(value);
							if (periodValue != null)
								sql = col + " > " + periodValue;
						} else if (org.springframework.util.StringUtils.hasText(value)) {
							sql = "UPPER(" + col + ") > '" + value.toUpperCase() + "'";
						}
					} else if (gridFilterOperator.isNull(operator) || gridFilterOperator.isNullOrEmpty(operator)) {
						if (isMeasure || isPeriod)
							sql = col + " IS NULL";
						else
							sql = "(" + col + " IS NULL OR " + col + " = '')";
					} else if (gridFilterOperator.isNotNull(operator)
							|| gridFilterOperator.isNotNullOrEmpty(operator)) {
						if (isMeasure || isPeriod)
							sql = col + " IS NOT NULL";
						else
							sql = "(" + col + " IS NOT NULL AND " + col + " <> '')";
					}
				}
			}
		}
		return sql;
	}

	protected String buildWherePart() throws Exception {		
		List<UnionGridCondition> conditions = buildConditions();
		String wherePart = "";
		String coma = " WHERE ";
		for (UnionGridCondition item : conditions) {
			String part = ""; //item.asSql(parameters, filter.getUnionGrid());
			if (!StringUtils.hasText(part)) {
				continue;
			}

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
		for (UnionGridColumn column : this.filter.getUnionGrid().getColumns()) {
			if (column.getOrderAsc() != null) {
				String col = column.getDbColAliasName();
				String o = column.getOrderAsc() ? " ASC" : " DESC";
				sql += coma + col + o;
				coma = ", ";
			}
		}
		if (!sql.isEmpty()){
			sql = " ORDER BY " + sql;
		}
		else if (filter.getUnionGrid().getColumns().size() > 0) {
//			UnionGridColumn column = filter.getUnionGrid().getColumns().get(0);
//			if (!column.isCustom()) {
//				sql = " ORDER BY " + column.getDbColName(true, false);
//			}
		}
		return sql;
	}

	protected String buildGroupByPart() {
		String selectPart = "";
//		String coma = " GROUP BY ";
		boolean hasMeasure = false;
		for (UnionGridColumn column : filter.getUnionGrid().getColumns()) {
			if (column.isMeasure()) {
				hasMeasure = true;
			} 
			else {								
//				String col = column.getGroupByCols(filter.getUnionGrid());
//				if (col == null || col.equals("null")) {
//					continue;
//				}
//				selectPart += coma + col;
//				coma = ", ";
					
			}
		}
		if (addMainGridOid) {
//			String col = getMainGridOidCol();
//			if (col != null) {
//				selectPart += coma + col;
//				coma = ", ";
//			}
		}
		return hasMeasure ? selectPart : null;
	}
	
	private List<UnionGridCondition> buildConditions(){
		List<UnionGridCondition> conditions = new ArrayList<>(filter.getUnionGrid().getConditions());
//		int position = conditions.size();
		if(filter.getFilter() != null) {
//			if(filter.getFilter().getAttributeFilter() != null) {
//				List<AttributeFilterItem> items = filter.getFilter().getAttributeFilter().getSortedItems();		
//				for(AttributeFilterItem item : items) {	
//					UnionGridCondition condition = new UnionGridCondition(item, filter.getUnionGrid());
//					condition.setPosition(position++);
//					conditions.add(condition);
//				}
//			}
//			if(filter.getFilter().getPeriodFilter() != null) {
//				List<PeriodFilterItem> items = filter.getFilter().getPeriodFilter().getSortedItems();		
//				for(PeriodFilterItem item : items) {	
//					UnionGridCondition condition = new UnionGridCondition(item, filter.getUnionGrid());
//					condition.setPosition(position++);
//					conditions.add(condition);
//				}
//			}
//			if(filter.getFilter().getMeasureFilter() != null) {
//				List<MeasureFilterItem> items = filter.getFilter().getMeasureFilter().getSortedItems();		
//				for(MeasureFilterItem item : items) {	
//					UnionGridCondition condition = new UnionGridCondition(item, filter.getUnionGrid());
//					condition.setPosition(position++);
//					conditions.add(condition);
//				}
//			}
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

	public String buildColumnCountDetailsQuery() throws Exception {	
		if(this.filter.getUnionGridColumns().size() == 0) {
			return null;
		}
		UnionGridColumn column = this.getFilter().getUnionGridColumns().get(0);
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
		if(this.filter.getUnionGridColumns().size() == 0) {
			return null;
		}
		String parentSql = buildQuery();
		String sql = "SELECT COUNT(1) FROM (" + parentSql + ") AS B";	
		String coma = " GROUP BY ";
		for (UnionGridColumn column : this.getFilter().getUnionGridColumns()) {
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
		if(this.filter.getUnionGridColumns().size() == 0) {
			return null;
		}
		String parentSql = buildQuery();
		String sql = "SELECT ";	
		String coma = "";
		for (UnionGridColumn column : this.getFilter().getUnionGridColumns()) {
			String col = column.getDbColAliasName();
			if (col == null) {
				continue;
			}
			sql += coma + col;
			coma = ", ";
		}
		sql += coma + "COUNT(1) FROM (" + parentSql + ") AS B";	
		
		coma = " GROUP BY ";
		for (UnionGridColumn column : this.getFilter().getUnionGridColumns()) {
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
	
	
}

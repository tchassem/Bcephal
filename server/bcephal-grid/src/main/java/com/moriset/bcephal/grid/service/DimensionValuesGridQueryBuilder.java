/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.ReconciliationParameterCodes;
import com.moriset.bcephal.domain.VariableValue;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.filters.GridFilterOperator;
import com.moriset.bcephal.domain.filters.MeasureFunctions;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleRowType;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.service.form.ReferenceCondition;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.service.QueryBuilder;
import com.moriset.bcephal.service.condition.VariableReferenceConditionItemType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author MORISET-004
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class DimensionValuesGridQueryBuilder extends QueryBuilder {

	/**
	 * 
	 */
	private DimensionDataFilter filter;
	
	protected ParameterRepository parameterRepository;
	
	public DimensionValuesGridQueryBuilder(DimensionDataFilter filter) {
		this.filter = filter;
		if(filter != null && filter.getDataSourceType() != null) {
			if(filter.getDataSourceType().isMaterializedGrid()) {
				setTableName(new MaterializedGrid(filter.getDataSourceId()).getMaterializationTableName());
			}	
			else if(filter.getDataSourceType().isJoin()) {
				setTableName(new Join(filter.getDataSourceId()).getMaterializationTableName());
			}	
		}
	}
	
	Attribute getAttribute() {
		Attribute attrib = new Attribute(filter.getDimensionId());
		attrib.setDataSourceId(filter.getDataSourceId());
		attrib.setDataSourceType(filter.getDataSourceType());
		return attrib;
	}
	
	Measure getMeasure() {
		Measure attrib = new Measure(filter.getDimensionId());
		attrib.setDataSourceId(filter.getDataSourceId());
		attrib.setDataSourceType(filter.getDataSourceType());
		return attrib;
	}
	
	Period getPeriod() {
		Period attrib = new Period(filter.getDimensionId());
		attrib.setDataSourceId(filter.getDataSourceId());
		attrib.setDataSourceType(filter.getDataSourceType());
		return attrib;
	}
	
	Dimension getDimension() {
		if(DimensionType.MEASURE.equals(filter.getDimensionType())) {
			return getMeasure();
		}
		if(DimensionType.PERIOD.equals(filter.getDimensionType())) {
			return getPeriod();
		}
		return getAttribute();
	}

	@Override
	public String buildCountQuery() {
		String sql = "SELECT COUNT(1) FROM (" + buildQuery() + ") AS A";		
		return sql;
	}
	
	@Override
	protected String buildSelectPart() {
		String selectPart = "SELECT DISTINCT " + getDimension().getUniverseTableColumnName() + " ";
		return selectPart;
	}	
	
	@Override
	protected String buildOrderPart() {
		return " ORDER BY " + getDimension().getUniverseTableColumnName() + " ASC ";
	}
	
	
	
	@Override
	protected String buildWherePart() {
		String sql = "";
		String where = " WHERE ";
				
		if (filter.getGrid() != null && filter.getGrid().getType() == GrilleType.INPUT) {
			sql += where + UniverseParameters.SOURCE_TYPE + " = '" + UniverseSourceType.INPUT_GRID + "'";
			where = " AND ";
			sql += where + UniverseParameters.SOURCE_ID + " = " + filter.getGrid().getId();
			where = " AND ";
		}	
		else if((filter.getDataSourceType().isUniverse() || filter.getDataSourceType().isInputGrid()) && filter.getDataSourceId() != null) {
			sql += where + UniverseParameters.SOURCE_TYPE + " = '" + UniverseSourceType.INPUT_GRID + "'";
			where = " AND ";
			sql += where + UniverseParameters.SOURCE_ID + " = " + filter.getDataSourceId();
			where = " AND ";
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
		
		if (filter.getUserFilter() != null) {
			String userFilterSql = buildUniverseFilterWherePart(filter.getUserFilter());
			if (StringUtils.hasText(userFilterSql)) {
				sql += where + "(" + userFilterSql + ")";
				where = " AND ";
			}
		}
		if (filter.getAdminFilter() != null) {
			String adminFilterSql = buildUniverseFilterWherePart(filter.getAdminFilter());
			if (StringUtils.hasText(adminFilterSql)) {
				sql += where + "(" + adminFilterSql + ")";
				where = " AND ";
			}
		}
		
		if(filter.getColumnFilters() != null){			
			String query = getSqlOperationForFilter(filter.getColumnFilters());
			if (StringUtils.hasText(query)) {
				sql += where + "(" + query + ")";
				where = " AND ";
			}
		}
		
		String conditions = buildConditionsWherePartSql();
		if(StringUtils.hasText(conditions)) {
			sql += where + "(" + conditions + ")";
			where = " AND ";
		}
		
		sql += buildWherePartForeReco(where);
		return sql;
	}
	
	
	
	public String buildConditionsWherePartSql() {
		String sql = "";
		if(filter.getReference() != null && filter.getReference().getConditions() != null && filter.getReference().getConditions().size() > 0) {
			String coma = "";		
			for(ReferenceCondition condition : filter.getReference().getConditions()) {
				String itemSql = condition.buildSql();
				if(StringUtils.hasText(itemSql)) {
					
					boolean isFirst = condition.getPosition() == 0;
					FilterVerb verb = condition.getVerb() != null ? FilterVerb.valueOf(condition.getVerb()) : FilterVerb.AND;
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
						verbString = coma;
					}
					
					if(isAndNot || isOrNot) {
						itemSql = "(" + itemSql + ")";
					}
					sql = sql.concat(verbString).concat(itemSql);					
				}
			}
		}
		
		if(filter.getVariableReference() != null && filter.getVariableReference().getItems() != null && filter.getVariableReference().getItems().size() > 0) {
			String coma = "";	
			filter.getVariableReference().sortItems();
			for(DashboardItemVariableReferenceConditionData condition : filter.getVariableReference().getItems()) {
				if (condition.getConditionItemType() == VariableReferenceConditionItemType.VARIABLE) {
					VariableValue value = null;
					if (filter.getVariableReference().getVariableValues() != null
							&& StringUtils.hasText(condition.getVariableName())
							&& (value = getVariableValue(condition.getVariableName())) != null ) {
						
						if (condition.getColumn().isAttribute()) {
							condition.setStringValue(value.getStringValue());
						} else if (condition.getColumn().isPeriod()) {
							PeriodValue p = new PeriodValue();
							p.setDateValue(value.getPeriodValue());
							condition.setPeriodValue(p);
						} else if (condition.getColumn().isMeasure()) {
							condition.setDecimalValue(value.getDecimalValue());
						}
					}else {
						continue; 
					}
				}
				String itemSql = condition.buildSql();
				if(StringUtils.hasText(itemSql)) {
					
					boolean isFirst = condition.getPosition() == 0;
					FilterVerb verb = condition.getVerb() != null ? FilterVerb.valueOf(condition.getVerb()) : FilterVerb.AND;
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
						verbString = coma;
					}
					
					if(isAndNot || isOrNot) {
						itemSql = "(" + itemSql + ")";
					}
					sql = sql.concat(verbString).concat(itemSql);					
				}
			}
		}
		return sql;
	}
	
	private VariableValue getVariableValue(String name) {
		for (VariableValue varel : filter.getVariableReference().getVariableValues()) {
			if(varel.getName().equals(name)) {
				return varel;
			}
		}
		return null;
	}
	
	protected String buildWherePartForeReco(String where) {
		String sql = "";
		if(getFilter().getGrid() == null || !getFilter().getGrid().isReport()) {
		
			if(getFilter().getRecoAttributeId() != null && getFilter().getRowType() != null 
					&& getFilter().getRowType() != GrilleRowType.ALL && getFilter().getRowType() != GrilleRowType.ON_HOLD) {
				String col = new Attribute(getFilter().getRecoAttributeId()).getUniverseTableColumnName();
				if (getFilter().getRowType().isPositive()) {
					sql += where + "((" + col + " IS NOT NULL AND " + col + " != '')";
					where = " AND ";
				}
				else {
					sql += where + "((" + col + " IS NULL OR " + col + " = '')";
					where = " AND ";
				}
				this.attributes.put(getFilter().getRecoAttributeId(), new Attribute(getFilter().getRecoAttributeId()));
				
				if(getFilter().getRecoData() != null && getFilter().getRecoData().isAllowPartialReco() 
						&& getFilter().getRecoData().getPartialRecoAttributeId() != null
						&& getFilter().getRecoData().getRemainningMeasureId() != null) {
					String partialRecocol = new Attribute(getFilter().getRecoData().getPartialRecoAttributeId()).getUniverseTableColumnName();
					String remainningCol = new Measure(getFilter().getRecoData().getRemainningMeasureId()).getUniverseTableColumnName();
					if (getFilter().getRowType().isPositive()) {
						sql += " AND (" + partialRecocol + " IS NOT NULL AND (" + remainningCol + " = 0))";		
					}
					else {
						sql += " OR (" + partialRecocol + " IS NOT NULL AND (" + remainningCol + " != 0))";		
					}					
					this.attributes.put(getFilter().getRecoData().getPartialRecoAttributeId(), new Attribute(getFilter().getRecoData().getPartialRecoAttributeId()));
					this.measures.put(getFilter().getRecoData().getRemainningMeasureId(), new Measure(getFilter().getRecoData().getRemainningMeasureId()));
				}
				sql += ")";
			}	
			
			if(getFilter().getFreezeAttributeId() != null && getFilter().getRowType() != null 
					&& getFilter().getRowType() != GrilleRowType.ALL) {
				String col = new Attribute(getFilter().getFreezeAttributeId()).getUniverseTableColumnName();
				if (getFilter().getRowType() == GrilleRowType.ON_HOLD) {
					sql += where + "(" + col + " IS NOT NULL AND " + col + " != '')";
					where = " AND ";
				}
				else {
					sql += where + "(" + col + " IS NULL OR " + col + " = '')";
					where = " AND ";
				}
				this.attributes.put(getFilter().getFreezeAttributeId(), new Attribute(getFilter().getFreezeAttributeId()));
			}			
			
			if(getFilter().isCredit() || getFilter().isDebit()) {
				if(getFilter().getDebitCreditAttribute() == null) {
					Parameter parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_DC_ATTRIBUTE, ParameterType.ATTRIBUTE);
					if(parameter != null && parameter.getLongValue() != null) {
						 getFilter().setDebitCreditAttribute(new Attribute(parameter.getLongValue()));
					}
					parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_DEBIT_VALUE, ParameterType.ATTRIBUTE_VALUE);
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
					String col = getFilter().getDebitCreditAttribute().getUniverseTableColumnName();									
					if(getFilter().isCredit() && getFilter().isDebit()){
						sql += where + "(" + col + " = '" + getFilter().getCreditValue() + "' OR " + col + " = '" + getFilter().getDebitValue() + "')";
					}
					else if(getFilter().isCredit()) {
						sql += where + col + " = '" + getFilter().getCreditValue() + "'";
					}
					else if(getFilter().isDebit()) {
						sql += where + col + " = '" + getFilter().getDebitValue() + "'";
					}
					this.attributes.put(getFilter().getDebitCreditAttribute().getId(), getFilter().getDebitCreditAttribute());
				}
			}
				
			if(getFilter().getIds() != null && getFilter().getIds().size() > 0){
				if(getFilter().isConterpart()) {
					if(getFilter().getRecoAttributeId() != null) {
						String part = UniverseParameters.ID + " IN (";
						String coma = "";
						for(Long oid : getFilter().getIds()){
							part += coma + oid;
							coma = ", ";
						}
						part += ")";
						
						String col = new Attribute(getFilter().getRecoAttributeId()).getUniverseTableColumnName();
						sql += where + "(" + col + " IN (Select distinct "
								+ col + " FROM " + UniverseParameters.UNIVERSE_TABLE_NAME
								+ " WHERE " + part + "))";
						where = " AND ";
					}
				}
				else {
					sql += where + UniverseParameters.ID + " IN (";
					String coma = "";
					for(Long oid : getFilter().getIds()){
						sql += coma + oid;
						coma = ", ";
					}
					sql += ")";
					where = " AND ";
				}
			}
		}
		
		
		return sql;
	}
	
	protected String getSqlOperationForFilter(ColumnFilter columnFilter){
		String sql = "";
		String coma = "";
		if(columnFilter != null){
			if(columnFilter.isGrouped()) {
				for(ColumnFilter item : columnFilter.getItems()) {
					String query = getSqlOperationForFilter(item);
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
				sql = getSqlOperationForColumn(columnFilter); 
			}
		}
		
		return sql;
	}
	
	protected String getSqlOperationForColumn(ColumnFilter columnFilter){
		String sql = null;
		GrilleColumn column = new GrilleColumn();
		column.setType(columnFilter.getDimensionType());		
		column.setDataSourceType(filter.getDataSourceType());
		column.setDataSourceId(filter.getDataSourceId());
		if(filter.getDataSourceType() == DataSourceType.JOIN) {
			column.setDimensionId(columnFilter.getJoinColumnId());
		}
		else {
			column.setDimensionId(columnFilter.getDimensionId());
		}
		String col = column.getUniverseTableColumnName();
		String operator = columnFilter.getOperation();
		String value = columnFilter.getValue();
		boolean isMeasure = column.isMeasure();
		boolean isPeriod = column.isPeriod();
		
		if(getFilter().getGrid() != null && getFilter().getGrid().isReport() && isMeasure) {
			String functions = MeasureFunctions.SUM.code;
			col = col + "_" + functions;
		}
		
//		if(isMeasure && grilleFilter.getGrid().isReport() && !grilleFilter.getGrid().isReconciliation()) {
//			String functions = !StringUtils.isBlank(columnFilter.column.getFunctions()) ? columnFilter.column.getFunctions() : Functions.SUM.code;
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
}

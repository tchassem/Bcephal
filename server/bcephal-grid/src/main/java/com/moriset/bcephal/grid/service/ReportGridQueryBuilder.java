/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.ReconciliationParameterCodes;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.MeasureFunctions;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleRowType;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.repository.ParameterRepository;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ReportGridQueryBuilder extends InputGridQueryBuilder {

	protected int measureColumnCount;
	protected GrilleColumn measureColumn;
	protected boolean addOidOfEachRow = true;
		
	protected ParameterRepository parameterRepository;
		
	
	public ReportGridQueryBuilder(GrilleDataFilter filter) {
		super(filter);
		measureColumnCount = 0;
		measureColumn = null;
		
		setSourceType(null);
		setSourceId(null);
	}
	
	
	protected boolean isReport() {
		return getFilter() != null && getFilter().getGrid() != null &&getFilter().getGrid().isReport() && getFilter().getGrid().isConsolidated();
	}

	
	@Override
	public String buildCountQuery() {
		addOidOfEachRow = !getFilter().getGrid().isConsolidated();
		attributes = new HashMap<>();
		measures = new HashMap<>();
		periods = new HashMap<>();
		String table = "A";
		String sql = buildSelectPart();
		if(sql == null) {
			return null;
		}
		boolean isReport = isReport();
		String where = buildWherePart();
		String whereOperationForFilter = buildWherePartOperationForFilter();
		String from = buildFromPart(table);		
		String group = isReport ? buildGroupByPart(getFilter().getGrid().getColumns(), table) : "";
						
		sql += from + " " + where + " " + group;
		//sql = "SELECT COUNT(1) FROM (SELECT * FROM (" + sql + ") AS AA) AS AAA "+ whereOperationForFilter;/**/
		
		if(isReport) {
			if(org.springframework.util.StringUtils.hasText(whereOperationForFilter)) {
				sql = "SELECT COUNT(1) FROM (SELECT * FROM (" + sql + ") AS AA) AS AAA "+ whereOperationForFilter;
			}
			else {
				sql = "SELECT COUNT(1) FROM (" + sql + ") AS AA ";
			}
		}
		else {
			sql = "SELECT COUNT(1) FROM (" + sql + ") AS AA " + whereOperationForFilter;
		}
		
		return sql;
	}
	
	/**
	 * Build query
	 * @param filter
	 * @return
	 */
	@Override
	public String buildQuery() {	
		addOidOfEachRow = !getFilter().getGrid().isConsolidated();
		attributes = new HashMap<>();
		measures = new HashMap<>();
		periods = new HashMap<>();
		String table = "A";
		String sql = buildSelectPart();
		if(sql == null) {
			return null;
		}
		
		boolean isReport = isReport();
		String where = buildWherePart();
		String whereOperationForFilter = buildWherePartOperationForFilter();
		String from = buildFromPart(table);		
		String group = isReport ? buildGroupByPart(getFilter().getGrid().getColumns(), table) : "";
		//sql += from + " " + where + " " + group;
		
		if(isReport) {
			if(org.springframework.util.StringUtils.hasText(whereOperationForFilter)) {
				sql = buildCustomForReport(sql + from  + where + " " + group, whereOperationForFilter);
			}
			else {				
				if(addOidOfEachRow || !getFilter().getGrid().isConsolidated()) {
					String col = table.concat(".").concat(UniverseParameters.ID);
					sql += "," + col;
					group += group.length() > 0 ? ", " + col : "";
				}
				else{
					sql +=", row_number() OVER() as id";
				}
				sql += from + " " + where + " " + group;
				String order = buildOrderPart(table);
				if (order.isEmpty() && isContainsId(sql)){
					order = " ORDER BY ID DESC";
				}
				else {
					sql = sql + " " + order;
				}
			}
		}
		else {
			sql += from + " " + where + " " + group;
			String order = buildOrderPart(table);
			if(order != null) {
				if (order.isEmpty() && isContainsId(sql)){
					order = " ORDER BY ID DESC";
				}
				if(org.springframework.util.StringUtils.hasText(whereOperationForFilter)) {
					sql = "SELECT * FROM ( "+ sql + " " + order + " ) as AAA "+ whereOperationForFilter;
				}
				else {
					sql = sql + " " + order;
				}
			}
			//sql = "SELECT * FROM ( "+ sql + " " + order + " ) as AAA "+ whereOperationForFilter;
			//sql = sql + " " + order + "  " + whereOperationForFilter;
		}
		
		return sql;
	}
		
	@Override
	protected String getColumnNameForDetails(GrilleColumn column) {
		String col = column.getUniverseTableColumnName();
		if (column.isMeasure() && column.getDataSourceType() != DataSourceType.MATERIALIZED_GRID) {
			String functions = StringUtils.hasText(column.getDimensionFunction()) ? column.getDimensionFunction() : MeasureFunctions.SUM.code;
			col = col.concat("_").concat(functions);			
		}
		return col;
	}
	
	protected boolean isContainsId(String sql) {
		String query = sql.toLowerCase();
		return query.contains(", id ") || query.contains(" id ,") 
				|| query.contains(",id ")|| query.contains(",id,")|| query.contains(" id,");
	}
	
	protected String buildCustomForReport(String sql, String whereOperationForFilter) {
		String selectPart = "SELECT ";
		String coma = "";
		String groupByPart = "";
		String groupBy = "GROUP BY ";
		String table = "AAA";
		for (GrilleColumn column : getFilter().getGrid().getColumns()) {
			String col = column.getUniverseTableColumnName();
			col = table.concat(".").concat(col);
			if (col == null) continue;
			if (column.isMeasure()) {
				String functions = StringUtils.hasText(column.getDimensionFunction()) ? column.getDimensionFunction() : MeasureFunctions.SUM.code;
				String name = column.getUniverseTableColumnName();
				if (column.getDataSourceType() != DataSourceType.MATERIALIZED_GRID) {
					col = col.concat("_").concat(functions);
					name = column.getUniverseTableColumnName().concat("_").concat(functions);			
				}
//				col = col.concat("_").concat(functions);
//				String name = column.getUniverseTableColumnName().concat("_").concat(functions);
				
				String measurePart = "SUM(".concat(col).concat(") AS ").concat(name);
				selectPart += coma + measurePart;
			}
			else {
				selectPart += coma + col;
				groupByPart += groupBy + col;
				groupBy = ", ";
			}
			coma = ", ";
		}
		String order = buildOrderPart(table);
		
		if(addOidOfEachRow) {
			String col = table.concat(".").concat(UniverseParameters.ID);
			selectPart += coma + col;
			groupByPart += groupBy + col;
		}
		else{
			selectPart += coma + "row_number() OVER()";
		}
		
		sql = selectPart + " FROM ( "+ sql +" ) as " + table + " " + whereOperationForFilter + " " + groupByPart  + " " + order;
		return sql;
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
		for (GrilleColumn column : getFilter().getGrid().getColumns()) {
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
	
	protected String buildMeasureColumSelectPart(GrilleColumn column, String col) {
		String functions = StringUtils.hasText(column.getDimensionFunction()) ? column.getDimensionFunction() : MeasureFunctions.SUM.code;
		boolean isCount = functions.toUpperCase().equals(MeasureFunctions.COUNT.code.toUpperCase());
		
		String name = col;
		if (column.getDataSourceType() != DataSourceType.MATERIALIZED_GRID) {
			name = col.concat("_").concat(functions);			
		}
		functions = functions.toUpperCase().equals(MeasureFunctions.AVERAGE.name().toUpperCase()) ? MeasureFunctions.AVERAGE.code : functions;
		column.setAlias(name);
//		if(column != null && column.isUseDebitCredit()) {
//			if(!this.isDebitCreditInitialized){
//				initCreditDebit();
//			}
//			if(StringUtils.hasText(this.debitCreditColumn) && StringUtils.hasText(this.debitValue)){
//				String sql = "SUM(CASE WHEN ".concat(this.debitCreditColumn).concat(" = '").concat(this.debitValue)
//						.concat("' THEN ").concat("-" + col)
//						.concat(" WHEN ").concat(this.debitCreditColumn).concat(" = '").concat(this.creditValue)
//						.concat("' THEN ").concat(col)
//				.concat(" ELSE ").concat("0").concat(" END) AS ").concat(name);
//				
//				return sql;
//			}			
//		}
		
		if(isCount){
			col = !StringUtils.hasText(col) ? "1" : col;
			return functions.concat("(").concat(col).concat(") AS ").concat(name);
		}
		else {
			return functions.concat("(").concat(col).concat(") AS ").concat(name);
		}
	}
	
	
	
	protected String buildPeriodColumSelectPart(GrilleColumn column, String col) {
		column.setAlias(col);
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

	protected String buildFromPart(String table) {
		if(getFilter().getGrid().isUseLink()){
			return " FROM (" + buildUniverseTableWithLinks() + ") " + table;
		}
		else {
			return " FROM " + getTableName() + " " + table;
		}
	}
	
	protected String buildOrderPart(String table) {
		String sql = "";
		String coma = "";
		List<GrilleColumn> columns = getFilter().getGrid().getColumns();
		boolean isReport = isReport();
		for (GrilleColumn column : columns) {
			if (column.getOrderAsc() != null) {
				boolean hasAlias = org.springframework.util.StringUtils.hasText(column.getAlias());
				String col = hasAlias ? column.getAlias() : column.getUniverseTableColumnName();
				String o = column.getOrderAsc() ? " ASC" : " DESC";				
				if (column.isMeasure() && isReport) {
					String functions = StringUtils.hasText(column.getDimensionFunction()) ? column.getDimensionFunction() : MeasureFunctions.SUM.code;
					col = col.concat("_").concat(functions);
					sql += coma + col + o;
				}				
				else {
					if(hasAlias) {
						sql += coma + col + o;
					}
					else {
						sql += coma + table + "." + col + o;
					}
				}
				coma = ", ";
			}
		}
		
		if (!sql.isEmpty()){
			sql = " ORDER BY " + sql;
		}
		
		
		return sql;
	}
	
	protected String buildGroupByPart(List<GrilleColumn> columns, String table) {		
		String groupBy = "";
		String coma = "";
		String var = StringUtils.hasText(table) ? table + "." : "";
//		boolean addDebitCredit = StringUtils.hasText(this.debitCreditColumn) && StringUtils.hasText(this.debitValue);
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
					String col = hasAlias ? column.getAlias() : var + column.getUniverseTableColumnName();
	//				if(addDebitCredit && col.equals(this.debitCreditColumn)){
	//					addDebitCredit = false;
	//				}			
					groupBy += coma + col;
					coma = ", ";
				}
			}
		}
//		if(addDebitCredit && StringUtils.hasText(this.debitCreditColumn) && StringUtils.hasText(this.debitValue)) {
//			groupBy += coma + table + "." + this.debitCreditColumn;
//			coma = ", ";
//		}	
		if(addOidOfEachRow || !getFilter().getGrid().isConsolidated()) {
			groupBy += coma + var + UniverseParameters.ID;
			coma = ", ";
		}
		if(!groupBy.isEmpty()) {
			groupBy = " GROUP BY " + groupBy;
		}		
		return groupBy;
	}
	
	@Override
	protected String buildWherePart() {
		boolean checkIsReady = (getFilter().getGrid() != null && getFilter().getGrid().getDataSourceType() != null && !getFilter().getGrid().getDataSourceType().isMaterializedGrid())
				|| (getFilter().getDataSourceType() != null && !getFilter().getDataSourceType().isMaterializedGrid());
		return buildWherePart(checkIsReady);
	}
	
	
	protected String buildWherePart(boolean checkIsReady) {		
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
		
//		if (getSourceType() != null) {
			
//			sql += where + UniverseParameters.SOURCE_TYPE + " = '" + getSourceType() + "'";
//			where = " AND ";
//			if(getSourceId() != null) {
//				sql += where + UniverseParameters.SOURCE_ID + " = " + getSourceId();
//			}
//			where = " AND ";
//		}
		
		boolean isReport = isReport();
		if(isReport && this.measureColumnCount == 1 && this.measureColumn != null) {
			String functions = StringUtils.hasText(this.measureColumn.getDimensionFunction()) ? this.measureColumn.getDimensionFunction() : MeasureFunctions.SUM.code;
			@SuppressWarnings("unused")
			String name = this.measureColumn.getUniverseTableColumnName().concat("_").concat(functions);
			name = this.measureColumn.getUniverseTableColumnName();
			//sql += where + " " + name + " <> 0";
		}
		
		if(getFilter().getGrid() == null || !getFilter().getGrid().isReport()) {
			
			String status = buildWhereStatusPart();
			if (StringUtils.hasText(status)) {
				sql += where + status;
				where = " AND ";
			}					
//				String col = new Attribute(getFilter().getBillingStatusAttributeId()).getUniverseTableColumnName();
//				String value = getFilter().getRowType() == GrilleRowType.BILLED ? getFilter().getBillingStatusBilledValue() : getFilter().getBillingStatusDraftValue();
//				if (StringUtils.hasText(value)) {
//					sql += where + "(UPPER(" + col + ") = '" + value.toUpperCase() + "')";
//					where = " AND ";
//				}
//				this.attributes.put(getFilter().getRecoAttributeId(), new Attribute(getFilter().getRecoAttributeId()));	
			
			
		
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
				if(getFilter().getNeutralizationAttributeId() != null && getFilter().getRecoData().isAllowNeutralization()) {
					col = new Attribute(getFilter().getNeutralizationAttributeId()).getUniverseTableColumnName();
					sql += where + "(" + col + " IS NULL OR " + col + " = '')";
					where = " AND ";
				}
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
			
			if(getFilter().getData() != null && getFilter().getRecoData().isUseDebitCredit() && getFilter().isCredit() || getFilter().isDebit()) {
				if(getFilter().getDebitCreditAttribute() == null) {
					if(getFilter().getRecoData().getDebitCreditAttributeId() != null) {
						getFilter().setDebitCreditAttribute(new Attribute(getFilter().getRecoData().getDebitCreditAttributeId()));
					}
					else {
						Parameter parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_DC_ATTRIBUTE, ParameterType.ATTRIBUTE);
						if(parameter != null && parameter.getLongValue() != null) {
							 getFilter().setDebitCreditAttribute(new Attribute(parameter.getLongValue()));
						}
					}					
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
				
			if((getFilter().getIds() != null && getFilter().getIds().size() > 0) || (getFilter().getRecoValues() != null && !getFilter().getRecoValues().isEmpty())){
				if(getFilter().isConterpart()) {
					if(getFilter().getRecoAttributeId() != null) {
						if(getFilter().getRecoValues() != null && !getFilter().getRecoValues().isEmpty()) {
							String col = new Attribute(getFilter().getRecoAttributeId()).getUniverseTableColumnName();
							String part = col + " IN (";
							String coma = "";
							for(String value : getFilter().getRecoValues()){
								part += coma  + "'" + value + "'";
								coma = ", ";
							}
							part += ")";
							sql += where + part;
							where = " AND ";
						}
						else {
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
				}
				else if(getFilter().getIds() != null && getFilter().getIds().size() > 0){
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

		if (getFilter().getGrid() != null && getFilter().getGrid().getUserFilter() != null && !ignoreUserFilter) {
			String userFilterSql = buildUniverseFilterWherePart(getFilter().getGrid().getUserFilter());
			if (StringUtils.hasText(userFilterSql)) {
				sql += where + "(" + userFilterSql + ")";
				where = " AND ";
			}
		}
		if (getFilter().getGrid() != null && getFilter().getGrid().getAdminFilter() != null) {
			String adminFilterSql = buildUniverseFilterWherePart(getFilter().getGrid().getAdminFilter());
			if (StringUtils.hasText(adminFilterSql)) {
				sql += where + "(" + adminFilterSql + ")";
				where = " AND ";
			}
		}
		
		if (getFilter().getGrid() != null && getFilter().getGrid().getGridUserFilter() != null) {
			String userFilterSql = buildUniverseFilterWherePart(getFilter().getGrid().getGridUserFilter());
			if (StringUtils.hasText(userFilterSql)) {
				sql += where + "(" + userFilterSql + ")";
				where = " AND ";
			}
		}
		if (getFilter().getGrid() != null && getFilter().getGrid().getGridAdminFilter() != null) {
			String adminFilterSql = buildUniverseFilterWherePart(getFilter().getGrid().getGridAdminFilter());
			if (StringUtils.hasText(adminFilterSql)) {
				sql += where + "(" + adminFilterSql + ")";
				where = " AND ";
			}
		}
		
		if (getFilter().getFilter() != null) {
			String filterSql = buildUniverseFilterWherePart(getFilter().getFilter());
			if (StringUtils.hasText(filterSql)) {
				sql += where + "(" + filterSql + ")";
				where = " AND ";
			}
		}
		
		
		return sql;
	}
	
	
	protected String buildWhereSourcePart() {
		String sql = "";
		if (getSourceType() != null) {
			sql += UniverseParameters.SOURCE_TYPE + " = '" + getSourceType() + "'";
			if(getSourceId() != null) {
				sql += " AND " + UniverseParameters.SOURCE_ID + " = " + getSourceId();
			}
		}
		return sql;
	}
			
	
	protected String buildWhereStatusPart() {		
		String sql = "";
		if(getFilter().getRowType() != null && getFilter().getBillingStatusAttributeId()!= null 
				&& getFilter().getRowType() != GrilleRowType.ALL
				&& (getFilter().getGrid().getType() == GrilleType.INVOICE_REPOSITORY 
				|| getFilter().getGrid().getType() == GrilleType.CREDIT_NOTE_REPOSITORY
				|| getFilter().getGrid().getType() == GrilleType.BILLING_EVENT_REPOSITORY )) {
			String col = new Attribute(getFilter().getBillingStatusAttributeId()).getUniverseTableColumnName();
			String value = getFilter().getRowType() == GrilleRowType.BILLED ? getFilter().getBillingStatusBilledValue() : getFilter().getBillingStatusDraftValue();
			if (StringUtils.hasText(value)) {
				sql += "(UPPER(" + col + ") = '" + value.toUpperCase() + "')";
			}
			this.attributes.put(getFilter().getRecoAttributeId(), new Attribute(getFilter().getRecoAttributeId()));	
		}
		return sql;
	}
	
	
	protected String buildWherePartOperationForFilter() {		
		String sql = "";
		String where = " WHERE ";
		String filterOperator = "";
		if(getFilter().getColumnFilters() != null){			
			String query = getSqlOperationForFilter(getFilter().getColumnFilters(), getFilter());
			if (StringUtils.hasText(query)) {
				sql += where + " "+ filterOperator + " (" + query + ")";
				where = " AND ";
			}
		}	
		return sql;
	}
	
	
	protected String buildUniverseTableWithLinks(){
		List<Attribute> attributes = new ArrayList<Attribute>(this.attributes.values());
		Collection<Measure> measures = this.measures.values();
		Collection<Period> periods = this.periods.values();
		
		String table = "U";
		String coma = ", ";		
		String sql = "SELECT " + table + "." + UniverseParameters.ID + " AS " + UniverseParameters.ID + ", " 
				+ table + "." + UniverseParameters.ISREADY + " AS " + UniverseParameters.ISREADY + ", " 
				+ table + "." + UniverseParameters.SOURCE_TYPE + " AS " + UniverseParameters.SOURCE_TYPE;	
		
		
		for(Attribute attribute : attributes){
			String col = attribute.getUniverseTableColumnName();
			sql += coma + buildLinkedScope(attribute, table) + " AS " + col;
			coma = ", ";
		}
		for(Measure measure : measures){
			String col = measure.getUniverseTableColumnName();
			sql += coma + table + "." + col + " AS " + col;
			coma = ", ";
		}
		for(Period period : periods){
			String col = period.getUniverseTableColumnName();
			sql += coma + table + "." + col + " AS " + col;
			coma = ", ";
		}
		sql += " FROM " + UniverseParameters.SCHEMA_NAME + UniverseParameters.UNIVERSE_TABLE_NAME + " " + table;
			
		sql += " WHERE " + table + "." + UniverseParameters.ISREADY +  " = true";
				
		return sql;
	}
	
	protected String buildLinkedScope(Attribute attribute, String table){
		String col = attribute.getUniverseTableColumnName();
		return table + "." + col;
	}
	

	
}

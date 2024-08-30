package com.moriset.bcephal.grid.service;

import java.util.HashMap;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.ReconciliationParameterCodes;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.grid.domain.GrilleRowType;
import com.moriset.bcephal.grid.domain.JoinColumn;
import com.moriset.bcephal.service.filters.ISpotService;

public class JoinRecoQueryBuilder extends JoinQueryBuilder {
	
	

	public JoinRecoQueryBuilder(JoinFilter filter,
			com.moriset.bcephal.grid.repository.JoinColumnRepository JoinColumnRepository, ISpotService spotService) {
		super(filter, JoinColumnRepository, spotService);
		addMainGridOid = true;
	}
	
	
	@Override
	public String buildCountQuery() throws Exception {
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
			
			String recoWherePart = buildRecoWherePart();
			if (StringUtils.hasText(recoWherePart)) {
				return "SELECT COUNT(1) FROM (" + sql + ") AS items" + recoWherePart;
			}
			
			return "SELECT COUNT(1) FROM (" + sql + ") AS items";
		}
		return null;
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
			
			String recoWherePart = buildRecoWherePart();
			if (StringUtils.hasText(recoWherePart)) {
				sql = "SELECT * FROM (" + sql + ") AS items" + recoWherePart;
			}

			String orderBy = buildOrderPart();
			if (StringUtils.hasText(orderBy)) {
				sql += orderBy;
			}
		}		
		return sql;
	}
	
	@Override
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
			if (!column.isCustom()) {
				sql = " ORDER BY " + column.getDbColAliasName();
			}
		}
		return sql;
	}

	@Override
	protected String buildWherePart() throws Exception {
		String sql = super.buildWherePart();
		String where = " WHERE ";
		if (StringUtils.hasText(sql)) {
			where = " AND ";
		}		
//		if(getFilter().isCredit() || getFilter().isDebit()) {
//			if(this.filter.getDebitCreditAttribute() == null) {
//				Parameter parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_DC_ATTRIBUTE, ParameterType.ATTRIBUTE);
//				if(parameter != null && parameter.getLongValue() != null) {
//					 getFilter().setDebitCreditAttribute(new Attribute(parameter.getLongValue()));
//				}
//				parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_DEBIT_VALUE, ParameterType.ATTRIBUTE_VALUE);
//				if(parameter != null && parameter.getStringValue() != null) {
//					 getFilter().setDebitValue(parameter.getStringValue());
//				}
//				else {
//					getFilter().setDebitValue("D");
//				}
//				parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_CREDIT_VALUE, ParameterType.ATTRIBUTE_VALUE);
//				if(parameter != null && parameter.getStringValue() != null) {
//					 getFilter().setCreditValue(parameter.getStringValue());
//				}
//				else {
//					getFilter().setCreditValue("C");
//				}
//			}
//			if(getFilter().getDebitCreditAttribute() != null) {
//				String col = getFilter().getDebitCreditAttribute().getUniverseTableColumnName();									
//				if(getFilter().isCredit() && getFilter().isDebit()){
//					sql += where + "(" + col + " = '" + getFilter().getCreditValue() + "' OR " + col + " = '" + getFilter().getDebitValue() + "')";
//				}
//				else if(getFilter().isCredit()) {
//					sql += where + col + " = '" + getFilter().getCreditValue() + "'";
//				}
//				else if(getFilter().isDebit()) {
//					sql += where + col + " = '" + getFilter().getDebitValue() + "'";
//				}
//				this.attributes.put(getFilter().getDebitCreditAttribute().getId(), getFilter().getDebitCreditAttribute());
//			}
//		}
			
		if(getFilter().getIds() != null && getFilter().getIds().size() > 0){
			if(getFilter().isConterpart()) {
				if(getFilter().getRecoAttributeId() != null) {
//					String part = UniverseParameters.ID + " IN (";
//					String coma = "";
//					for(Long oid : getFilter().getIds()){
//						part += coma + oid;
//						coma = ", ";
//					}
//					part += ")";
					
//					String col = getJoinCol(getFilter().getRecoData().getRecoAttributeId());
//					String col = getJoinCol(getFilter().getl.getRecoData().getRecoAttributeId());
//					String primaryGridName = getMainGridCol(getFilter().getRecoData().getRecoAttributeId(), DimensionType.ATTRIBUTE);
//					String secondaryGridName = getMainGridCol(getFilter().getRecoData().getRecoAttributeId(), DimensionType.ATTRIBUTE);
//					String primaryCol = getMainGridCol(getFilter().getRecoAttributeId(), DimensionType.ATTRIBUTE);
//					String secondaryCol = getMainGridCol(getFilter().getRecoAttributeId(), DimensionType.ATTRIBUTE);
//					sql += where + "(" + primaryCol + " IN (Select distinct "
//							+ secondaryCol + " FROM " + UniverseParameters.UNIVERSE_TABLE_NAME
//							+ " WHERE " + part + "))";
//					where = " AND ";
					
					
//					String primaryGridName = getMainGridCol(getFilter().getRecoData().getRecoAttributeId(), DimensionType.ATTRIBUTE);
//					String secondaryGridName = getMainGridCol(getFilter().getRecoData().getRecoAttributeId(), DimensionType.ATTRIBUTE);
//					String primaryCol = getMainGridCol(getFilter().getRecoAttributeId(), DimensionType.ATTRIBUTE);
//					String secondaryCol = getMainGridCol(getFilter().getRecoAttributeId(), DimensionType.ATTRIBUTE);
//					sql += where + "(" + primaryCol + " IN (Select distinct "
//							+ secondaryCol + " FROM " + UniverseParameters.UNIVERSE_TABLE_NAME
//							+ " WHERE " + part + "))";
//					where = " AND ";
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
		return sql;
	}
	
	protected String buildRecoWherePart() throws Exception {
		String coma = " WHERE ";
		String sql = "";
//		if(getFilter().getRecoData().getRecoAttributeId() == null) {
//			getFilter().getRecoData().setRecoAttributeId(getFilter().getRecoAttributeId());
//		}
		if(getFilter().getRecoData().getRecoAttributeId() != null && getFilter().getRowType() != null 
				&& getFilter().getRowType() != GrilleRowType.ALL && getFilter().getRowType() != GrilleRowType.ON_HOLD) {
			
			String col = getJoinCol(getFilter().getRecoData().getRecoAttributeId());
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
				
				String partialRecocol = getJoinCol(getFilter().getRecoData().getPartialRecoAttributeId());
				String remainningCol = getJoinCol(getFilter().getRecoData().getRemainningMeasureId());
				if (getFilter().getRowType().isPositive()) {
					sql += " AND (" + partialRecocol + " IS NOT NULL AND (" + remainningCol + " = 0))";		
				}
				else {
					sql += " OR (" + partialRecocol + " IS NOT NULL AND (" + remainningCol + " != 0))";		
				}
			}
			sql += ")";
		}	
		
		if(getFilter().getRecoData().getFreezeAttributeId() != null && getFilter().getRowType() != null 
				&& getFilter().getRowType() != GrilleRowType.ALL) {
			String col = getJoinCol(getFilter().getRecoData().getFreezeAttributeId());
			if (getFilter().getRowType() == GrilleRowType.ON_HOLD) {
				sql += coma + "(" + col + " IS NOT NULL AND " + col + " != '')";
				coma = " AND ";
			}
			else {
				sql += coma + "(" + col + " IS NULL OR " + col + " = '')";
				coma = " AND ";
			}
		}	
		

		if(getFilter().getRecoData().getDebitCreditAttributeId() != null && (getFilter().isCredit() || getFilter().isDebit())) {
			if(!StringUtils.hasText(getFilter().getDebitValue())) {
				Parameter parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_DEBIT_VALUE, ParameterType.ATTRIBUTE_VALUE);
				if(parameter != null && parameter.getStringValue() != null) {
					 getFilter().setDebitValue(parameter.getStringValue());
				}
				else {
					getFilter().setDebitValue("D");
				}
			}
			if(!StringUtils.hasText(getFilter().getCreditValue())) {
				Parameter parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_CREDIT_VALUE, ParameterType.ATTRIBUTE_VALUE);
				if(parameter != null && parameter.getStringValue() != null) {
					 getFilter().setCreditValue(parameter.getStringValue());
				}
				else {
					getFilter().setCreditValue("C");
				}
			}
			String col = getJoinCol(getFilter().getRecoData().getDebitCreditAttributeId());									
			if(getFilter().isCredit() && getFilter().isDebit()){
				sql += coma + "(" + col + " = '" + getFilter().getCreditValue() + "' OR " + col + " = '" + getFilter().getDebitValue() + "')";
				coma = " AND ";
			}
			else if(getFilter().isCredit()) {
				sql += coma + col + " = '" + getFilter().getCreditValue() + "'";
				coma = " AND ";
			}
			else if(getFilter().isDebit()) {
				sql += coma + col + " = '" + getFilter().getDebitValue() + "'";
				coma = " AND ";
			}
			
		}
		
		return sql;
	}
	
	
	protected String getJoinCol(Long columnOid) {
		return new JoinColumn(columnOid).getDbColAliasName();
	}
	
//	protected String getMainGridCol(Long columnOid, DimensionType dimensionType) {
//		JoinGrid mainGrid = getFilter().getJoin().getMainGrid();	
//		JoinColumn column = getFilter().getJoin().getColumn(columnOid, dimensionType, mainGrid.getGridType());
//		return column.getDbColAliasName();
//	}
	
}

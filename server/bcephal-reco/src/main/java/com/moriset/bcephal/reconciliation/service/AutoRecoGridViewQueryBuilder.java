/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import java.math.BigDecimal;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.reconciliation.domain.AutoReco;
import com.moriset.bcephal.reconciliation.domain.ReconciliationCondition;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModelSide;

import jakarta.persistence.EntityManager;


/**
 * @author Joseph Wambo
 *
 */
public class AutoRecoGridViewQueryBuilder  extends AutoRecoGridQueryBuilder {
	
	String viewName;
	
	/**
	 * @param userSession
	 */
	public AutoRecoGridViewQueryBuilder(AutoReco reco, boolean forSecondaryGrid, EntityManager entityManager, String viewName) {
		super(reco, forSecondaryGrid, entityManager);
		this.viewName = viewName;
	}
	
	public AutoRecoGridViewQueryBuilder(AutoReco reco, boolean forSecondaryGrid, EntityManager entityManager, String viewName, ReconciliationModelSide side, boolean forPartial) {
		super(reco, forSecondaryGrid, entityManager, side, forPartial);
		this.viewName = viewName;
	}
	
	
	
	/**
	 * Build query
	 * @param filter
	 * @return
	 */
	@Override
	public String buildQuery() {	
		String table = viewName;
		String sql = buildSelectPart();
		if(sql == null) {
			return null;
		}
		String from = buildFromPart(table);
		String where = buildWherePart();
		sql += from + " " + where;			
		return sql;
	}
	
	
	
	
	@Override
	protected String buildSelectPart() {
		String selectPart = "SELECT DISTINCT " + UniverseParameters.ID;
		if(measure != null) {
			selectPart += ", " + measure.getUniverseTableColumnName();
			this.measures.put(measure.getId(), measure);
		}	
		if(measure2 != null) {
			selectPart += ", " + measure2.getUniverseTableColumnName();
			this.measures.put(measure2.getId(), measure);
		}	
		if(useCreditDebit) {
			selectPart += ", DC AS DC";
		}
		return selectPart;
	}
	
	@Override
	protected String buildFromPart(String table) {
		return " FROM " + UniverseParameters.SCHEMA_NAME + table;
	}
	
	@Override
	protected String buildWherePart() {	
		String sql = ""; 	
		String where = " WHERE ";
		if(StringUtils.hasText(sql)) {
			where = " AND ";
		}	
		if(forSecondaryGrid) {
			if(measure != null) {
				String measureSql = buildMeasureWherePart();
				if(StringUtils.hasText(measureSql)) {
					sql += where + measureSql;
					where = " AND ";
				}
			}			
			String commonAttributeSql = buildCommonAttributeSqlWherePart();
			if(StringUtils.hasText(commonAttributeSql)) {
				sql += where + commonAttributeSql;
				where = " AND ";
			}
		}
		if(excludedOids != null && !excludedOids.isEmpty()) {
			String oids = "(";
			String coma = "";
			for (Long oid : excludedOids) {
				oids += coma + oid;
				coma = ",";
			}
			oids += ")";
			sql += where + UniverseParameters.ID + " NOT IN " + oids;
		}
		return sql;
	}
	
	private String buildCommonAttributeSqlWherePart() {
		String sql = "";
		for(ReconciliationCondition item : reco.getSortedConditions()) {
			String s = item.getSql(item.getSecondaryDbColumn(), item.getValue(), item.getDimensionType());
			if(StringUtils.hasText(s)) {
				boolean isFirst = item.getPosition() == 0;
				String verb = org.springframework.util.StringUtils.hasText(item.getVerb()) ? item.getVerb(): "AND";
				
				if(FilterVerb.ANDNO.name().equals(verb)) {
					verb = "AND_NOT";
				}
				else if(FilterVerb.ORNO.name().equals(verb)) {
					verb = "OR_NOT";
				}
				else if(isFirst){
					verb = "";
				}
				if("AND_NOT".equals(verb) || "OR_NOT".equals(verb)) {
					verb = isFirst ? "NOT" : verb.replace("_", " ");
					s = "(" + s + ")";
				}
				if(StringUtils.hasText(verb)) {
					sql += " " + verb + " " ;
				}				
				sql += s;
			}
		}
		if(StringUtils.hasText(sql)) {
			sql = "(" + sql + ")";
		}		
		return sql;
	}

	private String buildMeasureWherePart() {
		String sql = null;	
		String col = measure.getUniverseTableColumnName();
		if(amount1 == null || amount1.compareTo(BigDecimal.ZERO) == 0) {
			sql = "(" + col + " IS NULL OR " + col + " = 0)";				
		}
		else {
			if(forCumulatedBalance) {
				if(useCreditDebit) {
					sql = "";
				}
				else {
					if(amount1.compareTo(amount2) == 0) {
//						String sign = amount1.compareTo(BigDecimal.ZERO) >= 0 ? " <= " : " >= ";					
//						sql = col + " = " + amount1;
					}
					else {
						
					}
				}
			}
			else{
				if(useCreditDebit) {
					String cdColumn = "DC";
					if(amount1.equals(amount2)) {
						BigDecimal opositeAmount1 = BigDecimal.ZERO.subtract(amount1);
						sql = "((" + cdColumn + " = '" + cdValue +"' AND " + col + " = " + amount1 + ")"
								+ " OR (" + cdColumn + " = '" + opositecdValue +"' AND " + col + " = " + opositeAmount1 + "))";
					}
					else {
						BigDecimal opositeAmount1 = BigDecimal.ZERO.subtract(amount1);
						BigDecimal opositeAmount2 = BigDecimal.ZERO.subtract(amount2);
						sql = "((" + cdColumn + " = '" + cdValue +"' AND " + col + " >= " + amount1 + " AND " + col + " <= " + amount2 + ")"
								+ " OR (" + cdColumn + " = '" + opositecdValue +"' AND " + col + " >= " + opositeAmount2 + " AND " + col + " <= " + opositeAmount1 + "))";
					}
				}
				else {
					if(amount1.compareTo(amount2) == 0) {
						sql = col + " = " + amount1;
					}
					else {
						sql = "(" + col + " >= " + amount1 + " AND " + col + " <= " + amount2 + ")";
					}
				}
			}
		}		
		return sql;
	}

	@Override
	protected String buildOrderPart(String table) {
		String sql = super.buildOrderPart(table);
		String coma = "";
		if(StringUtils.hasText(sql)) {
			coma = ", ";
		}
		if (sql.isEmpty()){
			sql = " ORDER BY " + sql;
		}
		sql += coma + table + ".ID ASC";		
		return sql;
	}

	
}

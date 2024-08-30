/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.service.ReportGridQueryBuilder;
import com.moriset.bcephal.reconciliation.domain.AutoReco;
import com.moriset.bcephal.reconciliation.domain.AutoRecoRankingItem;
import com.moriset.bcephal.reconciliation.domain.ReconciliationCondition;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModelSide;

import jakarta.persistence.EntityManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class AutoRecoGridQueryBuilder extends ReportGridQueryBuilder {

	EntityManager entityManager;
	
	boolean forZeroAount;
	boolean forSecondaryGrid;
	boolean forCumulatedBalance;
	
	Measure measure;
	Measure measure2;
	BigDecimal amount1;
	BigDecimal amount2;
	
	List<Long> excludedOids;
	
	AutoReco reco;
	
	Map<String, Object> commonAttributeValuesMap;
	
	
	String creditValue;
	String debitValue;
	String cdColumn;
	Attribute cdAttribute;
	String cdValue;
	String opositecdValue;
	boolean useCreditDebit;
	
	ReconciliationModelSide side;
	
	public AutoRecoGridQueryBuilder(GrilleDataFilter filter) {
		super(filter);
	}
	
	public AutoRecoGridQueryBuilder(AutoReco reco, boolean forSecondaryGrid, EntityManager entityManager) {
		this(null);
		this.excludedOids = new ArrayList<Long>(0);
		this.reco = reco;
		this.forSecondaryGrid = forSecondaryGrid;
		this.entityManager = entityManager;
		if(this.forSecondaryGrid) {
			this.measure = new Measure(this.reco.getSecondaryMeasureId());
		}
		else {
			this.measure = new Measure(this.reco.getPrimaryMeasureId());
		}
	}
	
	public AutoRecoGridQueryBuilder(AutoReco reco, boolean forSecondaryGrid, EntityManager entityManager, ReconciliationModelSide side, boolean forPartial) {
		this(reco, forSecondaryGrid, entityManager);
		this.side = side;
		if(forPartial) {
			this.measure = new Measure(this.reco.getModel().getRemainningMeasureId());
			this.measure2 = new Measure(this.reco.getModel().getReconciliatedMeasureId());
		}
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
			this.measures.put(measure2.getId(), measure2);
		}
		if(!forSecondaryGrid) {
			ReconciliationModelSide side = null;
			if(getFilter().getGrid().getId() == reco.getPrimaryGrid().getId()) {
				if(reco.isLeftGridPrimary()) {
					side = ReconciliationModelSide.LEFT;
				}
				else {
					side = ReconciliationModelSide.RIGHT;
				}
			}
			else if(getFilter().getGrid().getId() == reco.getSecondaryGrid().getId()) {
				if(reco.isLeftGridPrimary()) {
					side = ReconciliationModelSide.RIGHT;
				}
				else {
					side = ReconciliationModelSide.LEFT;
				}
			}
			
			int arg = 1;
			for(GrilleColumn column : reco.getConditionColumns(side)) {
				if (column.getType().isAttribute() && column.getDimensionId() != null) {
					Attribute attribute = new Attribute(column.getDimensionId());
					boolean cont = this.attributes.containsKey(attribute.getId());					
					String col = attribute.getUniverseTableColumnName();
					selectPart += ", " + col + (cont ? (" AS " + col + "_" + arg++) : "");
					this.attributes.put(attribute.getId(), attribute);
				}
				if (column.getType().isPeriod()) {
					Period period = new Period(column.getDimensionId());
					boolean cont = this.periods.containsKey(period.getId());						
					String col = period.getUniverseTableColumnName();
					selectPart += ", " + col + (cont ? (" AS " + col + "_" + arg++) : "");
					this.periods.put(period.getId(), period);
				}
			}
		}
		if(useCreditDebit) {
			selectPart += ", " + cdColumn + " AS DC";
			this.attributes.put(cdAttribute.getId(), cdAttribute);
		}		
		
		for(AutoRecoRankingItem item : this.reco.getSortedRankingItems()) {
			if(this.side == item.getSide() && item.getDimensionId() != null) {
				if (item.isAttribute() && !this.attributes.containsKey(item.getDimensionId())) {
					Attribute dimension = new Attribute(item.getDimensionId());
					this.attributes.put(dimension.getId(), dimension);
					String col = dimension.getUniverseTableColumnName();
					selectPart += ", " + col;
				}
				else if (item.isMeasure() && !this.measures.containsKey(item.getDimensionId())) {
					Measure dimension = new Measure(item.getDimensionId());
					this.measures.put(dimension.getId(), dimension);
					String col = dimension.getUniverseTableColumnName();
					selectPart += ", " + col;
				}
				else if (item.isPeriod() && item.getDimensionId() != null) {
					Period dimension = new Period(item.getDimensionId());
					this.periods.put(dimension.getId(), dimension);
					String col = dimension.getUniverseTableColumnName();
					selectPart += ", " + col;
				}
			}
		}
		
		return selectPart;
	}
	
	
	@Override
	protected String buildWherePart() {	
		String sql = super.buildWherePart();		
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
				sql += s;
			}
		}
		return sql;
	}

	private String buildMeasureWherePart() {
		String sql = null;	
		String col = measure.getUniverseTableColumnName();
		if(amount1 == null || (amount1 == BigDecimal.ZERO && (amount2 == null || amount2 == BigDecimal.ZERO))) {
			sql = "(" + col + " IS NULL OR " + col + " = 0)";				
		}
		else {
			if(forCumulatedBalance) {
				if(useCreditDebit) {
					sql = "";
				}
				else {
					if(amount1.equals(amount2)) {
//						String sign = amount1.compareTo(BigDecimal.ZERO) >= 0 ? " <= " : " >= ";					
//						sql = col + " = " + amount1;
					}
					else {
						
					}
				}
			}
			else{
				if(useCreditDebit) {
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
					this.attributes.put(cdAttribute.getId(), cdAttribute);
				}
				else {
					if(amount1.equals(amount2)) {
						sql = col + " = " + amount1;
					}
					else {
						sql = "(" + col + " >= " + amount1 + " AND " + col + " <= " + amount2 + ")";
					}
				}
			}
		}		
		this.measures.put(measure.getId(), measure);
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

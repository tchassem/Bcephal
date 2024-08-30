/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.reconciliation.domain.AutoReco;
import com.moriset.bcephal.reconciliation.domain.AutoRecoRankingItem;
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
public class AutoRecoGridForUnionQueryBuilder extends AutoRecoGridQueryBuilder {
		
	public AutoRecoGridForUnionQueryBuilder(GrilleDataFilter filter) {
		super(filter);
	}
	
	public AutoRecoGridForUnionQueryBuilder(AutoReco reco, boolean forSecondaryGrid, EntityManager entityManager) {
		super(reco, forSecondaryGrid, entityManager);
	}
	
	public AutoRecoGridForUnionQueryBuilder(AutoReco reco, boolean forSecondaryGrid, EntityManager entityManager, ReconciliationModelSide side, boolean forPartial) {
		super(reco, forSecondaryGrid, entityManager, side, forPartial);
	}
	
	@Override
	protected String buildSelectPart() {
		String selectPart = "SELECT DISTINCT " + UniverseParameters.ID + "::TEXT AS id";
		if(measure != null) {
			selectPart += ", " + measure.getUniverseTableColumnName() + " as measure";
			this.measures.put(measure.getId(), measure);
		}
		if(measure2 != null) {
			selectPart += ", " + measure2.getUniverseTableColumnName() + " as measure2";
			this.measures.put(measure2.getId(), measure2);
		}
		if(!forSecondaryGrid) {
			ReconciliationModelSide side = null;
			if(getFilter().getGrid().getId() == reco.getPrimaryUnionModelGrid().getGridId()) {
				if(reco.isLeftGridPrimary()) {
					side = ReconciliationModelSide.LEFT;
				}
				else {
					side = ReconciliationModelSide.RIGHT;
				}
			}
			else if(getFilter().getGrid().getId() == reco.getSecondaryUnionModelGrid().getGridId()) {
				if(reco.isLeftGridPrimary()) {
					side = ReconciliationModelSide.RIGHT;
				}
				else {
					side = ReconciliationModelSide.LEFT;
				}
			}
			
			int arg = 1;
			for(GrilleColumn column : reco.getConditionColumnsForUnion(side)) {
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
			GrilleColumn column = getFilter().getGrid().getColumnById(cdAttribute.getId());
			selectPart += ", " + column.getUniverseTableColumnName() + " AS DC";
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
	protected String buildOrderPart(String table) {
		return null;
	}
	
	
}

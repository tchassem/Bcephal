/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.SmartMaterializedGridColumn;
import com.moriset.bcephal.grid.domain.UnionGridColumn;
import com.moriset.bcephal.grid.domain.UnionGridItem;
import com.moriset.bcephal.grid.service.UnionGridFilter;
import com.moriset.bcephal.grid.service.UnionGridQueryBuilder;
import com.moriset.bcephal.reconciliation.domain.AutoReco;
import com.moriset.bcephal.reconciliation.domain.AutoRecoRankingItem;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModelSide;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionModelGrid;

import jakarta.persistence.EntityManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class AutoRecoUnionQueryBuilder extends UnionGridQueryBuilder {

	protected GrilleDataFilter gridDataFilter;
	AutoRecoGridQueryBuilder gridBuilder;
	
	EntityManager entityManager;
	
	boolean forZeroAount;
	boolean forSecondaryGrid;
	boolean forCumulatedBalance;
	
	Measure measure;
	Measure measure2;
	BigDecimal amount1;
	BigDecimal amount2;
	
	List<String> excludedOids;
	
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
		
	public AutoRecoUnionQueryBuilder(AutoReco reco, boolean forSecondaryGrid, EntityManager entityManager) {
		super(null, null, null);
		this.setAddMainGridOid(true);
		this.excludedOids = new ArrayList<>(0);
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
	
	public AutoRecoUnionQueryBuilder(AutoReco reco, boolean forSecondaryGrid, EntityManager entityManager, ReconciliationModelSide side, boolean forPartial) {
		this(reco, forSecondaryGrid, entityManager);
		this.side = side;
//		if(forPartial) {
//			this.measure = new Measure(this.reco.getModel().getRemainningMeasureId());
//			this.measure2 = new Measure(this.reco.getModel().getReconciliatedMeasureId());
//		}
	}
	
	public void setGridDataFilter(GrilleDataFilter gridDataFilter){
		this.gridDataFilter = gridDataFilter;
		if(gridDataFilter instanceof UnionGridFilter) {
			setFilter((UnionGridFilter)gridDataFilter);
		}
	}
	@Override
	public String buildQuery() throws Exception {
		if(gridBuilder != null) {
			return gridBuilder.buildQuery();
		}
		return super.buildQuery();
	}
	
	protected Map<Long, Object> attributes;
	protected Map<Long, Object> periods;
	protected Map<Long, Object> measures;
	
	@Override
	protected String buildSelectPart(UnionGridItem unionGridItem) {
		String selectPart = "SELECT id::TEXT || '_' ||  " + unionGridItem.getGrid().getId() + "::TEXT as id";
		String coma = ", ";
		Long measureId = measure.getId();//filter.getRecoData().getAmountMeasureId();
		Long measureId2 = measure2 != null ? measure2.getId() : null;
		String measure2Col = null;
		for (UnionGridColumn column : filter.getUnionGrid().getColumns()) {
			if(column.getId().equals(measureId)) {
				SmartMaterializedGridColumn c = column.getColumnByGridId(unionGridItem.getGrid().getId());
				String col = c != null ? c.getDbColumnName() : "null";
				if (!StringUtils.hasText(col)) {
					continue;
				}
				selectPart += coma + col + " as measure";
				coma = ", ";
			}
			else if(measureId2 != null && column.getId().equals(measureId2)) {
				SmartMaterializedGridColumn c = column.getColumnByGridId(unionGridItem.getGrid().getId());
				measure2Col = c != null ? c.getDbColumnName() : "null";
			}
		}
		if(StringUtils.hasText(measure2Col)) {
			selectPart += coma + measure2Col + " as measure2";
			coma = ", ";
		}
		
		this.attributes = new HashMap<>();
		this.periods = new HashMap<>();
		this.measures = new HashMap<>();
		if(!forSecondaryGrid) {
			ReconciliationModelSide side = null;
			if(reco.getPrimaryUnionModelGrid().isUnion() && getFilter().getUnionGrid().getId() == reco.getPrimaryUnionModelGrid().getGrid().getId()) {
				if(reco.isLeftGridPrimary()) {
					side = ReconciliationModelSide.LEFT;
				}
				else {
					side = ReconciliationModelSide.RIGHT;
				}
			}
			else if(reco.getSecondaryUnionModelGrid().isUnion() && getFilter().getUnionGrid().getId() == reco.getSecondaryUnionModelGrid().getGrid().getId()) {
				if(reco.isLeftGridPrimary()) {
					side = ReconciliationModelSide.RIGHT;
				}
				else {
					side = ReconciliationModelSide.LEFT;
				}
			}			
			int arg = 1;
			for(UnionGridColumn column : reco.getUnionConditionColumns(side)) {
				SmartMaterializedGridColumn c = column.getColumnByGridId(unionGridItem.getGrid().getId());
				String col = c != null ? c.getDbColumnName() : "null";			
				if (column.getType().isAttribute()) {
					boolean cont = this.attributes.containsKey(column.getId());		
					if(!cont) {
						selectPart += ", " + col + " AS " + column.getDbColAliasName();
						this.attributes.put(column.getId(), col);
					}					
				}
				if (column.getType().isPeriod()) {
					boolean cont = this.periods.containsKey(column.getId());	
					if(!cont) {
						selectPart += ", " + col + " AS " + column.getDbColAliasName();
						this.periods.put(column.getId(), col);
					}					
				}
			}	
		}
		
		if(useCreditDebit) {
//			selectPart += ", " + cdColumn + " AS DC";
			this.attributes.put(cdAttribute.getId(), cdAttribute);
			UnionGridColumn column = filter.getUnionGrid().getColumnById(cdAttribute.getId());
			SmartMaterializedGridColumn c = column.getColumnByGridId(unionGridItem.getGrid().getId());
			String col = c != null ? c.getDbColumnName() : "null";
			selectPart += ", " + col + " AS DC";
		}	
		
		for(AutoRecoRankingItem item : this.reco.getSortedRankingItems()) {
			if(this.side == item.getSide() && item.getDimensionId() != null) {				
				ReconciliationUnionModelGrid grid = item.getSide().isLeft() ? reco.getUnionModel().getLeftGrid() : reco.getUnionModel().getRigthGrid();
				if(grid.isUnion()) {
					UnionGridColumn column = grid.getGrid().getColumnById(item.getDimensionId());	
					if(column != null) {
						SmartMaterializedGridColumn c = column.getColumnByGridId(unionGridItem.getGrid().getId());
						String col = c != null ? c.getDbColumnName() : "null";			
						
						if (item.isAttribute() && !this.attributes.containsKey(item.getDimensionId())) {
							this.attributes.put(item.getDimensionId(), col);
							selectPart += ", " + col;
						}
						else if (item.isMeasure() && !this.measures.containsKey(item.getDimensionId())) {
							this.measures.put(item.getDimensionId(), col);
							selectPart += ", " + col;
						}
						else if (item.isPeriod() && item.getDimensionId() != null) {
							this.periods.put(item.getDimensionId(), col);
							selectPart += ", " + col;
						}
					}
				}
				
			}
		}
		
		return selectPart;
	}
	
}

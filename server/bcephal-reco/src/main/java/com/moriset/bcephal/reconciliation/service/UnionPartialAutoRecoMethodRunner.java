package com.moriset.bcephal.reconciliation.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.filters.ReconciliationDataFilter;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.UnionGridColumn;
import com.moriset.bcephal.grid.service.UnionGridFilter;
import com.moriset.bcephal.reconciliation.domain.PartialRecoItem;
import com.moriset.bcephal.reconciliation.domain.ReconciliationCondition;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModelSide;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionData;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionModelGrid;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnionPartialAutoRecoMethodRunner extends UnionAutoRecoMethodRunner {

	protected void reconciliateAndCommit(RunModes mode) throws Exception {
		reconciliationUnionModelService.reconciliateAndCommit2(datas, "B-CEPHAL", mode, false);
	}
	
	@Override
	protected ReconciliationUnionData buildReconciliationData() {
		ReconciliationUnionData data = super.buildReconciliationData();
		data.setAllowPartialReco(true);
		return data;
	}
	
	protected boolean validateAmount(PartialRecoItem primaryPartialRecoItem, PartialRecoItem secondaryPartialRecoItem, List<BigDecimal> amounts, ReconciliationUnionData data) {
		
        boolean isLeftPlusRight = this.reco.getUnionModel().getBalanceFormula().isLeftPlusRight();
        data.setLeftAmount(primaryPartialRecoItem.getRealAmount());
        data.setRigthAmount(secondaryPartialRecoItem.getRealAmount());
                        
        BigDecimal balance = isLeftPlusRight ? data.getLeftAmount().add(data.getRigthAmount()) : data.getLeftAmount().subtract(data.getRigthAmount());
        data.setBalanceAmount(balance);
                
        boolean valide = balance.compareTo(this.reco.getConditionMinValue()) >= 0 && balance.compareTo(this.reco.getConditionMaxValue()) <= 0;
        
        if (valide) {
        	boolean primaryIsPositive = primaryPartialRecoItem.getRemainningAmount().compareTo(BigDecimal.ZERO) >= 0;
        	boolean balanceIsPositive = balance.compareTo(BigDecimal.ZERO) >= 0;
        	boolean croisement = primaryIsPositive != balanceIsPositive;
        	BigDecimal primaryRecoAmount = BigDecimal.ZERO;
        	BigDecimal secondaryRecoAmount = BigDecimal.ZERO;
        	BigDecimal reconciliatedRecoAmount = BigDecimal.ZERO;
        	
        	BigDecimal smallest = primaryPartialRecoItem.getRemainningAmount().abs().compareTo(secondaryPartialRecoItem.getRemainningAmount().abs()) <= 0
    				? primaryPartialRecoItem.getRemainningAmount() : secondaryPartialRecoItem.getRemainningAmount();
        	
        	if(croisement) {
        		primaryRecoAmount = primaryPartialRecoItem.getRemainningAmount();
        		secondaryRecoAmount = isLeftPlusRight ? BigDecimal.ZERO.subtract(primaryRecoAmount) : primaryRecoAmount;
        		reconciliatedRecoAmount = smallest;
        	}
        	else if(!isLeftPlusRight) {
        		reconciliatedRecoAmount = secondaryPartialRecoItem.getRemainningAmount();
        		if(primaryIsPositive) {
        			primaryRecoAmount = primaryPartialRecoItem.getRemainningAmount().compareTo(secondaryPartialRecoItem.getRemainningAmount()) <= 0 
            				? primaryPartialRecoItem.getRemainningAmount()
            						: secondaryPartialRecoItem.getRemainningAmount();
        		}
        		else {
        			primaryRecoAmount = primaryPartialRecoItem.getRemainningAmount().compareTo(secondaryPartialRecoItem.getRemainningAmount()) <= 0 
            				? secondaryPartialRecoItem.getRemainningAmount()
            						: primaryPartialRecoItem.getRemainningAmount();
        		}
        		secondaryRecoAmount = primaryRecoAmount;
        	}
        	else if(isLeftPlusRight) {
        		reconciliatedRecoAmount = secondaryPartialRecoItem.getRemainningAmount();
        		primaryRecoAmount = BigDecimal.ZERO.subtract(secondaryPartialRecoItem.getRemainningAmount());
        		secondaryRecoAmount = secondaryPartialRecoItem.getRemainningAmount();
        	}
        	primaryPartialRecoItem.setReconciliatedAmount(primaryPartialRecoItem.getReconciliatedAmount().add(primaryRecoAmount));
        	primaryPartialRecoItem.setRemainningAmount(primaryPartialRecoItem.getRemainningAmount().subtract(primaryRecoAmount));
        	
        	secondaryPartialRecoItem.setReconciliatedAmount(secondaryPartialRecoItem.getReconciliatedAmount().add(secondaryRecoAmount));
    		secondaryPartialRecoItem.setRemainningAmount(secondaryPartialRecoItem.getRemainningAmount().subtract(secondaryRecoAmount));
    		
    		data.setReconciliatedAmount(reconciliatedRecoAmount);
        }
        return valide;
	}
	
	@Override
	protected List<BigDecimal> buildSecondaryAmounts(BigDecimal primaryAmount, AutoRecoUnionQueryBuilder builder,
			boolean isLeftPlusRight, boolean useCreditDebit) {				
		List<BigDecimal> amounts = new ArrayList<>(0);
		if(useCreditDebit && builder.cdValue.equals(builder.debitValue)) {
    		primaryAmount = BigDecimal.ZERO.subtract(primaryAmount);
    	}              
        BigDecimal secondaryAmount = primaryAmount;
        if(isLeftPlusRight) {
        	secondaryAmount = BigDecimal.ZERO.subtract(primaryAmount);
        }        
        amounts.add(secondaryAmount.add(this.reco.getConditionMinValue()));
    	amounts.add(secondaryAmount.add(this.reco.getConditionMaxValue())); 
    	builder.setAmount1(amounts.get(0));
        builder.setAmount2(amounts.get(1));
        if(builder.gridBuilder != null) {
        	builder.gridBuilder.setAmount1(amounts.get(0));
            builder.gridBuilder.setAmount2(amounts.get(1));
        }
		return amounts;
	}
	
	@Override
	protected Map<String, Object> buildConditionAttributeValuesMap(Object[] row, AutoRecoUnionQueryBuilder builder) {
		Map<String, Object> map = new HashMap<>();
		int i = 0;
		for(ReconciliationCondition item : reco.getSortedConditions()) {
			item.setValue(null);
			DimensionType columnType = null;
			ReconciliationUnionModelGrid grid = item.getSide1().isLeft() ? reco.getUnionModel().getLeftGrid() : reco.getUnionModel().getRigthGrid();
			if(grid.isUnion()) {
				UnionGridColumn column = grid.getGrid().getColumnById(item.getColumnId1());
				item.setUnionGridColumn1(column);
				columnType = column != null ? column.getType() : null;
				item.setPrimaryDbColumn(column != null ? column.getDbColAliasName() : null);
			}
			else {
				GrilleColumn column = grid.getGrille().getColumnById(item.getColumnId1());
				item.setColumn1(column);
				columnType = column != null ? column.getType() : null;
				item.setPrimaryDbColumn(column != null ? column.getUniverseTableColumnName() : null);
			}
			
			grid = item.getSide2().isLeft() ? reco.getUnionModel().getLeftGrid() : reco.getUnionModel().getRigthGrid();
			if(grid.isUnion()) {
				UnionGridColumn column = grid.getGrid().getColumnById(item.getColumnId2());
				item.setUnionGridColumn2(column);
				//item.setPrimaryDbColumn(column != null ? column.getDbColAliasName() : null);
			}
			else {
				GrilleColumn column = grid.getGrille().getColumnById(item.getColumnId2());
				item.setColumn2(column);
				//item.setPrimaryDbColumn(column != null ? column.getUniverseTableColumnName() : null);
			}
			ReconciliationModelSide secondaryside = reco.isLeftGridPrimary() ? ReconciliationModelSide.RIGHT : ReconciliationModelSide.LEFT;
			boolean secondaryToPrimary = secondaryside == item.getSide1();
			grid = secondaryToPrimary && secondaryside.isLeft() ? reco.getUnionModel().getLeftGrid() : reco.getUnionModel().getRigthGrid();
			item.setSecondaryToPrimary(secondaryToPrimary);
			Long columnId = secondaryToPrimary ? item.getColumnId1() : item.getColumnId2();
			if(grid.isUnion()) {
				UnionGridColumn secondarycolumn = grid.getGrid().getColumnById(columnId);	
				item.setSecondaryDbColumn(secondarycolumn != null ? secondarycolumn.getDbColAliasName() : null);
			}
			else {
				GrilleColumn secondarycolumn = grid.getGrille().getColumnById(columnId);
				item.setSecondaryDbColumn(secondarycolumn != null ? secondarycolumn.getUniverseTableColumnName() : null);
			}
			
			if(columnType != null) {
				item.setDimensionType(columnType);
				if(columnType.isAttribute() /*&& column.getDimensionId() != null*/) {
					item.setDimensionType(columnType);
					item.setValue(row[i + 3]);
					map.put(item.getSecondaryDbColumn(), row[i + 3]);					
				}
				if (columnType.isPeriod()) {
					Date date = null;
					Object ob = row[i + 3];
					if(ob instanceof Date) {
						date = (Date)ob;
					}
					else if(ob != null){
						try {
							date = new SimpleDateFormat("dd/MM/yyyy").parse(ob.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					item.setValue(date);
					
					if(date == null) {
						continue;
					}
					//map.put(item.getDbColumn(), date);
				}
			}
			i++;
			
			
			
			
//			item.setValue(null);
//			UnionGridColumn column = item.getSide1().isLeft() ? reco.getUnionModel().getLeftGrid().getGrid().getColumnById(item.getColumnId1()) 
//					: reco.getUnionModel().getRigthGrid().getGrid().getColumnById(item.getColumnId1());
//			item.setUnionGridColumn1(column);
//			item.setUnionGridColumn2(item.getSide2().isLeft() ? reco.getUnionModel().getLeftGrid().getGrid().getColumnById(item.getColumnId2()) 
//					: reco.getUnionModel().getRigthGrid().getGrid().getColumnById(item.getColumnId2()));
//			item.setPrimaryDbColumn(column != null ? column.getDbColAliasName() : null);
//			
//			ReconciliationModelSide secondaryside = reco.isLeftGridPrimary() ? ReconciliationModelSide.RIGHT : ReconciliationModelSide.LEFT;
//			UnionGridColumn secondarycolumn = null;
//			if(secondaryside == item.getSide1()) {
//				secondarycolumn = secondaryside.isLeft() ? reco.getUnionModel().getLeftGrid().getGrid().getColumnById(item.getColumnId1()) 
//						: reco.getUnionModel().getRigthGrid().getGrid().getColumnById(item.getColumnId1());
//				item.setSecondaryToPrimary(true);
//			}
//			else {
//				secondarycolumn = secondaryside.isLeft() ? reco.getUnionModel().getLeftGrid().getGrid().getColumnById(item.getColumnId2()) 
//						: reco.getUnionModel().getRigthGrid().getGrid().getColumnById(item.getColumnId2());
//				item.setSecondaryToPrimary(false);
//			}
//			item.setSecondaryDbColumn(secondarycolumn != null ? secondarycolumn.getDbColAliasName() : null);
//			
//			if(column != null) {
//				item.setDimensionType(column.getType());
//				if(column.getType().isAttribute() /*&& column.getDimensionId() != null*/) {
//					item.setDimensionType(column.getType());
//					item.setValue(row[i + 3]);
//					map.put(item.getSecondaryDbColumn(), row[i + 3]);					
//				}
//				if (column.getType().isPeriod()) {
//					Date date = null;
//					Object ob = row[i + 3];
//					if(ob instanceof Date) {
//						date = (Date)ob;
//					}
//					else if(ob != null){
//						try {
//							date = new SimpleDateFormat("dd/MM/yyyy").parse(ob.toString());
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//					item.setValue(date);
//					
//					if(date == null) {
//						continue;
//					}
//					//map.put(item.getDbColumn(), date);
//				}
//			}
//			i++;
		}
		
        if(builder.useCreditDebit) {
        	Object cd = row[i + 3];
        	if(builder.debitValue.equals(cd)) {
        		//map.put(builder.cdColumn, builder.creditValue);
        		builder.cdValue = builder.creditValue;
        		builder.opositecdValue = builder.debitValue;
        	}
        	else {
        		//map.put(builder.cdColumn, builder.debitValue);
        		builder.cdValue = builder.debitValue;
        		builder.opositecdValue = builder.creditValue;
        	}
        }
		return map;
	}

	@Override
	protected int buildViews() throws Exception {		
		refreshAmounts();
		
		boolean useCreditDebit = this.reco.getUnionModel().isUseDebitCredit();		
		this.primaryFilter = buidPrimaryFilter(); 
		this.reco.setConditions(this.reco.getSortedConditions()); 
        this.secondaryFilter = buidSecondaryFilter(); 
                 
        this.primaryColumns = "";
        this.secondaryColumns = "";
		this.primaryView = "RECOMODEL_P_VIEW_" + this.reco.getId();
		this.SecondaryView = "RECOMODEL_S_VIEW_" + this.reco.getId();
		
		primaryBuilder.measure = getPrimaryRemainningMeasure();
        primaryBuilder.measure2 = getPrimaryReconciliatedMeasure();
        if(primaryBuilder.gridBuilder != null) {
        	GrilleColumn column = primaryBuilder.gridBuilder.getFilter().getGrid().getColumnById(primaryBuilder.measure.getId()); 
        	primaryBuilder.gridBuilder.measure = new Measure(column != null ? column.getDimensionId() : null);
        	column = primaryBuilder.gridBuilder.getFilter().getGrid().getColumnById(primaryBuilder.measure2.getId()); 
            primaryBuilder.gridBuilder.measure2 = new Measure(column != null ? column.getDimensionId() : null);
        }
        
        secondaryBuilder.measure = getSecondaryRemainningMeasure();
        secondaryBuilder.measure2 = getSecondaryReconciliatedMeasure();
        if(secondaryBuilder.gridBuilder != null) {
        	GrilleColumn column = secondaryBuilder.gridBuilder.getFilter().getGrid().getColumnById(secondaryBuilder.measure.getId()); 
        	secondaryBuilder.gridBuilder.measure = new Measure(column != null ? column.getDimensionId() : null);
        	column = secondaryBuilder.gridBuilder.getFilter().getGrid().getColumnById(secondaryBuilder.measure2.getId()); 
        	secondaryBuilder.gridBuilder.measure2 = new Measure(column != null ? column.getDimensionId() : null);
        }
        
        return createViews(useCreditDebit);		
	}	
	
	protected void refreshAmounts() throws Exception {
		if(this.reco.isRefreshAmounts()) {
			log.debug("Try to refresh reconciliated and reamaing amounts...");
			
			log.debug("Left grid amounts refreshing...");
			ReconciliationUnionModelGrid grid = this.reco.getUnionModel().getLeftGrid();
			if(grid.isUnion()) {
				refreshAmountsForUnion(grid, true);
			}
			else {
				refreshAmountsForUniverse(grid, true);
			}
			log.debug("Left grid amounts refreshed...");
			
			log.debug("Right grid amounts refreshing...");
			grid = this.reco.getUnionModel().getRigthGrid();
			if(grid.isUnion()) {
				refreshAmountsForUnion(grid, false);
			}
			else {
				refreshAmountsForUniverse(grid, false);
			}
			log.debug("Right grid amounts refreshed...");
		}
	}
	
	private void refreshAmountsForUnion(ReconciliationUnionModelGrid grid, boolean forleft) throws Exception {
		UnionGridFilter filter = new UnionGridFilter();		
		filter.setUnionGrid(grid.getGrid());
		filter.setPage(0);
		filter.setPageSize(25);
		filter.setShowAll(true);
		filter.setAllowRowCounting(false);			
		filter.setRecoAttributeId(grid.getRecoTypeColumnId());			
		filter.setFreezeAttributeId(grid.getFreezeTypeColumnId());			
		filter.setNoteAttributeId(grid.getNoteTypeColumnId());
		filter.setDebit(grid.getGrid().isDebit());
		filter.setCredit(grid.getGrid().isCredit());
		filter.setConterpart(false);
		filter.setRowType(grid.getGrid().getRowType());			
		filter.setRecoData(new ReconciliationDataFilter());
		filter.getRecoData().setAllowFreeze(this.reco.getUnionModel().isAllowFreeze());
		filter.getRecoData().setFreezeAttributeId(grid.getFreezeTypeColumnId());
		filter.getRecoData().setAllowNeutralization(this.reco.getUnionModel().isAllowNeutralization());
		filter.getRecoData().setNeutralizationAttributeId(grid.getNeutralizationTypeColumnId());
		filter.getRecoData().setAllowPartialReco(this.reco.getUnionModel().isAllowPartialReco());
		filter.getRecoData().setPartialRecoAttributeId(grid.getPartialRecoTypeColumnId());
		filter.getRecoData().setRecoAttributeId(grid.getRecoTypeColumnId());
		filter.getRecoData().setAmountMeasureId(grid.getMeasureColumnId());
		filter.getRecoData().setReconciliatedMeasureId(grid.getReconciliatedMeasureColumnId());
		filter.getRecoData().setRemainningMeasureId(grid.getRemainningMeasureColumnId());
		filter.getRecoData().setCredit(grid.getGrid().isCredit());
		filter.getRecoData().setDebit(grid.getGrid().isDebit());
		filter.getRecoData().setUseDebitCredit(this.reco.getUnionModel().isUseDebitCredit());	
//		filter.getRecoData().setMainGridType(grid.getGrid().getType().getGridType().name());
//		filter.getRecoData().setMainGridId(grid.getGrid().getMainGrid().getGridId());
		reconciliationUnionModelService.refreshReconciliatedAmounts2(filter, Locale.ENGLISH, true);
	}

	private void refreshAmountsForUniverse(ReconciliationUnionModelGrid grid, boolean forleft) {
		UnionGridFilter filter = new UnionGridFilter();
		filter.setGrid(grid.getGrille());
		//filter.setFilter(this.reco.getModel().getLeftGrid().getUserFilter());
		//filter.attribute = filter.attribute;			
		filter.setPage(0);
		filter.setPageSize(25);
		filter.setShowAll(true);
		filter.setAllowRowCounting(false);			
		filter.setRecoAttributeId(grid.getRecoTypeColumnId());			
		filter.setFreezeAttributeId(grid.getFreezeTypeColumnId());			
		filter.setNoteAttributeId(grid.getNoteTypeColumnId());
		filter.setDebit(grid.getGrille().isDebit());
		filter.setCredit(grid.getGrille().isCredit());
		filter.setConterpart(false);
		filter.setRowType(grid.getGrille().getRowType());			
		filter.setRecoData(new ReconciliationDataFilter());
		filter.getRecoData().setAllowFreeze(this.reco.getUnionModel().isAllowFreeze());
		filter.getRecoData().setFreezeAttributeId(grid.getFreezeTypeColumnId());
		filter.getRecoData().setAllowNeutralization(this.reco.getUnionModel().isAllowNeutralization());
		filter.getRecoData().setNeutralizationAttributeId(grid.getNeutralizationTypeColumnId());
		filter.getRecoData().setAllowPartialReco(this.reco.getUnionModel().isAllowPartialReco());
		filter.getRecoData().setPartialRecoAttributeId(grid.getPartialRecoTypeColumnId());
		filter.getRecoData().setRecoAttributeId(grid.getRecoTypeColumnId());
		filter.getRecoData().setAmountMeasureId(grid.getMeasureColumnId());
		filter.getRecoData().setReconciliatedMeasureId(grid.getReconciliatedMeasureColumnId());
		filter.getRecoData().setRemainningMeasureId(grid.getRemainningMeasureColumnId());
		filter.getRecoData().setCredit(grid.getGrille().isCredit());
		filter.getRecoData().setDebit(grid.getGrille().isDebit());
		filter.getRecoData().setUseDebitCredit(this.reco.getUnionModel().isUseDebitCredit());
		filter.loadFilterRecoDataForGrid();
		reconciliationModelService.refreshReconciliatedAmounts(filter, Locale.ENGLISH);		
	}
	
	
	
	
	
}

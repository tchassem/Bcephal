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
import com.moriset.bcephal.domain.filters.ReconciliationDataFilter;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.reconciliation.domain.PartialRecoItem;
import com.moriset.bcephal.reconciliation.domain.ReconciliationCondition;
import com.moriset.bcephal.reconciliation.domain.ReconciliationData;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModelSide;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PartialAutoRecoMethodRunner extends AutoRecoMethodRunner {

	protected void reconciliateAndCommit(RunModes mode) throws Exception {
		reconciliationModelService.reconciliateAndCommit2(datas, "B-CEPHAL", mode);
	}
	
	protected boolean validateAmount(PartialRecoItem primaryPartialRecoItem, PartialRecoItem secondaryPartialRecoItem, List<BigDecimal> amounts, ReconciliationData data) {
		
        boolean isLeftPlusRight = this.reco.getModel().getBalanceFormula().isLeftPlusRight();
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
	protected List<BigDecimal> buildSecondaryAmounts(BigDecimal primaryAmount, AutoRecoGridQueryBuilder builder,
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
		return amounts;
	}
	
	@Override
	protected Map<String, Object> buildConditionAttributeValuesMap(Object[] row, AutoRecoGridQueryBuilder builder) {
		Map<String, Object> map = new HashMap<>();
		int i = 0;
		for(ReconciliationCondition item : reco.getSortedConditions()) {
			item.setValue(null);
			GrilleColumn column = item.getSide1().isLeft() ? reco.getModel().getLeftGrid().getColumnById(item.getColumnId1()) : reco.getModel().getRigthGrid().getColumnById(item.getColumnId1());
			item.setColumn1(column);
			item.setColumn2(item.getSide2().isLeft() ? reco.getModel().getLeftGrid().getColumnById(item.getColumnId2()) : reco.getModel().getRigthGrid().getColumnById(item.getColumnId2()));
			item.setPrimaryDbColumn(column != null ? column.getUniverseTableColumnName() : null);
			
			ReconciliationModelSide secondaryside = reco.isLeftGridPrimary() ? ReconciliationModelSide.RIGHT : ReconciliationModelSide.LEFT;
			GrilleColumn secondarycolumn = null;
			if(secondaryside == item.getSide1()) {
				secondarycolumn = secondaryside.isLeft() ? reco.getModel().getLeftGrid().getColumnById(item.getColumnId1()) : reco.getModel().getRigthGrid().getColumnById(item.getColumnId1());
				item.setSecondaryToPrimary(true);
			}
			else {
				secondarycolumn = secondaryside.isLeft() ? reco.getModel().getLeftGrid().getColumnById(item.getColumnId2()) : reco.getModel().getRigthGrid().getColumnById(item.getColumnId2());
				item.setSecondaryToPrimary(false);
			}
			item.setSecondaryDbColumn(secondarycolumn != null ? secondarycolumn.getUniverseTableColumnName() : null);
			
			if(column != null) {
				item.setDimensionType(column.getType());
				if(column.getType().isAttribute() && column.getDimensionId() != null) {
					item.setDimensionType(column.getType());
					item.setValue(row[i + 3]);
					map.put(item.getSecondaryDbColumn(), row[i + 3]);					
				}
				if (column.getType().isPeriod()) {
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
		
		this.primaryFilter = buidPrimaryFilter(); 
        this.primaryBuilder = new AutoRecoGridQueryBuilder(this.reco, false, entityManager, this.reco.getPrimarySide(), true); 
        this.primaryBuilder.setParameterRepository(parameterRepository);
        //primaryBuilder.setMeasure(new Measure(this.reco.getPrimaryMeasureId()));
        boolean useCreditDebit = this.reco.getModel().isUseDebitCredit();
        if(useCreditDebit && !stop) {
        	buildCDValues(primaryBuilder);
        }
        this.reco.setConditions(this.reco.getSortedConditions());
                
        this.secondaryFilter = buidSecondaryFilter(); 
        this.secondaryBuilder = new AutoRecoGridQueryBuilder(this.reco, false, entityManager, this.reco.getSecondarySide(), true);
        this.secondaryBuilder.setParameterRepository(parameterRepository);
        //secondaryBuilder.setMeasure(new Measure(this.reco.getSecondaryMeasureId()));
        if(useCreditDebit) {
        	secondaryBuilder.useCreditDebit = primaryBuilder.useCreditDebit;
        	secondaryBuilder.debitValue = primaryBuilder.debitValue;
        	secondaryBuilder.creditValue = primaryBuilder.creditValue;
        	secondaryBuilder.cdValue = primaryBuilder.cdValue;
        	secondaryBuilder.cdColumn = primaryBuilder.cdColumn;
        	secondaryBuilder.cdAttribute = primaryBuilder.cdAttribute;
        }        
                 
        this.primaryColumns = "";
        this.secondaryColumns = "";
		this.primaryView = "RECO_P_VIEW_" + this.reco.getId();
		this.SecondaryView = "RECO_S_VIEW_" + this.reco.getId();
					
		return createViews(useCreditDebit);
	}	
	
	protected void refreshAmounts() {
		if(this.reco.isRefreshAmounts()) {
			log.debug("Try to refresh reconciliated and reamaing amounts...");
			
			log.debug("Left grid amounts refreshing...");
			GrilleDataFilter filter = new GrilleDataFilter();
			filter.setGrid(this.reco.getModel().getLeftGrid());
			//filter.setFilter(this.reco.getModel().getLeftGrid().getUserFilter());
			//filter.attribute = filter.attribute;			
			filter.setPage(0);
			filter.setPageSize(25);
			filter.setShowAll(true);
			filter.setAllowRowCounting(false);			
			filter.setRecoAttributeId(this.reco.getModel().getRecoAttributeId());			
			filter.setFreezeAttributeId(this.reco.getModel().getFreezeAttributeId());			
			filter.setNoteAttributeId(this.reco.getModel().getNoteAttributeId());
			filter.setDebit(this.reco.getModel().getLeftGrid().isDebit());
			filter.setCredit(this.reco.getModel().getLeftGrid().isCredit());
			filter.setConterpart(false);
			filter.setRowType(this.reco.getModel().getLeftGrid().getRowType());			
			filter.setRecoData(new ReconciliationDataFilter());
			filter.getRecoData().setAllowFreeze(this.reco.getModel().isAllowFreeze());
			filter.getRecoData().setFreezeAttributeId(this.reco.getModel().getFreezeAttributeId());
			filter.getRecoData().setAllowNeutralization(this.reco.getModel().isAllowNeutralization());
			filter.getRecoData().setNeutralizationAttributeId(this.reco.getModel().getNeutralizationAttributeId());
			filter.getRecoData().setAllowPartialReco(this.reco.getModel().isAllowPartialReco());
			filter.getRecoData().setPartialRecoAttributeId(this.reco.getModel().getPartialRecoAttributeId());
			filter.getRecoData().setRecoAttributeId(this.reco.getModel().getRecoAttributeId());
			filter.getRecoData().setAmountMeasureId(this.reco.getModel().getLeftMeasureId());
			filter.getRecoData().setReconciliatedMeasureId(this.reco.getModel().getReconciliatedMeasureId());
			filter.getRecoData().setRemainningMeasureId(this.reco.getModel().getRemainningMeasureId());
			filter.getRecoData().setCredit(this.reco.getModel().getLeftGrid().isCredit());
			filter.getRecoData().setDebit(this.reco.getModel().getLeftGrid().isDebit());
			filter.getRecoData().setUseDebitCredit(this.reco.getModel().isUseDebitCredit());
			
			reconciliationModelService.refreshReconciliatedAmounts(filter, Locale.ENGLISH);
			log.debug("Left grid amounts refreshed...");
			
			log.debug("Right grid amounts refreshing...");
			filter = new GrilleDataFilter();
			filter.setGrid(this.reco.getModel().getRigthGrid());
			//filter.setFilter(this.reco.getModel().getRigthGrid().getUserFilter());
			//filter.attribute = filter.attribute;			
			filter.setPage(0);
			filter.setPageSize(25);
			filter.setShowAll(true);
			filter.setAllowRowCounting(false);			
			filter.setRecoAttributeId(this.reco.getModel().getRecoAttributeId());			
			filter.setFreezeAttributeId(this.reco.getModel().getFreezeAttributeId());			
			filter.setNoteAttributeId(this.reco.getModel().getNoteAttributeId());
			filter.setDebit(this.reco.getModel().getRigthGrid().isDebit());
			filter.setCredit(this.reco.getModel().getRigthGrid().isCredit());
			filter.setConterpart(false);
			filter.setRowType(this.reco.getModel().getRigthGrid().getRowType());			
			filter.setRecoData(new ReconciliationDataFilter());
			filter.getRecoData().setAllowFreeze(this.reco.getModel().isAllowFreeze());
			filter.getRecoData().setFreezeAttributeId(this.reco.getModel().getFreezeAttributeId());
			filter.getRecoData().setAllowNeutralization(this.reco.getModel().isAllowNeutralization());
			filter.getRecoData().setNeutralizationAttributeId(this.reco.getModel().getNeutralizationAttributeId());
			filter.getRecoData().setAllowPartialReco(this.reco.getModel().isAllowPartialReco());
			filter.getRecoData().setPartialRecoAttributeId(this.reco.getModel().getPartialRecoAttributeId());
			filter.getRecoData().setRecoAttributeId(this.reco.getModel().getRecoAttributeId());
			filter.getRecoData().setAmountMeasureId(this.reco.getModel().getRigthMeasureId());
			filter.getRecoData().setReconciliatedMeasureId(this.reco.getModel().getReconciliatedMeasureId());
			filter.getRecoData().setRemainningMeasureId(this.reco.getModel().getRemainningMeasureId());
			filter.getRecoData().setCredit(this.reco.getModel().getRigthGrid().isCredit());
			filter.getRecoData().setDebit(this.reco.getModel().getRigthGrid().isDebit());
			filter.getRecoData().setUseDebitCredit(this.reco.getModel().isUseDebitCredit());
			
			reconciliationModelService.refreshReconciliatedAmounts(filter, Locale.ENGLISH);
			log.debug("Right grid amounts refreshed...");
			
		}
	}
	
}

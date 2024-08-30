package com.moriset.bcephal.reconciliation.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.reconciliation.domain.PartialRecoItem;
import com.moriset.bcephal.reconciliation.domain.ReconciliationData;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Scope(scopeName= ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PartialAutoRecoOneOnOneRunner extends PartialAutoRecoMethodRunner {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void run() throws Exception {
		log.debug("Auto Reco : {} - Running....", this.reco.getName());
		int count = 0;
		reco.setBatchChunk(1);
		try {						
			log.trace("Auto Reco : {} - Build views....", this.reco.getName());
			count = buildViews();
			log.trace("Auto Reco : {} - Views builded!", this.reco.getName());
			if(listener != null) {
	        	listener.startSubInfo(count);
	        }
			log.debug("Auto Reco : {} - Primary grid row count = {}", this.reco.getName(), count);
			if(stop) { return;}
			
			List<Object[]> rows = getPrimaryGridNextRows();
			int pageCount = rows.size();
			log.trace("Auto Reco : {} - Primary grid rows readed!", this.reco.getName());
			
			
	        if(stop) { return;}	        
	        
	        boolean isLeftPlusRight = this.reco.getModel().getBalanceFormula().isLeftPlusRight();
	        boolean useCreditDebit = this.reco.getModel().isUseDebitCredit();			
	        boolean leftGridIsprimary = getReco().isLeftGridPrimary();
	        
	        while(pageCount > 0 && count > 0 && !stop) {
	        	if(stop) { return;}
	        	for(Object[] row : rows) {       
	        		if(stop) { return;}
	        		ReconciliationData data = buildReconciliationData();
	        		boolean reconciliated = false;
	        		count--;
	        		if(leftGridIsprimary) {
	        			data.setLeftids(new ArrayList<Long>());
	        		}
	        		else {
	        			data.setRightids(new ArrayList<Long>());
	        		}
	        		BigDecimal primaryAmount = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;	        		
	        		primaryRowId = Long.parseLong(row[0].toString());
	        		if(leftGridIsprimary) {
	        			data.getLeftids().add(primaryRowId);	      
	        		}
	        		else {
	        			data.getRightids().add(primaryRowId);	      
	        		}
	        		
	        		PartialRecoItem primaryPartialRecoItem = new PartialRecoItem();
	        		primaryPartialRecoItem.setPrimary(true);
	        		primaryPartialRecoItem.setId(primaryRowId);
	        		primaryPartialRecoItem.setLeft(leftGridIsprimary);
	        		primaryPartialRecoItem.setRemainningAmount(primaryAmount);
	        		BigDecimal recoAmount = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
	        		primaryPartialRecoItem.setReconciliatedAmount(recoAmount);
	        		
	        		boolean isDebit = StringUtils.hasText(secondaryBuilder.opositecdValue) && secondaryBuilder.opositecdValue.equals(secondaryBuilder.debitValue);
	        		primaryPartialRecoItem.buildRealAmount(useCreditDebit, isDebit);
	        			        		
	        		log.trace("[{} - {} - {}]", (count+1), primaryRowId, row[1]);
	        		    	        		
	        		Map<String, Object> map = buildConditionAttributeValuesMap(row, secondaryBuilder);
	                secondaryBuilder.setCommonAttributeValuesMap(map);
	                List<BigDecimal> amounts = buildSecondaryAmounts(primaryAmount, secondaryBuilder, isLeftPlusRight, useCreditDebit);	                
	                
	    			int scount = getSecondaryGridRowCount();
	    			
	    			List<Object[]> srows = getSecondaryGridNextRows();
	                int secondaryPageCount = srows.size();	                       	                
	                secondaryRowIds = new ArrayList<>(0);	
	                
	                while(!reconciliated && secondaryPageCount > 0 && scount > 0 && !stop) { 
	                	if(stop) { return;}
		                for(Object[] r : srows) {
		                	if(stop) { return;}
		                	scount--;
		                	secondaryRowIds = new ArrayList<>(0);
		                	long soid = Long.parseLong(r[0].toString());
		                	if(soid != primaryRowId) {	  
		                		secondaryRowIds.add(soid);
			                	BigDecimal setAmount = r[1] != null ? new BigDecimal(r[1].toString()) : BigDecimal.ZERO;
			                	
			                	PartialRecoItem secondaryPartialRecoItem = new PartialRecoItem();
			                	secondaryPartialRecoItem.setId(soid);
			                	secondaryPartialRecoItem.setLeft(!leftGridIsprimary);
			                	secondaryPartialRecoItem.setRemainningAmount(setAmount);
				        		BigDecimal secrecoAmount = r[2] != null ? new BigDecimal(r[2].toString()) : BigDecimal.ZERO;
				        		secondaryPartialRecoItem.setReconciliatedAmount(secrecoAmount);
			                	
				        		String dc = useCreditDebit && r.length > 3 ? (String)r[3] : null;				        		
				        		boolean isSecondDebit = StringUtils.hasText(dc) && dc.equals(secondaryBuilder.debitValue);
				        		secondaryPartialRecoItem.buildRealAmount(useCreditDebit, isSecondDebit);
				        		primaryPartialRecoItem.buildRealAmount(useCreditDebit, isDebit);
				        					                	
			                	boolean valid = validateAmount(primaryPartialRecoItem, secondaryPartialRecoItem, amounts, data);
			                	if(valid) {
			                		if(stop) { return;}
			                		data.setPartialRecoItems(new ArrayList<>());
			                		data.getPartialRecoItems().add(primaryPartialRecoItem);
			                		data.getPartialRecoItems().add(secondaryPartialRecoItem);
			                		buildEnrichmentData(data);
		                			reconciliate(data, primaryRowId, soid, setAmount, primaryView, SecondaryView);			                			
			                		reconciliated = true;			                		
			                		boolean isOk = BigDecimal.ZERO.compareTo(data.getBalanceAmount()) == 0;			                		
			                		if(isOk) {			                			
				                		break;
			                		}		                		
			                	}     
		                	}
		                } 
		                if(!reconciliated) {
		                	
		                }
	                }
	                setpCount++;
	                if(setpCount == batchSetpNbr) {
		        		if(listener != null) {
		                	listener.nextSubInfoStep(setpCount);
		                }
		        		setpCount = 0;
	                }
	            }
	        	if(count > 0) { 	        		
	        		rows = getPrimaryGridNextRows();
	                pageCount = rows.size();
	        	}
	        }	  
	        if(transactionCount > 0) {
	        	reconciliationModelService.reconciliateAndCommit(datas, "B-CEPHAL",  RunModes.A);			
				transactionCount = 0;
				datas.clear();
			}
	        if(setpCount > 0) {
	        	if(listener != null) {
                	listener.nextSubInfoStep(setpCount);
                }
	        	setpCount = 0;
			}
	        runScript();	        
		}
		catch (Exception e) {
			if(e instanceof BcephalException) {
				throw e;
			}
			String message = "Unable to run Auto Reco : " + this.reco.getName() + "\n" + e.getMessage();			
			throw new BcephalException(message);
		}
		finally {
			dropViews();
		}		
	}
	
	
	
	
	
}

package com.moriset.bcephal.reconciliation.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.socket.TaskProgressInfo;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionData;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Scope(scopeName= ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UnionAutoRecoOneSideRunner extends UnionAutoRecoMethodRunner {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void run() throws Exception {
		log.debug("Auto Reco : {} - Running....", this.reco.getName());
		int count = 0;
		try{
			Integer maxDuration = null;
			if(this.getReco().isUseCombinations()) {
				maxDuration = this.getReco().getMaxDurationPerLine() > 0
						? this.getReco().getMaxDurationPerLine() : null;
			}
						
			log.trace("Auto Reco : {} - Build views....", this.reco.getName());
			count = buildViews();
			log.trace("Auto Reco : {} - Views builded!", this.reco.getName());
			if(listener != null) {
	        	listener.startSubInfo(count);
	        }
			log.debug("Auto Reco : {} - Primary grid row count = {}", this.reco.getName(), count);
			if(stop) { return;}
			
			List<Object[]> rows = getPrimaryGridNextRows();
			@SuppressWarnings("unused")
			int pageCount = rows.size();
			log.trace("Auto Reco : {} - Primary grid rows readed!", this.reco.getName());
						
			
	        if(stop) { return;}	        
	        
	        boolean isLeftPlusRight = this.getReco().getModel().getBalanceFormula().isLeftPlusRight();
	        boolean useCreditDebit = this.getReco().getModel().isUseDebitCredit();
	        
	        while(!rows.isEmpty() && count > 0 && !stop) { 
	        	if(stop) { return;}
	        	for(Object[] row : rows) { 
	        		if(stop) { return;}
	        		ReconciliationUnionData data = buildReconciliationData();	        
	    	        //service.loadSettings(data);
	        		boolean reconciliated = false;
	        		count--;
	        		if(getReco().isLeftGridPrimary()) {
	        			data.setLeftids(new ArrayList<>());
	        		}
	        		else {
	        			data.setRightids(new ArrayList<>());
	        		}
	        		
	        		BigDecimal primaryAmount = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;	        		
	        		String primaryRowOid = row[0].toString();
	        		if(getReco().isLeftGridPrimary()) {
	        			data.getLeftids().add(primaryRowOid);	     
	        		}
	        		else {
	        			data.getRightids().add(primaryRowOid);	     
	        		}	        				        		
	        		log.trace("[{} - {} - {}]", (count+1), primaryRowOid, primaryAmount);
	        		
	                Map<String, Object> map = buildConditionAttributeValuesMap(row, secondaryBuilder);
	                secondaryBuilder.setCommonAttributeValuesMap(map);
	                
	                if(useCreditDebit) {
	        			if(StringUtils.hasText(secondaryBuilder.opositecdValue) && secondaryBuilder.opositecdValue.equals(secondaryBuilder.debitValue)) {
	                		primaryAmount = BigDecimal.ZERO.subtract(primaryAmount);
	                	}
	        		}	                
	                List<BigDecimal> amounts = buildSecondaryAmounts(primaryAmount, secondaryBuilder, isLeftPlusRight, useCreditDebit);
	                                  
	                secondaryBuilder.setForCumulatedBalance(true);
	                
	                int scount = getSecondaryGridRowCount();	    			
	    			List<Object[]> srows = getSecondaryGridNextRows();
	                int secondaryPageCount = srows.size();	                       	                
	                @SuppressWarnings("unused")
					List<String> secondaryRowOids = new ArrayList<>(0);	        				        		
	                	                
	                LocalDateTime startDate = LocalDateTime.now();	                
	                
	                while(!reconciliated && secondaryPageCount > 0 && scount > 0 && !stop) {
	                	scount--;
	                	if(stop) { return;}
	                    int cardinality = secondaryPageCount;
	                    
	                    Set<Integer> elts = new HashSet<>(0);
	            		for(int i = 0; i < secondaryPageCount; i++) {
	            			elts.add(i);
	            		}	
	                    
	            		long combinaisonCount = 1L;
	            		CombinaisonUtil<Integer> util = new CombinaisonUtil<>();
	            		if(this.getReco().isUseCombinations()) {		            		
		            		combinaisonCount = Long.MAX_VALUE;
		            		if(cardinality <= 170) {
		            			Double c = util.combinaisonCount(cardinality); 
		            			combinaisonCount = c < Double.valueOf(combinaisonCount) ? c.longValue() : combinaisonCount;
		            		}
		            		
		            		if(getListener() != null && combinaisonCount > 5000) {
		            			//listener.createSubInfo(null, "Line" + count);
		            			//listener.startSubInfo(combinaisonCount);		            			
		            			//listener.OnChange();
		            			listener.getInfo().getCurrentSubTask().setCurrentSubTask(new TaskProgressInfo());
		            			listener.getInfo().getCurrentSubTask().getCurrentSubTask().setId(null);
		            			listener.getInfo().getCurrentSubTask().getCurrentSubTask().setName("Line" + count);
		            			
		            			listener.getInfo().getCurrentSubTask().getCurrentSubTask().setStepCount(combinaisonCount);
		            			listener.getInfo().getCurrentSubTask().getCurrentSubTask().setCurrentStep(0);
		            			
		            			listener.OnChange();
			                }
	            		}
	            			            		
	            		while(cardinality > 0 &&!reconciliated && !stop) {
	            			if(stop) { return;}	
	            			log.trace("Cardinality = {}", cardinality);
		            		Set<Set<Integer>> combinations = util.combinaison(elts, cardinality--);
		            		Iterator<Set<Integer>> iterator = combinations.iterator();
		            		while(iterator.hasNext() && !stop) {
		            			if(isMaxDurationReash(maxDuration, startDate)) {
	            					cardinality = 0;
	            					scount = 0;
	            					break;
	            				}
		            			if(stop) { return;}	
		            			Set<Integer> set = iterator.next();
		            			log.trace("Set = {}", set);
		            			BigDecimal setAmount = BigDecimal.ZERO;
		            			List<String> setOids = new ArrayList<>(0);
		            			for(Integer i : set) {
		            				if(isMaxDurationReash(maxDuration, startDate)) {
		            					cardinality = 0;
		            					scount = 0;
		            					break;
		            				}
		            				if(stop) { return;}	
		            				Object[] r = srows.get(i);
		            				setOids.add(r[0].toString());
		            				BigDecimal a = r[1] != null ? new BigDecimal(r[1].toString()) : BigDecimal.ZERO;
				                	if(useCreditDebit) {
				                		String dc = (String)r[2];
					                	if(StringUtils.hasText(dc) && dc.equals(secondaryBuilder.debitValue)) {
					                		setAmount = setAmount.subtract(a);
					                	}else {
					                		setAmount = setAmount.add(a);
					                	}
				                	}
				                	else {
				                		setAmount = setAmount.add(a);
				                	}
		                		}
		            			secondaryRowOids = new ArrayList<>(setOids);
		            			boolean valid = validateAmount(primaryAmount, setAmount, amounts, data);
			                	if(valid) {
			                		if(stop) { return;}			                		
			                		log.trace("{} - {} => {}", primaryRowOid, setOids, setAmount);
			                		if(getReco().isLeftGridPrimary()) {
			                			data.setRightids(new ArrayList<>(setOids));
			    	        		}
			    	        		else {
			    	        			data.setLeftids(new ArrayList<>(setOids));
			    	        		}	      
			                		data.setSecondaryids(new ArrayList<>(setOids));
			                		if(stop) { return;}
			                		boolean isOk = BigDecimal.ZERO.compareTo(data.getBalanceAmount()) == 0 || this.getReco().getModel().isAllowWriteOff();			                		
			                		if(isOk) {		                			
			                			buildWriteOffData(data);
			                			buildEnrichmentData(data);
			                			reconciliate(data, RunModes.A, true);
				                		reconciliated = true;			                		
				                		break;
			                		}
			                		else {
			                			log.debug("Unable to reconciliate.");
			                		}
			                	}  
			                	
//			                	if(listener != null && listener.getInfo().getCurrentSubTask() != null) {
//			                		listener.getInfo().getCurrentSubTask().setCurrentStep(listener.getInfo().getCurrentSubTask().getCurrentStep() + 1);			                		
//			            			if(listener.getInfo().getCurrentSubTask().getCurrentStep() % 500 == 0) {
//			            				listener.OnChange();
//			            			}
//				                }
			                	if(listener != null && listener.getInfo().getCurrentSubTask() != null 
			                			&& listener.getInfo().getCurrentSubTask().getCurrentSubTask() != null) {
			                		listener.getInfo().getCurrentSubTask().getCurrentSubTask().setCurrentStep(listener.getInfo().getCurrentSubTask().getCurrentSubTask().getCurrentStep() + 1);			                		
			            			if(listener.getInfo().getCurrentSubTask().getCurrentSubTask().getCurrentStep() % 5000 == 0) {
			            				listener.OnChange();
			            			}
				                }
			                	
			                	if(!this.getReco().isUseCombinations()) {
			                		cardinality = 0;
			                	}
		            		}
	            		}
	            		
//	            		if(getListener() != null && listener.getInfo().getCurrentSubTask() != null) {
//	            			getListener().endSubInfo();
//		                }
	            		if(getListener() != null && listener.getInfo().getCurrentSubTask() != null 
	                			&& listener.getInfo().getCurrentSubTask().getCurrentSubTask() != null) {
	            			getListener().endSubInfo();
		                }
	                }
	                if(listener != null) {
	                	listener.nextSubInfoStep(1);
	                }              
	            }
	        	if(count > 0) {    
	        		rows = getPrimaryGridNextRows();
	    			pageCount = rows.size();
	        	}
	        }  
	        
	        if(transactionCount > 0) {
	        	reconciliationUnionModelService.reconciliateAndCommit(datas, "B-CEPHAL", RunModes.A, false);			
				transactionCount = 0;
				datas.clear();
			}
	        refreshPublication();	
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

	private boolean isMaxDurationReash(Integer maxDuration, LocalDateTime startDate) {
		if(maxDuration != null) {
			Long duration = Duration.between(startDate, LocalDateTime.now()).getSeconds();
			if(Long.valueOf(maxDuration).compareTo(duration) < 0) {
				return true;
			}
		}
		return false;
	}
	
}

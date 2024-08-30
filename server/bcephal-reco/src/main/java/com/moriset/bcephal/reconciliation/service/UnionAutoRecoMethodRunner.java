package com.moriset.bcephal.reconciliation.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.ReconciliationParameterCodes;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.EnrichmentValue;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.filters.PeriodGranularity;
import com.moriset.bcephal.domain.filters.PeriodOperator;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.domain.filters.ReconciliationDataFilter;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleRowType;
import com.moriset.bcephal.grid.domain.SmartMaterializedGridColumn;
import com.moriset.bcephal.grid.domain.UnionGridColumn;
import com.moriset.bcephal.grid.repository.UnionGridColumnRepository;
import com.moriset.bcephal.grid.service.DbTableManager;
import com.moriset.bcephal.grid.service.SpotService;
import com.moriset.bcephal.grid.service.UnionGridFilter;
import com.moriset.bcephal.grid.service.UnionGridService;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.reconciliation.domain.AutoReco;
import com.moriset.bcephal.reconciliation.domain.AutoRecoLog;
import com.moriset.bcephal.reconciliation.domain.AutoRecoRankingItem;
import com.moriset.bcephal.reconciliation.domain.ReconciliationCondition;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModelSide;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionData;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionModelEnrichment;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionModelGrid;
import com.moriset.bcephal.reconciliation.domain.WriteOffFieldValueType;
import com.moriset.bcephal.reconciliation.domain.WriteOffUnionField;
import com.moriset.bcephal.reconciliation.domain.WriteOffUnionFieldValue;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
@Scope(scopeName= ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UnionAutoRecoMethodRunner {

	AutoReco reco;
	
	AutoRecoLog autoRecoLog;
	
	TaskProgressListener listener;
	
	boolean stop;
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	ParameterRepository parameterRepository;
		
	@Autowired
	ReconciliationUnionModelService reconciliationUnionModelService;
	
	@Autowired
	ReconciliationModelService reconciliationModelService;
	
	@Autowired
	UnionGridService unionGridService;
	
	@Autowired
	SpotService spotService;
	
	@Autowired
	UnionGridColumnRepository unionGridColumnRepository;
	
	GrilleDataFilter primaryFilter;
	AutoRecoUnionQueryBuilder primaryBuilder;
	String primaryView;
	String primaryColumns = "";
	
	GrilleDataFilter secondaryFilter;
	AutoRecoUnionQueryBuilder secondaryBuilder;	
	String SecondaryView;
    String secondaryColumns = "";
    
    protected String primaryRowId;
	
	protected List<String> secondaryRowIds;
	
    
    //@jakarta.transaction.Transactional(value = TxType.REQUIRED)
    @Transactional(propagation = Propagation.REQUIRED)
	public void run() throws Exception {
		log.debug("Auto Reco : {} - Running....", this.reco.getName());
		int count = 0;
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
	        
	        boolean isLeftPlusRight = this.reco.getUnionModel().getBalanceFormula().isLeftPlusRight();
	        boolean useCreditDebit = this.reco.getUnionModel().isUseDebitCredit();			
	        boolean leftGridIsprimary = getReco().isLeftGridPrimary();
	        
	        while(pageCount > 0 && count > 0 && !stop) {
	        	if(stop) { return;}
	        	for(Object[] row : rows) {       
	        		if(stop) { return;}
	        		ReconciliationUnionData data = buildReconciliationData();
	        		boolean reconciliated = false;
	        		count--;
	        		if(leftGridIsprimary) {
	        			data.setLeftids(new ArrayList<>());
	        		}
	        		else {
	        			data.setRightids(new ArrayList<>());
	        		}
	        		BigDecimal primaryAmount = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
	        		primaryRowId = row[0].toString();
	        		if(leftGridIsprimary) {
	        			data.getLeftids().add(primaryRowId);	      
	        		}
	        		else {
	        			data.getRightids().add(primaryRowId);	      
	        		}
	        		        		        		
	        		log.trace("[{} - {} - {}]", (count+1), primaryRowId, row[1]);
	        		
	        		Map<String, Object> map = buildConditionAttributeValuesMap(row, secondaryBuilder);
	                secondaryBuilder.setCommonAttributeValuesMap(map);
	        		      	                                
	                if(useCreditDebit) {
	                	if(StringUtils.hasText(secondaryBuilder.opositecdValue) && secondaryBuilder.opositecdValue.equals(secondaryBuilder.debitValue)) {
	                		primaryAmount = BigDecimal.ZERO.subtract(primaryAmount);
	                	}
	        		}	                
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
		                	String soid = r[0].toString();
		                	if(!soid.equals(primaryRowId)) {	  
		                		secondaryRowIds.add(soid);
			                	BigDecimal setAmount = r[1] != null ? new BigDecimal(r[1].toString()) : BigDecimal.ZERO;
			                	if(useCreditDebit) {
			                		String dc = (String)r[2];
				                	if(StringUtils.hasText(dc) && dc.equals(secondaryBuilder.debitValue)) {
				                		setAmount = BigDecimal.ZERO.subtract(setAmount);
				                	}else {
				                		setAmount = BigDecimal.ZERO.add(setAmount);
				                	}
			                	}
			                	
			                	boolean valid = validateAmount(primaryAmount, setAmount, amounts, data);
			                	if(valid) {
			                		if(stop) { return;}
			                		boolean isOk = BigDecimal.ZERO.compareTo(data.getBalanceAmount()) == 0 || this.reco.getUnionModel().isAllowWriteOff();			                		
			                		if(isOk) {
			                			buildWriteOffData(data);
			                			buildEnrichmentData(data);
			                			reconciliate(data, primaryRowId, soid, setAmount, primaryView, SecondaryView);			                			
				                		reconciliated = true;
				                		break;
			                		}
			                		else {
			                			log.debug("Unable to reconciliate.");
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
	        	reconciliationUnionModelService.reconciliateAndCommit(datas, "B-CEPHAL", RunModes.A, false);			
				transactionCount = 0;
				datas.clear();
			}
	        if(setpCount > 0) {
	        	if(listener != null) {
                	listener.nextSubInfoStep(setpCount);
                }
	        	setpCount = 0;
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
    
    
    protected void refreshPublication() {
//    	try {
//	    	ReconciliationUnionData data = buildReconciliationData();	   
//	    	reconciliationUnionModelService.refreshPublication(data);
//    	} catch (Exception e) {
//			log.error("", e);			
//		}
	}

	int batchSetpNbr = 5;
	int setpCount = 0;
    
    
    
    
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
				item.setSecondaryDbColumn(column != null ? column.getDbColAliasName() : null);
			}
			else {
				GrilleColumn column = grid.getGrille().getColumnById(item.getColumnId2());
				item.setColumn2(column);
				item.setSecondaryDbColumn(column != null ? column.getUniverseTableColumnName() : null);
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
					item.setValue(row[i + 2]);
					map.put(item.getSecondaryDbColumn(), row[i + 2]);					
				}
				if (columnType.isPeriod()) {
					Date date = null;
					Object ob = row[i + 2];
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
        	Object cd = row[i + 2];
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
			
						
//			UnionGridColumn column = item.getSide1().isLeft() ? reco.getUnionModel().getLeftGrid().getGrid().getColumnById(item.getColumnId1()) : reco.getUnionModel().getRigthGrid().getGrid().getColumnById(item.getColumnId1());
//			item.setUnionGridColumn1(column);
//			
//			item.setUnionGridColumn2(item.getSide2().isLeft() ? reco.getUnionModel().getLeftGrid().getGrid().getColumnById(item.getColumnId2()) : reco.getUnionModel().getRigthGrid().getGrid().getColumnById(item.getColumnId2()));
//			item.setPrimaryDbColumn(column != null ? column.getDbColAliasName() : null);
			
//			ReconciliationModelSide secondaryside = reco.isLeftGridPrimary() ? ReconciliationModelSide.RIGHT : ReconciliationModelSide.LEFT;
//			UnionGridColumn secondarycolumn = null;
//			if(secondaryside == item.getSide1()) {
//				secondarycolumn = secondaryside.isLeft() ? reco.getUnionModel().getLeftGrid().getGrid().getColumnById(item.getColumnId1()) : reco.getUnionModel().getRigthGrid().getGrid().getColumnById(item.getColumnId1());
//				item.setSecondaryToPrimary(true);
//			}
//			else {
//				secondarycolumn = secondaryside.isLeft() ? reco.getUnionModel().getLeftGrid().getGrid().getColumnById(item.getColumnId2()) : reco.getUnionModel().getRigthGrid().getGrid().getColumnById(item.getColumnId2());
//				item.setSecondaryToPrimary(false);
//			}
//			item.setSecondaryDbColumn(secondarycolumn != null ? secondarycolumn.getDbColAliasName() : null);
			
//			if(column != null) {
//				item.setDimensionType(column.getType());
//				if(column.getType().isAttribute() /*&& column.getDimensionId() != null*/) {
//					item.setDimensionType(column.getType());
//					item.setValue(row[i + 2]);
//					map.put(item.getSecondaryDbColumn(), row[i + 2]);					
//				}
//				if (column.getType().isPeriod()) {
//					Date date = null;
//					Object ob = row[i + 2];
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
//		}
//		
//        if(builder.useCreditDebit) {
//        	Object cd = row[i + 2];
//        	if(builder.debitValue.equals(cd)) {
//        		//map.put(builder.cdColumn, builder.creditValue);
//        		builder.cdValue = builder.creditValue;
//        		builder.opositecdValue = builder.debitValue;
//        	}
//        	else {
//        		//map.put(builder.cdColumn, builder.debitValue);
//        		builder.cdValue = builder.debitValue;
//        		builder.opositecdValue = builder.creditValue;
//        	}
//        }
//		return map;
	}
    
	protected List<BigDecimal> buildSecondaryAmounts(BigDecimal primaryAmount, AutoRecoUnionQueryBuilder builder,
			boolean isLeftPlusRight, boolean useCreditDebit) {
		
		boolean acceptWriteOff = this.reco.getUnionModel().isAllowWriteOff();
		boolean balanceIsZero = this.reco.isBalanceIsZero() || !acceptWriteOff;
        boolean balanceIsAmountInterval = this.reco.isBalanceIsAmountInterval() && acceptWriteOff;
        boolean balanceIsAmountPercentage = this.reco.isBalanceIsAmountPercentage() && acceptWriteOff;
		List<BigDecimal> amounts = new ArrayList<>(0);
		if(useCreditDebit && builder.cdValue.equals(builder.debitValue)) {
    		primaryAmount = BigDecimal.ZERO.subtract(primaryAmount);
    	}      
                        
        BigDecimal secondaryAmount = primaryAmount;
        if(isLeftPlusRight) {
        	secondaryAmount = BigDecimal.ZERO.subtract(primaryAmount);
        }
        
        if(balanceIsZero) {
        	amounts.add(secondaryAmount);
        	amounts.add(secondaryAmount);
        }
        else if(balanceIsAmountInterval) {
        	amounts.add(secondaryAmount.add(this.reco.getConditionMinValue()));
        	amounts.add(secondaryAmount.add(this.reco.getConditionMaxValue()));
        }
        else if(balanceIsAmountPercentage) {
        	BigDecimal min = secondaryAmount.abs().multiply(this.reco.getConditionMinValue()).divide(new BigDecimal(100));
        	BigDecimal max = secondaryAmount.abs().multiply(this.reco.getConditionMaxValue()).divide(new BigDecimal(100));
        	
        	amounts.add(secondaryAmount.add(min));
        	amounts.add(secondaryAmount.add(max));        	
        }
        
        builder.setAmount1(amounts.get(0));
        builder.setAmount2(amounts.get(1));
		return amounts;
	}
	
	protected boolean validateAmount(BigDecimal primaryAmount, BigDecimal secondaryAmount, List<BigDecimal> amounts, ReconciliationUnionData data) {
		boolean balanceIsZero = this.reco.isBalanceIsZero();
        boolean balanceIsAmountInterval = this.reco.isBalanceIsAmountInterval();
        boolean balanceIsAmountPercentage = this.reco.isBalanceIsAmountPercentage();
        boolean isLeftPlusRight = this.reco.getUnionModel().getBalanceFormula().isLeftPlusRight();
        boolean rightGridAsPrimary = this.reco.isCumulatedLeft();
        if(rightGridAsPrimary) {        	
        	data.setLeftAmount(secondaryAmount);
        	data.setRigthAmount(primaryAmount);
        }
        else {
        	data.setLeftAmount(primaryAmount);
        	data.setRigthAmount(secondaryAmount);
        } 
                
        BigDecimal balance = isLeftPlusRight ? data.getLeftAmount().add(data.getRigthAmount()) : data.getLeftAmount().subtract(data.getRigthAmount());
        data.setWriteOffAmount(balance);
        
        if(balanceIsZero) {
        	return balance.compareTo(BigDecimal.ZERO) == 0;
        }
        
        if(balanceIsAmountInterval) {        	
        	return balance.compareTo(this.reco.getConditionMinValue()) >= 0 && balance.compareTo(this.reco.getConditionMaxValue()) <= 0;
        }
        if(balanceIsAmountPercentage) {   
        	BigDecimal min = primaryAmount.abs().multiply(this.reco.getConditionMinValue()).divide(new BigDecimal(100));
        	BigDecimal max = primaryAmount.abs().multiply(this.reco.getConditionMaxValue()).divide(new BigDecimal(100));
        	return balance.compareTo(min) >= 0 && balance.compareTo(max) <= 0;
        }
        
		return false;
	}
	
	protected void reconciliate(ReconciliationUnionData data, String oid, String soid, BigDecimal amount, String primaryView, String SecondaryView) throws Exception {
		log.trace("{} - {} => {}", oid, soid, amount);
		if(getReco().isLeftGridPrimary()) {
			data.setRightids(new ArrayList<>());
			data.getRightids().add(soid);	      
		}
		else {
			data.setLeftids(new ArrayList<>());
			data.getLeftids().add(soid);	      
		}
		data.setPrimaryids(new ArrayList<>());
		data.getPrimaryids().add(oid);
		data.setSecondaryids(new ArrayList<>());
		data.getSecondaryids().add(soid);
		if(stop) { return;}
		reconciliate(data, RunModes.A, true);
	}
	
	protected void reconciliateAndCommit(RunModes mode) throws Exception {
		reconciliationUnionModelService.reconciliateAndCommit(datas, "B-CEPHAL", mode, false);
	}
	
	int transactionCount = 0;
	List<ReconciliationUnionData> datas = new ArrayList<>();
	protected void reconciliate(ReconciliationUnionData data, RunModes mode, boolean refreshView) throws Exception {
//		data.setLeftMeasureId(reco.getUnionModel().getLeftGrid().getMeasureColumnId());
//		data.setRightMeasureId(reco.getUnionModel().getRigthGrid().getMeasureColumnId());
//		
//		data.setAllowPartialReco(reco.getUnionModel().isAllowPartialReco());
//		data.setLeftPartialRecoAttributeId(reco.getUnionModel().getLeftGrid().getPartialRecoTypeColumnId());
//		data.setRightPartialRecoAttributeId(reco.getUnionModel().getRigthGrid().getPartialRecoTypeColumnId());
//		
//		data.setLeftReconciliatedMeasureId(reco.getUnionModel().getLeftGrid().getReconciliatedMeasureColumnId());
//		data.setRightReconciliatedMeasureId(reco.getUnionModel().getRigthGrid().getReconciliatedMeasureColumnId());
//		
//		data.setLeftRemainningMeasureId(reco.getUnionModel().getLeftGrid().getRemainningMeasureColumnId());
//		data.setRightRemainningMeasureId(reco.getUnionModel().getRigthGrid().getRemainningMeasureColumnId());
		
		boolean perf = data.isAllowPartialReco() 
				&& data.getLeftPartialRecoAttributeId() != null && data.getLeftReconciliatedMeasureId() != null && data.getLeftRemainningMeasureId() != null
				&& data.getRightPartialRecoAttributeId() != null && data.getRightReconciliatedMeasureId() != null && data.getRightRemainningMeasureId() != null;
		boolean performWo = this.reco.getUnionModel().isAllowWriteOff() && data.getWriteOffAmount() != null 
				&& data.getWriteOffAmount().compareTo(BigDecimal.ZERO) != 0;
		
		data.setPerformPartialReco(performWo ? !performWo : perf); 
				
		secondaryBuilder.excludedOids.addAll(data.getSecondaryids());
		transactionCount++;
		datas.add(data);
		if(transactionCount == reco.getBatchChunk()) {
			reconciliateAndCommit(mode);
			entityManager.clear();
			transactionCount = 0;
			datas.clear();
			
			int count = secondaryBuilder.excludedOids.size();
			if(refreshView && count >= 1000) {				
				String dropSql = "REFRESH MATERIALIZED VIEW " + SecondaryView;	
				Query query = entityManager.createNativeQuery(dropSql);
				query.executeUpdate();
				secondaryBuilder.excludedOids.clear();				
			}
		}
		
//		EntityTransaction tx = entityManager.getTransaction();
//		try{
//			tx.begin();	
//			service.loadSettings(data);
//			service.reconciliateWithoutCommint(data, mode);
//			log.trace("Reconciliate...");
//			if(secondaryBuilder.excludedOids.size() >= 1000 && refreshView) {
//				log.trace("Reconciliate - Refresh view...");
//				String dropSql = "REFRESH MATERIALIZED VIEW " + SecondaryView;	
//				Query query = entityManager.createNativeQuery(dropSql);
//				query.executeUpdate();
//				secondaryBuilder.excludedOids = new ArrayList<Long>(0);
//			}						
//			tx.commit();	
//		}
//		catch (Exception e) {
//			log.error("Reconciliate - ", e);
//			throw new BcephalException("Unable to reconciliate...");
//		}
//		finally {
//			if (tx != null && tx.isActive()) {
//				tx.rollback();
//			}
//		}
	}
	
	protected List<Object[]> getPrimaryGridNextRows() throws Exception {
		@SuppressWarnings("unused")
		int pageSize = 50000;
        String sql = "SELECT ".concat(primaryColumns).concat(" FROM ").concat(primaryView);
        String order = buildOrderPart(this.reco.getPrimarySide());
        if(org.springframework.util.StringUtils.hasText(order)) {
        	sql += order;
        }
        else {
        	sql += " ORDER BY ID ASC";
        }
        Query query = entityManager.createNativeQuery(sql);
        //query.setFirstResult(1);
		//query.setMaxResults(pageSize);		
        @SuppressWarnings("unchecked")
		List<Object[]> rows = query.getResultList();
        return rows;
	}
	
		
	private String buildOrderPart(ReconciliationModelSide side) {
		String sql = "";
		String coma = "";	
		ReconciliationUnionModelGrid grid = side.isLeft() ? reco.getUnionModel().getLeftGrid() : reco.getUnionModel().getRigthGrid();
		for (AutoRecoRankingItem item : this.reco.getSortedRankingItems()) {
			if (item.getSide() == side) {
				if(grid.isUnion()) {
					UnionGridColumn column = grid.getGrid().getColumnById(item.getDimensionId());	
					if(column != null) {
						SmartMaterializedGridColumn c = column.getColumnByGridId(grid.getGrid().getItems().get(0).getGrid().getId());
						String col = c != null ? c.getDbColumnName() : "null";
						String part = col + " " + (item.isDescending() ? "DESC" : "ASC");
						if(StringUtils.hasText(part)) {
							sql += coma + part;
							coma = ", ";	
						}	
					}
				}
				else {				
					String part = item.getSql();
					if(StringUtils.hasText(part)) {
						sql += coma + part;
						coma = ", ";	
					}	
				}
			}
		}		
		if (StringUtils.hasText(sql)){
			sql = " ORDER BY " + sql;
		}
		return sql;
	}

	protected int getSecondaryGridRowCount() throws Exception {
		String countSql = secondaryBuilder.buildQuery();
        countSql = "SELECT COUNT(1) FROM (" + countSql + ") AS A";
		Query query = entityManager.createNativeQuery(countSql);
		Number num = (Number) query.getSingleResult();
		return num.intValue();
	}
	
	protected List<Object[]> getSecondaryGridNextRows() throws Exception {
		@SuppressWarnings("unused")
		int secondaryPageSize = 50000;
		String sql = secondaryBuilder.buildQuery();
        Query query = entityManager.createNativeQuery(sql);
        //query.setFirstResult(1);
		//query.setMaxResults(secondaryPageSize);		
        @SuppressWarnings("unchecked")
		List<Object[]> srows = query.getResultList();
        return srows;
	}
	
	protected ReconciliationUnionData buildReconciliationData() {
		ReconciliationUnionData data = new ReconciliationUnionData();
        data.setReconciliationId(reco.getUnionModel().getId());
        data.setAddAutomaticManual(reco.getUnionModel().isAddAutomaticManual());
        data.setAddRecoDate(reco.getUnionModel().isAddRecoDate());
        data.setAddUser(reco.getUnionModel().isAddUser());
        data.setUseDebitCredit(reco.getUnionModel().isUseDebitCredit());
        data.setAllowReconciliatedAmountLog(reco.getUnionModel().getAllowReconciliatedAmountLog());
        data.setReconciliatedAmountLogGridId(reco.getUnionModel().getReconciliatedAmountLogGridId());
        
        data.setLeftMeasureId(reco.getUnionModel().getLeftGrid().getMeasureColumnId());
        data.setRightMeasureId(reco.getUnionModel().getRigthGrid().getMeasureColumnId());
        
        data.setLeftRecoTypeId(reco.getUnionModel().getLeftGrid().getRecoTypeColumnId());
		data.setRightRecoTypeId(reco.getUnionModel().getRigthGrid().getRecoTypeColumnId());
		
		data.setLeftPartialRecoAttributeId(reco.getUnionModel().getLeftGrid().getPartialRecoTypeColumnId());
		data.setRightPartialRecoAttributeId(reco.getUnionModel().getRigthGrid().getPartialRecoTypeColumnId());
		
		data.setLeftReconciliatedMeasureId(reco.getUnionModel().getLeftGrid().getReconciliatedMeasureColumnId());
		data.setRightReconciliatedMeasureId(reco.getUnionModel().getRigthGrid().getReconciliatedMeasureColumnId());
		
		data.setLeftRemainningMeasureId(reco.getUnionModel().getLeftGrid().getRemainningMeasureColumnId());
		data.setRightRemainningMeasureId(reco.getUnionModel().getRigthGrid().getRemainningMeasureColumnId());
                        
        data.setLeftRecoDateId(reco.getUnionModel().getLeftGrid().getRecoDateColumnId());
		data.setRightRecoDateId(reco.getUnionModel().getRigthGrid().getRecoDateColumnId());
		
		data.setLeftUserColumnId(reco.getUnionModel().getLeftGrid().getUserColumnId());
		data.setRightUserColumnId(reco.getUnionModel().getRigthGrid().getUserColumnId());
		
		data.setLeftModeColumnId(reco.getUnionModel().getLeftGrid().getModeColumnId());
		data.setRightModeColumnId(reco.getUnionModel().getRigthGrid().getModeColumnId());
		
		data.setRecoSequenceId(reco.getUnionModel().getRecoSequenceId());
		data.setPartialRecoSequenceId(reco.getUnionModel().getPartialRecoSequenceId());
        
        data.setPrimaryView(primaryView);
        data.setSecondaryView(SecondaryView);   
        
        data.setLeftGridType(reco.getUnionModel().getLeftGrid().getType());
		data.setRightGridType(reco.getUnionModel().getRigthGrid().getType());
		
		if(reco.getUnionModel().getLeftGrid().isUnion()) {
			data.setLeftGridId(reco.getUnionModel().getLeftGrid().getGrid().getId());
        	data.setLeftGrid(reconciliationUnionModelService.getUnionGrid(data.getLeftGridType(), data.getLeftGridId()));
        }
        else {
        	data.setLeftGridId(reco.getUnionModel().getLeftGrid().getGrille().getId());
        	data.setRightGrille(reconciliationUnionModelService.getGrille(data.getLeftGridType(), data.getLeftGridId()));
        }
        
		if(reco.getUnionModel().getRigthGrid().isUnion()) {			
			data.setRightGridId(reco.getUnionModel().getRigthGrid().getGrid().getId());
			data.setRightGrid(reconciliationUnionModelService.getUnionGrid(data.getRightGridType(), data.getRightGridId()));
        }
        else {
        	data.setRightGridId(reco.getUnionModel().getRigthGrid().getGrille().getId());
        	data.setRightGrille(reconciliationUnionModelService.getGrille(data.getRightGridType(), data.getRightGridId()));
        }
		
		if(this.reco.getUnionModel().isAllowWriteOff() && this.reco.getUnionModel().getWriteOffModel() != null) {
			data.setWriteOffGridId(this.reco.getUnionModel().getWriteOffModel().getWriteOffGridId());
			data.setWriteOffSide(this.reco.getUnionModel().getWriteOffModel().getWriteOffSide());
			data.setWriteOffTypeColumnId(this.reco.getUnionModel().getWriteOffModel().getWriteOffTypeColumnId());
			data.setWriteOffTypeValue(this.reco.getUnionModel().getWriteOffModel().getWriteOffTypeValue());
			
			if(this.reco.getUnionModel().getWriteOffModel().isUseGridMeasure()) {
				if(this.reco.getUnionModel().getWriteOffModel().getWriteOffSide() == ReconciliationModelSide.LEFT) {
					data.setWriteOffMeasureId(this.reco.getUnionModel().getLeftGrid().getMeasureColumnId());
				}
				else if(this.reco.getUnionModel().getWriteOffModel().getWriteOffSide() == ReconciliationModelSide.RIGHT) {
					data.setWriteOffMeasureId(this.reco.getUnionModel().getRigthGrid().getMeasureColumnId());
				}
			}			
			else if(this.reco.getUnionModel().getWriteOffModel().getWriteOffMeasureId() != null) {
				data.setWriteOffMeasureId(this.reco.getUnionModel().getWriteOffModel().getWriteOffMeasureId());
			}
		}
		
		data.setLeftNeutralizationAttributeId(reco.getUnionModel().getLeftGrid().getNeutralizationTypeColumnId());
		data.setRightNeutralizationAttributeId(reco.getUnionModel().getRigthGrid().getNeutralizationTypeColumnId());        
        data.setNeutralizationSequenceId(reco.getUnionModel().getNeutralizationSequenceId());
		        
        reconciliationUnionModelService.loadSettings(data);
        return data;
	}
			
	protected int buildViews() throws Exception {
		boolean useCreditDebit = this.reco.getUnionModel().isUseDebitCredit();		
		this.primaryFilter = buidPrimaryFilter(); 
		this.reco.setConditions(this.reco.getSortedConditions()); 
        this.secondaryFilter = buidSecondaryFilter(); 
                 
        this.primaryColumns = "";
        this.secondaryColumns = "";
		this.primaryView = "RECOMODEL_P_VIEW_" + this.reco.getId();
		this.SecondaryView = "RECOMODEL_S_VIEW_" + this.reco.getId();
					
		return createViews(useCreditDebit);
	}	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void createViewsFirst() throws Exception {
		String dropSql = "DROP MATERIALIZED VIEW IF EXISTS " + primaryView;	
		Query query = entityManager.createNativeQuery(dropSql);
		query.executeUpdate();
				
		primaryBuilder.setGridDataFilter(primaryFilter);
		String primarySql = primaryBuilder.buildQuery();
		log.trace("Primary view creation sql : {}", primarySql);
		String createSql = "CREATE MATERIALIZED VIEW ".concat(primaryView).concat(" AS ").concat(primarySql);
		for(String key : primaryBuilder.getParameters().keySet()) {
			String value = buildParamAsString(primaryBuilder.getParameters().get(key));
			createSql = createSql.replace(":" + key, value);
		}	
		query = entityManager.createNativeQuery(createSql);			
		query.executeUpdate();
		
		
		
		dropSql = "DROP MATERIALIZED VIEW IF EXISTS " + SecondaryView;		
		query = entityManager.createNativeQuery(dropSql);
		query.executeUpdate();
			
		secondaryBuilder.setGridDataFilter(secondaryFilter);
		String secondarySql = secondaryBuilder.buildQuery();
		log.trace("Secondary view creation sql : {}", secondarySql);
		createSql = "CREATE MATERIALIZED VIEW ".concat(SecondaryView).concat(" AS ").concat(secondarySql);
		for(String key : secondaryBuilder.getParameters().keySet()) {
			String value = buildParamAsString(secondaryBuilder.getParameters().get(key));
			createSql = createSql.replace(":" + key, value);
		}
		query = entityManager.createNativeQuery(createSql);
		query.executeUpdate();
	}
	
	private String buildParamAsString(Object param) {
		return param instanceof Number ? param.toString() 
				: param instanceof BigDecimal ? ((BigDecimal)param).toPlainString()
				: param instanceof Date ? "'" + new SimpleDateFormat("yyyy-MM-dd").format((Date)param)+ "'"
				: "'" + param.toString() + "'";
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected int createViews(boolean useCreditDebit) {
		try{		
			createViewsFirst();
			DbTableManager generator = new DbTableManager(entityManager);
			primaryColumns = "";
			String coma = "";
			List<String> pTableColumns = generator.getMaterializedViewColumns(primaryView);// generator.getTableColumns(primaryView);
			for(String column : pTableColumns) {
				primaryColumns += coma + column;
				coma = ",";
			}			
			String index = "RECO_P_VIEW_INDEX" + this.reco.getId();
			String indexSql = "CREATE INDEX  ".concat(index).concat(" ON ").concat(primaryView).concat(" (").concat(primaryColumns).concat(")");
			Query query = entityManager.createNativeQuery(indexSql);
			query.executeUpdate(); 
			
			
			generator = new DbTableManager(entityManager);
			secondaryColumns = "";
			coma = "";
			pTableColumns = generator.getMaterializedViewColumns(SecondaryView);// generator.getTableColumns(SecondaryView);
			for(String column : pTableColumns) {
				secondaryColumns += coma + column;
				coma = ",";
			}			
			index = "RECOMODEL_S_VIEW_INDEX" + this.reco.getId();
			indexSql = "CREATE INDEX ".concat(index).concat(" ON ").concat(SecondaryView).concat(" (").concat(secondaryColumns).concat(")");
			query = entityManager.createNativeQuery(indexSql);
			query.executeUpdate();
			
			
			
			if(this.reco.isPartialReco()) {
				secondaryBuilder = new AutoRecoUnionViewQueryBuilder(this.reco, true, entityManager, SecondaryView, this.reco.getSecondarySide(), true);
				secondaryBuilder.setMeasure(getSecondaryRemainningMeasure());
				secondaryBuilder.setMeasure2(getSecondaryReconciliatedMeasure());
			}
			else {
				secondaryBuilder = new AutoRecoUnionViewQueryBuilder(this.reco, true, entityManager, SecondaryView);
				secondaryBuilder.setMeasure(getSecondaryMeasure());
				((AutoRecoUnionViewQueryBuilder)secondaryBuilder).setMeasure2Col(null);
			}
			
	        this.secondaryBuilder.setParameterRepository(parameterRepository);
			if(useCreditDebit) {
	        	secondaryBuilder.useCreditDebit = primaryBuilder.useCreditDebit;
	        	secondaryBuilder.debitValue = primaryBuilder.debitValue;
	        	secondaryBuilder.creditValue = primaryBuilder.creditValue;
	        	secondaryBuilder.cdValue = primaryBuilder.cdValue;
	        	secondaryBuilder.cdAttribute = getSecondaryCdAttribute();
	        	secondaryBuilder.cdColumn = secondaryBuilder.cdAttribute != null ? secondaryBuilder.cdAttribute.getUniverseTableColumnName() : null;
	        } 
									
			String countSql = "SELECT COUNT(1) FROM ".concat(primaryView);
			query = entityManager.createNativeQuery(countSql);
			Number num = (Number) query.getSingleResult();
			return num.intValue();			
		}
		catch (Exception e) {
			log.debug("Project: {}  -  Unable to create views.", TenantContext.getCurrentTenant(), e);
			throw new BcephalException("Unable to create views");
		}
	}	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void dropViews() {
		try{	
			if(StringUtils.hasText(primaryView)) {
				String dropSql = "DROP MATERIALIZED VIEW IF EXISTS " + primaryView;	
				Query query = entityManager.createNativeQuery(dropSql);			
				query.executeUpdate();
			}
			if(StringUtils.hasText(SecondaryView)) {			
				String dropSql = "DROP MATERIALIZED VIEW IF EXISTS " + SecondaryView;		
				Query query = entityManager.createNativeQuery(dropSql);
				query.executeUpdate();
			}		
		}
		catch (Exception e) {
			log.debug("Unable to drop table.", e);
			throw new BcephalException("Unable to drop views");
		}		
	}
	
	protected void runScript() {
		
	}
	
	protected void buildCDValues(AutoRecoUnionQueryBuilder builder) {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_CREDIT_VALUE, ParameterType.ATTRIBUTE_VALUE);
		builder.creditValue = parameter != null && StringUtils.hasText(parameter.getStringValue()) ? parameter.getStringValue() : "C";
        parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_DEBIT_VALUE, ParameterType.ATTRIBUTE_VALUE);
        builder.debitValue = parameter != null && StringUtils.hasText(parameter.getStringValue()) ? parameter.getStringValue() : "D";
//        parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_DC_ATTRIBUTE, ParameterType.ATTRIBUTE);
//        builder.cdAttribute = parameter != null && parameter.getLongValue() != null ? new Attribute(parameter.getLongValue()) : null;        
//        builder.cdColumn = builder.cdAttribute != null ? builder.cdAttribute.getUniverseTableColumnName() : null;        
        builder.useCreditDebit = true;
	}
				
	protected GrilleDataFilter buidPrimaryFilter() {
		ReconciliationUnionModelGrid grid = reco.getPrimaryUnionModelGrid();		
		boolean useCreditDebit = this.reco.getUnionModel().isUseDebitCredit();
        this.primaryBuilder = new AutoRecoUnionQueryBuilder(this.reco, false, entityManager, this.reco.getPrimarySide(), false);
        this.primaryBuilder.setUnionGridColumnRepository(unionGridColumnRepository);
        this.primaryBuilder.setParameterRepository(parameterRepository);
        this.primaryBuilder.setSpotService(spotService);
        this.primaryBuilder.setParameterRepository(parameterRepository);
        this.primaryBuilder.setMeasure(getPrimaryMeasure());        
        if(useCreditDebit && !stop) {
        	buildCDValues(this.primaryBuilder);
        	this.primaryBuilder.cdAttribute = getPrimaryCdAttribute();
        	this.primaryBuilder.cdColumn = this.primaryBuilder.cdAttribute != null ? this.primaryBuilder.cdAttribute.getUniverseTableColumnName() : null; 
        }			
		
		GrilleDataFilter result = null;
		if(grid.isUnion()) {
			UnionGridFilter filter = new UnionGridFilter();				
			filter.setUnionGrid(grid.getGrid());
	        filter.setCredit(filter.getUnionGrid().isCredit());
	        filter.setDebit(filter.getUnionGrid().isDebit()); 	        
	        filter.setRecoAttributeId(grid.getRecoTypeColumnId());		
	        filter.setRecoData(new ReconciliationDataFilter());
	        filter.getRecoData().setRecoAttributeId(grid.getRecoTypeColumnId());
	        filter.getRecoData().setAmountMeasureId(grid.getMeasureColumnId());
	        filter.getRecoData().setPartialRecoAttributeId(grid.getPartialRecoTypeColumnId());
	        filter.getRecoData().setReconciliatedMeasureId(grid.getReconciliatedMeasureColumnId());
	        filter.getRecoData().setRemainningMeasureId(grid.getRemainningMeasureColumnId());
	        filter.getRecoData().setNoteAttributeId(grid.getNoteTypeColumnId()); 
	        result = filter;
	        this.primaryBuilder.setFilter(filter);
	        unionGridService.loadFilterClosures(filter, true);
		}
		else {
			GrilleDataFilter filter = new GrilleDataFilter();
	        filter.setGrid(grid.getGrille());
	        filter.setCredit(filter.getGrid().isCredit());
	        filter.setDebit(filter.getGrid().isDebit());	        	        
	        this.primaryBuilder.setGridBuilder(new AutoRecoGridForUnionQueryBuilder(this.reco, false, entityManager)); 
	        this.primaryBuilder.getGridBuilder().setFilter(filter);
	        this.primaryBuilder.getGridBuilder().setParameterRepository(parameterRepository);
	        GrilleColumn column = filter.getGrid().getColumnById(this.reco.getPrimaryMeasureId());	        
	        this.primaryBuilder.getGridBuilder().setMeasure(new Measure(column != null ? column.getDimensionId() : null));
	        if(useCreditDebit && !stop) {
	        	this.primaryBuilder.getGridBuilder().useCreditDebit = this.primaryBuilder.useCreditDebit;
	        	this.primaryBuilder.getGridBuilder().creditValue = this.primaryBuilder.creditValue;
	        	this.primaryBuilder.getGridBuilder().debitValue = this.primaryBuilder.debitValue;
	        	this.primaryBuilder.getGridBuilder().cdAttribute = getPrimaryCdAttribute();      
	        	this.primaryBuilder.getGridBuilder().cdColumn = this.primaryBuilder.getGridBuilder().cdAttribute != null ? this.primaryBuilder.getGridBuilder().cdAttribute.getUniverseTableColumnName() : null; 
	        }
	        setReconciliationDataFilter(filter, grid);
	        result = filter;
		}
		
		result.setRowType(GrilleRowType.NOT_RECONCILIATED);
		result.setFilter(reco.isLeftGridPrimary() ? reco.getLeftFilter() : reco.getRightFilter()); 
		result.getRecoData().setAllowPartialReco(reco.getUnionModel().isAllowPartialReco());
		result.setPage(1);
		result.setPageSize(1);
        result.setShowAll(false);	
        return result;
	}
	
	protected GrilleDataFilter buidSecondaryFilter() {
		ReconciliationUnionModelGrid grid = reco.getSecondaryUnionModelGrid();
		boolean useCreditDebit = this.reco.getUnionModel().isUseDebitCredit();
		this.secondaryBuilder = new AutoRecoUnionQueryBuilder(this.reco, false, entityManager, this.reco.getSecondarySide(), false);
        this.secondaryBuilder.setUnionGridColumnRepository(unionGridColumnRepository);
        this.secondaryBuilder.setParameterRepository(parameterRepository);
        this.secondaryBuilder.setSpotService(spotService);
        this.secondaryBuilder.setParameterRepository(parameterRepository);        
        secondaryBuilder.setMeasure(getSecondaryMeasure());
        if(useCreditDebit) {
        	secondaryBuilder.useCreditDebit = primaryBuilder.useCreditDebit;
        	secondaryBuilder.debitValue = primaryBuilder.debitValue;
        	secondaryBuilder.creditValue = primaryBuilder.creditValue;
        	secondaryBuilder.cdValue = primaryBuilder.cdValue;
        	secondaryBuilder.cdAttribute = getSecondaryCdAttribute();
        	secondaryBuilder.cdColumn = secondaryBuilder.cdAttribute != null ? secondaryBuilder.cdAttribute.getUniverseTableColumnName() : null; 
        }        
				
		GrilleDataFilter result = null;
		if(grid.isUnion()) {
			UnionGridFilter filter = new UnionGridFilter();				
			filter.setUnionGrid(grid.getGrid());
	        filter.setCredit(filter.getUnionGrid().isCredit());
	        filter.setDebit(filter.getUnionGrid().isDebit()); 
	        filter.setRecoAttributeId(grid.getRecoTypeColumnId());		
	        filter.setRecoData(new ReconciliationDataFilter());
	        filter.getRecoData().setRecoAttributeId(grid.getRecoTypeColumnId());
	        filter.getRecoData().setAmountMeasureId(grid.getMeasureColumnId());
	        filter.getRecoData().setPartialRecoAttributeId(grid.getPartialRecoTypeColumnId());
	        filter.getRecoData().setReconciliatedMeasureId(grid.getReconciliatedMeasureColumnId());
	        filter.getRecoData().setRemainningMeasureId(grid.getRemainningMeasureColumnId());
	        filter.getRecoData().setNoteAttributeId(grid.getNoteTypeColumnId()); 
	        result = filter;
	        this.secondaryBuilder.setFilter(filter);
	        unionGridService.loadFilterClosures(filter, true);
		}
		else {
			GrilleDataFilter filter = new GrilleDataFilter();
	        filter.setGrid(grid.getGrille());
	        filter.setCredit(filter.getGrid().isCredit());
	        filter.setDebit(filter.getGrid().isDebit());
	        
	        this.secondaryBuilder.setGridBuilder(new AutoRecoGridForUnionQueryBuilder(this.reco, false, entityManager)); 
	        this.secondaryBuilder.getGridBuilder().setFilter(filter);
	        this.secondaryBuilder.getGridBuilder().setParameterRepository(parameterRepository);
	        GrilleColumn column = filter.getGrid().getColumnById(this.reco.getSecondaryMeasureId());	        
	        this.secondaryBuilder.getGridBuilder().setMeasure(new Measure(column != null ? column.getDimensionId() : null));
	        if(useCreditDebit && !stop) {
	        	secondaryBuilder.getGridBuilder().useCreditDebit = secondaryBuilder.useCreditDebit;
	        	secondaryBuilder.getGridBuilder().debitValue = secondaryBuilder.debitValue;
	        	secondaryBuilder.getGridBuilder().creditValue = secondaryBuilder.creditValue;
	        	secondaryBuilder.getGridBuilder().cdValue = secondaryBuilder.cdValue;
	        	secondaryBuilder.getGridBuilder().cdAttribute = getSecondaryCdAttribute();
	        	secondaryBuilder.getGridBuilder().cdColumn = secondaryBuilder.getGridBuilder().cdAttribute != null ? secondaryBuilder.getGridBuilder().cdAttribute.getUniverseTableColumnName() : null;
	        	
	        }
	        setReconciliationDataFilter(filter, grid);
	        result = filter;
		}
		
		result.setRowType(GrilleRowType.NOT_RECONCILIATED);
		result.setFilter(reco.isLeftGridPrimary() ? reco.getRightFilter() : reco.getLeftFilter()); 
        result.getRecoData().setAllowPartialReco(reco.getUnionModel().isAllowPartialReco());  
		result.setPage(1);
		result.setPageSize(1);
        result.setShowAll(false);	
        return result;		
	}
	

	protected void setReconciliationDataFilter(GrilleDataFilter filter, ReconciliationUnionModelGrid grid) {
		GrilleColumn column = filter.getGrid().getColumnById(grid.getRecoTypeColumnId());
		filter.setRecoAttributeId(column != null ? column.getDimensionId() : null);	
		
		filter.setRecoData(new ReconciliationDataFilter());
		
		column = filter.getGrid().getColumnById(grid.getRecoTypeColumnId());
		filter.getRecoData().setRecoAttributeId(column != null ? column.getDimensionId() : null);	
		
		column = filter.getGrid().getColumnById(grid.getMeasureColumnId());
		filter.getRecoData().setAmountMeasureId(column != null ? column.getDimensionId() : null);	
        		
		column = filter.getGrid().getColumnById(grid.getPartialRecoTypeColumnId());
		filter.getRecoData().setPartialRecoAttributeId(column != null ? column.getDimensionId() : null);	
		
		column = filter.getGrid().getColumnById(grid.getReconciliatedMeasureColumnId());
		filter.getRecoData().setReconciliatedMeasureId(column != null ? column.getDimensionId() : null);	
		
		column = filter.getGrid().getColumnById(grid.getRemainningMeasureColumnId());
		filter.getRecoData().setRemainningMeasureId(column != null ? column.getDimensionId() : null);	
		
		column = filter.getGrid().getColumnById(grid.getNoteTypeColumnId());
		filter.getRecoData().setNoteAttributeId(column != null ? column.getDimensionId() : null);	
		
	}


	
	protected void buildWriteOffData(ReconciliationUnionData data) {
		if(this.reco.getUnionModel().isAllowWriteOff() && data.getWriteOffAmount() != null && data.getWriteOffAmount().compareTo(BigDecimal.ZERO) != 0) {
			if(this.reco.getUnionModel().getWriteOffModel() != null) {
				data.setWriteOffFields(new ArrayList<>(0));
				for(WriteOffUnionField field : this.reco.getUnionModel().getWriteOffModel().getFields()) {
					buildWriteOffField(data, field);
				}
			}			
		}
	}
		
	protected void buildWriteOffField(ReconciliationUnionData data, WriteOffUnionField field) {
		WriteOffUnionField item = field.copy();
		if(item.getDimensionId() != null) {
			if(item.getDimensionType().isAttribute() && item.getDefaultValueType() == WriteOffFieldValueType.LIST_OF_VALUES) {
				for(WriteOffUnionFieldValue value : field.getValues()) {
					//if(value.isDefaultValue()) {
						if(field.getDimensionType() != null && field.getDimensionType().isPeriod()) {
							item.setDateValue(value.getDateValue());
						} 
						else {
							item.setStringValue(value.getStringValue());
						}						
						data.getWriteOffFields().add(item);
					//}
				}
			}
			else {
				List<String> ids = new ArrayList<>(0);
				if(item.getDefaultValueType() == WriteOffFieldValueType.LEFT_SIDE) {					
					if(this.reco.getPrimaryUnionModelGrid() == this.reco.getUnionModel().getLeftGrid()) {
						if(primaryRowId != null) {
							ids.add(primaryRowId);
						}
					}
					else {
						if(secondaryRowIds != null) {
							ids.addAll(secondaryRowIds);
						}
					}
				}
				else if(item.getDefaultValueType() == WriteOffFieldValueType.RIGHT_SIDE) {
					if(this.reco.getPrimaryUnionModelGrid() == this.reco.getUnionModel().getLeftGrid()) {
						if(secondaryRowIds != null) {
							ids.addAll(secondaryRowIds);
						}
					}
					else {
						if(primaryRowId != null) {
							ids.add(primaryRowId);
						}
					}
				}
				if(!ids.isEmpty()) {
					ReconciliationUnionModelGrid grid = item.getDefaultValueType() == WriteOffFieldValueType.LEFT_SIDE ? this.reco.getUnionModel().getLeftGrid() : this.reco.getUnionModel().getRigthGrid();
					Object value = reconciliationUnionModelService.getFieldValue(grid, item.getSecondDimensionId(), item.getDimensionType(), ids);
					if(value != null) {
						if(item.getDimensionType().isAttribute()) {
							item.setStringValue((String)value);
						}
						else if(item.getDimensionType().isPeriod()) {
							item.getDateValue().setDateValue((Date)value);
						}
						else if(item.getDimensionType().isMeasure()) {
							item.setDecimalValue(null);
						}
						data.getWriteOffFields().add(item);
					}
				}
					
			}
		}		
	}
	
	
	
	protected void buildEnrichmentData(ReconciliationUnionData data) throws Exception {
		data.getEnrichmentItemDatas().clear();
		for(ReconciliationUnionModelEnrichment item : this.reco.getUnionModel().getEnrichmentListChangeHandler().getItems()) {
			buildEnrichmentItemData(data, item);
		}
	}

	private void buildEnrichmentItemData(ReconciliationUnionData data, ReconciliationUnionModelEnrichment item) throws Exception {		
		EnrichmentValue enrichmentValue = new EnrichmentValue();
    	enrichmentValue.setSide(item.getTargetSide().name());
    	enrichmentValue.setDimensionType(item.getDimensionType());
        enrichmentValue.setDimensionId(item.getTargetColumnId());
        
        if (item.getSourceSide() == ReconciliationModelSide.CUSTOM) {
        	enrichmentValue.setDecimalValue(item.getDecimalValue());
        	enrichmentValue.setStringValue(item.getStringValue());
        	enrichmentValue.setDateValue(new PeriodValue());            	
        	enrichmentValue.getDateValue().setDateOperator(item.getDateValue().getDateOperator());
        	enrichmentValue.getDateValue().setDateGranularity(item.getDateValue().getDateGranularity());
        	enrichmentValue.getDateValue().setDateNumber(item.getDateValue().getDateNumber());
        	enrichmentValue.getDateValue().setDateSign(item.getDateValue().getDateSign());
        	enrichmentValue.getDateValue().setDateValue(item.getDateValue().getDateValue());
            data.getEnrichmentItemDatas().add(enrichmentValue);
        }
        else if(item.getSourceSide() == ReconciliationModelSide.LEFT || item.getSourceSide() == ReconciliationModelSide.RIGHT) {
        	ReconciliationModelSide side = item.getSourceSide() == ReconciliationModelSide.LEFT ? ReconciliationModelSide.LEFT : ReconciliationModelSide.RIGHT;                        
            Object value = getGridItemValue(item, side);
            if(value != null) {
                if (item.isPeriod()) {
                	enrichmentValue.setDateValue(new PeriodValue());            	
                	enrichmentValue.getDateValue().setDateOperator(PeriodOperator.SPECIFIC);
                	enrichmentValue.getDateValue().setDateGranularity(PeriodGranularity.DAY);
                	enrichmentValue.getDateValue().setDateNumber(0);
                	enrichmentValue.getDateValue().setDateSign("+");
                	enrichmentValue.getDateValue().setDateValue((Date)value);
                }
                else if (item.isMeasure()) {
                    try {
                    	enrichmentValue.setDecimalValue(new BigDecimal(value.toString()));
                    }
                    catch (Exception e) {

                    }
                }
                else if (item.isAttribute()) {
                	enrichmentValue.setStringValue(value.toString());
                }
            } 
            data.getEnrichmentItemDatas().add(enrichmentValue);
        }
        else {
            throw new BcephalException("Enrichment item at position " + item.getPosition() + " is wrong!");
        }
    }  
	
	
	private Object getGridItemValue(ReconciliationUnionModelEnrichment item, ReconciliationModelSide side) {
		List<String> oids = new ArrayList<>(0);
		if(side == ReconciliationModelSide.LEFT) {					
			if(this.reco.getPrimaryUnionModelGrid() == this.reco.getUnionModel().getLeftGrid()) {
				if(primaryRowId != null) {
					oids.add(primaryRowId);
				}
			}
			else {
				if(secondaryRowIds != null) {
					oids.addAll(secondaryRowIds);
				}
			}
		}
		else if(side == ReconciliationModelSide.RIGHT) {
			if(this.reco.getPrimaryUnionModelGrid() == this.reco.getUnionModel().getLeftGrid()) {
				if(secondaryRowIds != null) {
					oids.addAll(secondaryRowIds);
				}				
			}
			else {
				if(primaryRowId != null) {
					oids.add(primaryRowId);
				}
			}
		}
		if(oids.size() > 0) {
			ReconciliationUnionModelGrid grid = item.getSourceSide().isLeft() ? this.reco.getUnionModel().getLeftGrid() : this.reco.getUnionModel().getRigthGrid();
			return reconciliationUnionModelService.getFieldValue(grid, item.getSourceColumnId(), item.getDimensionType(), oids);			
		}
		return null;
	}
	
	protected Measure getPrimaryMeasure() {
		return getMeasure(this.reco.getPrimaryUnionModelGrid(), this.reco.getPrimaryMeasureId());    
	}
	
	protected Measure getPrimaryReconciliatedMeasure() {
		return getMeasure(this.reco.getPrimaryUnionModelGrid(), this.reco.getPrimaryReconciliatedMeasureId());   
	}
	
	protected Measure getPrimaryRemainningMeasure() {
		return getMeasure(this.reco.getPrimaryUnionModelGrid(), this.reco.getPrimaryRemainningMeasureId());   
	}
	
	protected Measure getSecondaryMeasure() {
		return getMeasure(this.reco.getSecondaryUnionModelGrid(), this.reco.getSecondaryMeasureId());   
	}
	
	protected Measure getSecondaryReconciliatedMeasure() {
		return getMeasure(this.reco.getSecondaryUnionModelGrid(), this.reco.getSecondaryReconciliatedMeasureId());   
	}
	
	protected Measure getSecondaryRemainningMeasure() {
		return getMeasure(this.reco.getSecondaryUnionModelGrid(), this.reco.getSecondaryRemainningMeasureId());   
	}
		
	
	protected Attribute getPrimaryCdAttribute() {
		return getAttribute(this.reco.getPrimaryUnionModelGrid(), this.reco.getPrimaryCdAttributeId());    
	}
	
	protected Attribute getSecondaryCdAttribute() {
		return getAttribute(this.reco.getSecondaryUnionModelGrid(), this.reco.getSecondaryCdAttributeId());   
	}
	
	private Attribute getAttribute(ReconciliationUnionModelGrid grid, Long id) {
		if(grid.isUnion()) {
			UnionGridColumn column = grid.getUnionGridColumn(id, DimensionType.ATTRIBUTE);
			return new Attribute(column.getId(), null, DataSourceType.JOIN, grid.getGrid().getId());    
		}
		else {
			GrilleColumn column = grid.getGrille().getColumnById(id);
			return new Attribute(column.getId(), null, DataSourceType.UNIVERSE, null);
		}
	}
	
	private Measure getMeasure(ReconciliationUnionModelGrid grid, Long id) {
		if(grid.isUnion()) {
			UnionGridColumn column = grid.getUnionGridColumn(id, DimensionType.MEASURE);  
			return new Measure(column.getId(), null, DataSourceType.UNION_GRID, grid.getGrid().getId());   
		}
		else {
			GrilleColumn column = grid.getGrille().getColumnById(id);
			return new Measure(column.getId(), null, DataSourceType.UNIVERSE, null);
		}
		
		 
	}
	
}

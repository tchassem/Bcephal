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

import com.moriset.bcephal.domain.ReconciliationParameterCodes;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.EnrichmentValue;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.PeriodGranularity;
import com.moriset.bcephal.domain.filters.PeriodOperator;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.domain.filters.ReconciliationDataFilter;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleRowType;
import com.moriset.bcephal.grid.service.DbTableManager;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.reconciliation.domain.AutoReco;
import com.moriset.bcephal.reconciliation.domain.AutoRecoLog;
import com.moriset.bcephal.reconciliation.domain.AutoRecoRankingItem;
import com.moriset.bcephal.reconciliation.domain.ReconciliationCondition;
import com.moriset.bcephal.reconciliation.domain.ReconciliationData;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModelEnrichment;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModelSide;
import com.moriset.bcephal.reconciliation.domain.WriteOffField;
import com.moriset.bcephal.reconciliation.domain.WriteOffFieldValue;
import com.moriset.bcephal.reconciliation.domain.WriteOffFieldValueType;
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
public class AutoRecoMethodRunner {

	AutoReco reco;
	
	AutoRecoLog autoRecoLog;
	
	TaskProgressListener listener;
	
	boolean stop;
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	ParameterRepository parameterRepository;
		
	@Autowired
	ReconciliationModelService reconciliationModelService;
	
	GrilleDataFilter primaryFilter;
	AutoRecoGridQueryBuilder primaryBuilder;
	String primaryView;
	String primaryColumns = "";
	
	GrilleDataFilter secondaryFilter;
	AutoRecoGridQueryBuilder secondaryBuilder;	
	String SecondaryView;
    String secondaryColumns = "";
    
    protected Long primaryRowId;
	
	protected List<Long> secondaryRowIds;
	
    
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
	        
	        boolean isLeftPlusRight = this.reco.getModel().getBalanceFormula().isLeftPlusRight();
	        boolean useCreditDebit = this.reco.getModel().isUseDebitCredit();			
	        boolean leftGridIsprimary = getReco().isLeftGridPrimary();
	        
	        while(pageCount > 0 && count > 0 && !stop) {
	        	if(stop) { return;}
	        	for(Object[] row : rows) {       
	        		if(stop) { return;}
	        		ReconciliationData data = buildReconciliationData();	        
	    	        //service.loadSettings(data);
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
		                	long soid = Long.parseLong(r[0].toString());
		                	if(soid != primaryRowId) {	  
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
			                		boolean isOk = BigDecimal.ZERO.compareTo(data.getBalanceAmount()) == 0 || this.reco.getModel().isAllowWriteOff();			                		
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
    
    int batchSetpNbr = 5;
	int setpCount = 0;
    
    
    
    
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
					item.setValue(row[i + 2]);
					map.put(item.getSecondaryDbColumn(), row[i + 2]);					
				}
				if (column.getType().isPeriod()) {
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
	}
    
	protected List<BigDecimal> buildSecondaryAmounts(BigDecimal primaryAmount, AutoRecoGridQueryBuilder builder,
			boolean isLeftPlusRight, boolean useCreditDebit) {
		
		boolean acceptWriteOff = this.reco.getModel().isAllowWriteOff();
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
	
	protected boolean validateAmount(BigDecimal primaryAmount, BigDecimal secondaryAmount, List<BigDecimal> amounts, ReconciliationData data) {
		boolean balanceIsZero = this.reco.isBalanceIsZero();
        boolean balanceIsAmountInterval = this.reco.isBalanceIsAmountInterval();
        boolean balanceIsAmountPercentage = this.reco.isBalanceIsAmountPercentage();
        boolean isLeftPlusRight = this.reco.getModel().getBalanceFormula().isLeftPlusRight();
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
	
	protected void reconciliate(ReconciliationData data, long oid, long soid, BigDecimal amount, String primaryView, String SecondaryView) throws Exception {
		log.trace("{} - {} => {}", oid, soid, amount);
		if(getReco().isLeftGridPrimary()) {
			data.setRightids(new ArrayList<Long>());
			data.getRightids().add(soid);	      
		}
		else {
			data.setLeftids(new ArrayList<Long>());
			data.getLeftids().add(soid);	      
		}
		data.setPrimaryids(new ArrayList<Long>());
		data.getPrimaryids().add(oid);
		data.setSecondaryids(new ArrayList<Long>());
		data.getSecondaryids().add(soid);
		if(stop) { return;}
		reconciliate(data, RunModes.A, true);
	}
	
	protected void reconciliateAndCommit(RunModes mode) throws Exception {
		reconciliationModelService.reconciliateAndCommit(datas, "B-CEPHAL", mode);
	}
	
	int transactionCount = 0;
	List<ReconciliationData> datas = new ArrayList<>();
	protected void reconciliate(ReconciliationData data, RunModes mode, boolean refreshView) throws Exception {
		data.setLeftMeasureId(reco.getModel().getLeftMeasureId());
		data.setRigthMeasureId(reco.getModel().getRigthMeasureId());
		
		data.setAllowPartialReco(reco.getModel().isAllowPartialReco());
		data.setPartialRecoAttributeId(reco.getModel().getPartialRecoAttributeId());
		data.setReconciliatedMeasureId(reco.getModel().getReconciliatedMeasureId());
		data.setRemainningMeasureId(reco.getModel().getRemainningMeasureId());
		boolean perf = data.isAllowPartialReco() && data.getPartialRecoAttributeId() != null 
				&& data.getReconciliatedMeasureId() != null && data.getRemainningMeasureId() != null;
		boolean performWo = this.reco.getModel().isAllowWriteOff() && data.getWriteOffAmount() != null 
				&& data.getWriteOffAmount().compareTo(BigDecimal.ZERO) != 0;
		
		data.setPerformPartialReco(performWo ? !performWo : perf); 
		//data.setPartialRecoItems(new ArrayList<>());
				
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
		for (AutoRecoRankingItem item : this.reco.getSortedRankingItems()) {
			if (item.getSide() == side) {
				String part = item.getSql();
				if(StringUtils.hasText(part)) {
					sql += coma + part;
					coma = ", ";	
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
	
	protected ReconciliationData buildReconciliationData() {
		ReconciliationData data = new ReconciliationData();
        data.setReconciliationId(reco.getModel().getId());
        data.setRecoTypeId(reco.getModel().getRecoAttributeId());
        data.setAddAutomaticManual(reco.getModel().isAddAutomaticManual());
        data.setAddRecoDate(reco.getModel().isAddRecoDate());
        data.setAddUser(reco.getModel().isAddUser());
        data.setRecoDateId(reco.getModel().getRecoPeriodId());
        data.setPrimaryView(primaryView);
        data.setSecondaryView(SecondaryView);
        data.setUseDebitCredit(reco.getModel().isUseDebitCredit());
        data.setAllowReconciliatedAmountLog(reco.getModel().getAllowReconciliatedAmountLog());
        data.setReconciliatedAmountLogGridId(reco.getModel().getReconciliatedAmountLogGridId());
        reconciliationModelService.loadSettings(data);
        return data;
	}
		
	protected int buildViews() throws Exception {
				
		this.primaryFilter = buidPrimaryFilter(); 
        //service.loadClosures(primaryFilter);
        this.primaryBuilder = new AutoRecoGridQueryBuilder(this.reco, false, entityManager); 
        this.primaryBuilder.setParameterRepository(parameterRepository);
        primaryBuilder.setMeasure(new Measure(this.reco.getPrimaryMeasureId()));
        boolean useCreditDebit = this.reco.getModel().isUseDebitCredit();
        if(useCreditDebit && !stop) {
        	buildCDValues(primaryBuilder);
        }
        this.reco.setConditions(this.reco.getSortedConditions());
                
        this.secondaryFilter = buidSecondaryFilter();  
        //service.loadClosures(secondaryFilter);
        this.secondaryBuilder = new AutoRecoGridQueryBuilder(this.reco, false, entityManager);
        this.secondaryBuilder.setParameterRepository(parameterRepository);
        secondaryBuilder.setMeasure(new Measure(this.reco.getSecondaryMeasureId()));
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
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void createViewsFirst() {
		String dropSql = "DROP MATERIALIZED VIEW IF EXISTS " + primaryView;	
		Query query = entityManager.createNativeQuery(dropSql);
		query.executeUpdate();
				
		primaryBuilder.setFilter(primaryFilter);
		String primarySql = primaryBuilder.buildQuery();
		log.trace("Primary view creation sql : {}", primarySql);
		String createSql = "CREATE MATERIALIZED VIEW ".concat(primaryView).concat(" AS ").concat(primarySql);
		query = entityManager.createNativeQuery(createSql);
		query.executeUpdate();
		
		
		
		dropSql = "DROP MATERIALIZED VIEW IF EXISTS " + SecondaryView;		
		query = entityManager.createNativeQuery(dropSql);
		query.executeUpdate();
			
		secondaryBuilder.setFilter(secondaryFilter);
		String secondarySql = secondaryBuilder.buildQuery();
		log.trace("Secondary view creation sql : {}", secondarySql);
		createSql = "CREATE MATERIALIZED VIEW ".concat(SecondaryView).concat(" AS ").concat(secondarySql);
		query = entityManager.createNativeQuery(createSql);
		query.executeUpdate();
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
			index = "RECO_S_VIEW_INDEX" + this.reco.getId();
			indexSql = "CREATE INDEX ".concat(index).concat(" ON ").concat(SecondaryView).concat(" (").concat(secondaryColumns).concat(")");
			query = entityManager.createNativeQuery(indexSql);
			query.executeUpdate();
			
			if(this.reco.isPartialReco()) {
				secondaryBuilder = new AutoRecoGridViewQueryBuilder(this.reco, true, entityManager, SecondaryView, this.reco.getSecondarySide(), true);
			}
			else {
				secondaryBuilder = new AutoRecoGridViewQueryBuilder(this.reco, true, entityManager, SecondaryView);
				secondaryBuilder.setMeasure(new Measure(this.reco.getSecondaryMeasureId()));
			}
	        this.secondaryBuilder.setParameterRepository(parameterRepository);
			if(useCreditDebit) {
	        	secondaryBuilder.useCreditDebit = primaryBuilder.useCreditDebit;
	        	secondaryBuilder.debitValue = primaryBuilder.debitValue;
	        	secondaryBuilder.creditValue = primaryBuilder.creditValue;
	        	secondaryBuilder.cdValue = primaryBuilder.cdValue;
	        	secondaryBuilder.cdColumn = primaryBuilder.cdColumn;
	        	secondaryBuilder.cdAttribute = primaryBuilder.cdAttribute;
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
	
	protected void buildCDValues(AutoRecoGridQueryBuilder builder) {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_CREDIT_VALUE, ParameterType.ATTRIBUTE_VALUE);
		builder.creditValue = parameter != null && StringUtils.hasText(parameter.getStringValue()) ? parameter.getStringValue() : "C";
        parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_DEBIT_VALUE, ParameterType.ATTRIBUTE_VALUE);
        builder.debitValue = parameter != null && StringUtils.hasText(parameter.getStringValue()) ? parameter.getStringValue() : "D";
        parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_DC_ATTRIBUTE, ParameterType.ATTRIBUTE);
        builder.cdAttribute = parameter != null && parameter.getLongValue() != null ? new Attribute(parameter.getLongValue()) : null;        
        builder.cdColumn = builder.cdAttribute != null ? builder.cdAttribute.getUniverseTableColumnName() : null;        
        builder.useCreditDebit = true;
	}
				
	protected GrilleDataFilter buidPrimaryFilter() {
		GrilleDataFilter filter = new GrilleDataFilter();	
		filter.setRowType(GrilleRowType.NOT_RECONCILIATED);
        filter.setRecoAttributeId(reco.getModel().getRecoAttributeId());
        filter.setGrid(reco.getPrimaryGrid());
        filter.setFilter(reco.isLeftGridPrimary() ? reco.getLeftFilter() : reco.getRightFilter());
        filter.setCredit(filter.getGrid().isCredit());
        filter.setDebit(filter.getGrid().isDebit()); 
        filter.setRecoData(new ReconciliationDataFilter());
        filter.getRecoData().setRecoAttributeId(reco.getModel().getRecoAttributeId());
        filter.getRecoData().setAllowPartialReco(reco.getModel().isAllowPartialReco());
        filter.getRecoData().setPartialRecoAttributeId(reco.getModel().getPartialRecoAttributeId());
        filter.getRecoData().setReconciliatedMeasureId(reco.getModel().getReconciliatedMeasureId());
        filter.getRecoData().setRemainningMeasureId(reco.getModel().getRemainningMeasureId());
        filter.getRecoData().setNoteAttributeId(reco.getModel().getNoteAttributeId());
        filter.setPage(1);
        filter.setPageSize(1);
        filter.setShowAll(false);		
        return filter;
	}
	
	protected GrilleDataFilter buidSecondaryFilter() {
		GrilleDataFilter filter = new GrilleDataFilter();	
		filter.setRowType(GrilleRowType.NOT_RECONCILIATED);
        filter.setRecoAttributeId(reco.getModel().getRecoAttributeId());
        filter.setGrid(reco.getSecondaryGrid());
        filter.setFilter(reco.isLeftGridPrimary() ? reco.getRightFilter() : reco.getLeftFilter());
        filter.setCredit(filter.getGrid().isCredit());
        filter.setDebit(filter.getGrid().isDebit()); 
        filter.setRecoData(new ReconciliationDataFilter());
        filter.getRecoData().setRecoAttributeId(reco.getModel().getRecoAttributeId());
        filter.getRecoData().setAllowPartialReco(reco.getModel().isAllowPartialReco());
        filter.getRecoData().setPartialRecoAttributeId(reco.getModel().getPartialRecoAttributeId());
        filter.getRecoData().setReconciliatedMeasureId(reco.getModel().getReconciliatedMeasureId());
        filter.getRecoData().setRemainningMeasureId(reco.getModel().getRemainningMeasureId());
        filter.getRecoData().setNoteAttributeId(reco.getModel().getNoteAttributeId()); 
        filter.setPage(1);
        filter.setPageSize(10000);
        filter.setShowAll(false);		
        return filter;
	}
	
	
	protected void buildWriteOffData(ReconciliationData data) {
		if(this.reco.getModel().isAllowWriteOff() && data.getWriteOffAmount() != null && data.getWriteOffAmount().compareTo(BigDecimal.ZERO) != 0) {
			buildWriteOffMeasure(data);	
			if(this.reco.getModel().getWriteOffModel() != null) {
				data.setWriteOffFields(new ArrayList<>(0));
				for(WriteOffField field : this.reco.getModel().getWriteOffModel().getFields()) {
					buildWriteOffField(data, field);
				}
			}			
		}
	}
	
	protected void buildWriteOffMeasure(ReconciliationData data) {
		if(this.reco.getModel().getWriteOffModel() != null){
			if(this.reco.getModel().getWriteOffModel().getWriteOffMeasureId() != null) {
				data.setWriteOffMeasureId(this.reco.getModel().getWriteOffModel().getWriteOffMeasureId());
			}
			else if(this.reco.getModel().getWriteOffModel().getWriteOffMeasureSide() == ReconciliationModelSide.LEFT) {
				data.setWriteOffMeasureId(this.reco.getModel().getLeftMeasureId());
			}
			else if(this.reco.getModel().getWriteOffModel().getWriteOffMeasureSide() == ReconciliationModelSide.RIGHT) {
				data.setWriteOffMeasureId(this.reco.getModel().getRigthMeasureId());
			}	
		}		
	}
	
	protected void buildWriteOffField(ReconciliationData data, WriteOffField field) {
		WriteOffField item = field.copy();
		if(item.getDimensionId() != null && item.getDimensionType().isAttribute()) {
			if(item.getDefaultValueType() == WriteOffFieldValueType.LIST_OF_VALUES) {
				for(WriteOffFieldValue value : field.getValues()) {
					if(value.isDefaultValue()) {
						item.setStringValue(value.getStringValue());
						data.getWriteOffFields().add(item);
					}
				}
			}
			else {
				List<Long> ids = new ArrayList<>(0);
				if(item.getDefaultValueType() == WriteOffFieldValueType.LEFT_SIDE) {					
					if(this.reco.getPrimaryGrid() == this.reco.getModel().getLeftGrid()) {
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
					if(this.reco.getPrimaryGrid() == this.reco.getModel().getLeftGrid()) {
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
					String value = reconciliationModelService.getWriteOffFieldAttributeValue(new Attribute(item.getDimensionId()), ids);
					item.setStringValue(value);
					data.getWriteOffFields().add(item);
				}
			}
		}
		else if(item.getDimensionId() != null && item.getDimensionType().isPeriod()) {
			List<Long> ids = new ArrayList<>(0);
			if(item.getDefaultValueType() == WriteOffFieldValueType.LEFT_SIDE) {					
				if(this.reco.getPrimaryGrid() == this.reco.getModel().getLeftGrid()) {
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
				if(this.reco.getPrimaryGrid() == this.reco.getModel().getLeftGrid()) {
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
			if(ids.size() > 0) {
				Date value = reconciliationModelService.getWriteOffFieldPeriodValue(new Period(item.getDimensionId()), ids);
				item.getDateValue().setDateValue(value);
				data.getWriteOffFields().add(item);
			}
		}
	}
	
	
	
	protected void buildEnrichmentData(ReconciliationData data) throws Exception {
		data.getEnrichmentItemDatas().clear();
		for(ReconciliationModelEnrichment item : this.reco.getModel().getEnrichmentListChangeHandler().getItems()) {
			buildEnrichmentItemData(data, item);
		}
	}

	private void buildEnrichmentItemData(ReconciliationData data, ReconciliationModelEnrichment item) throws Exception {	
		try {
			boolean isLog = item.getTargetSide().isLogGrid();
			GrilleColumn targetColumn = getColumn(item.getTargetSide().isLeft() ? this.reco.getModel().getLeftGrid() : this.reco.getModel().getRigthGrid(), item.getTargetColumnId());
	        GrilleColumn sourceColumn = getColumn(item.getSourceSide().isLeft() ? this.reco.getModel().getLeftGrid() : this.reco.getModel().getRigthGrid(), item.getSourceColumnId());
			
	        if(isLog) {
	        	targetColumn = new GrilleColumn(null, null, item.getDimensionType(), item.getTargetColumnId(), null);
	        }
	        
	        if (targetColumn != null) {
	        	EnrichmentValue enrichmentValue = new EnrichmentValue();
	        	enrichmentValue.setSide(item.getTargetSide().name());
	        	enrichmentValue.setDimensionType(targetColumn.getType());
	            enrichmentValue.setDimensionId(targetColumn.getDimensionId());
	            
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
	            else if(sourceColumn != null) {
	            	ReconciliationModelSide side = item.getSourceSide() == ReconciliationModelSide.LEFT ? ReconciliationModelSide.LEFT : ReconciliationModelSide.RIGHT;                        
	                Object value = getGridItemValue(sourceColumn, side);
	                if(value != null) {
	                    if (targetColumn.isPeriod()) {
	                    	enrichmentValue.setDateValue(new PeriodValue());            	
	                    	enrichmentValue.getDateValue().setDateOperator(PeriodOperator.SPECIFIC);
	                    	enrichmentValue.getDateValue().setDateGranularity(PeriodGranularity.DAY);
	                    	enrichmentValue.getDateValue().setDateNumber(0);
	                    	enrichmentValue.getDateValue().setDateSign("+");
	                    	enrichmentValue.getDateValue().setDateValue((Date)value);
	                    }
	                    else if (targetColumn.isMeasure()) {
	                        try {
	                        	enrichmentValue.setDecimalValue(new BigDecimal(value.toString()));
	                        }
	                        catch (Exception e) {
	
	                        }
	                    }
	                    else if (targetColumn.isAttribute()) {
	                    	enrichmentValue.setStringValue(value.toString());
	                    }
	                } 
	                data.getEnrichmentItemDatas().add(enrichmentValue);
	            }
	            else {
	                throw new BcephalException("Enrichment item at position " + (item.getPosition() + 1) + " is wrong!");
	            }
	        }
	        else {
	        	throw new BcephalException("Enrichment item at position " + (item.getPosition() + 1) + " is wrong!");
	        }    
		}catch (Exception e) {
			if(e instanceof BcephalException) {
				throw e;
			}
			log.error("", e);
			throw new BcephalException("Enrichment item at position " + (item.getPosition() + 1) + " is wrong!");
		}
	}
	
	
	private GrilleColumn getColumn(Grille grid, Long columnId) {
        if (columnId != null) {
            for (GrilleColumn column : grid.getColumnListChangeHandler().getItems())
            {
                if (column.getId().equals(columnId)) return column;
            }
        }
        return null;
    }

	private Object getGridItemValue(GrilleColumn sourceColumn, ReconciliationModelSide side) {
		List<Long> oids = new ArrayList<>(0);
		if(side == ReconciliationModelSide.LEFT) {					
			if(this.reco.getPrimaryGrid() == this.reco.getModel().getLeftGrid()) {
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
			if(this.reco.getPrimaryGrid() == this.reco.getModel().getLeftGrid()) {
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
			if (sourceColumn.isPeriod()) {
				return reconciliationModelService.getWriteOffFieldPeriodValue(new Period(sourceColumn.getDimensionId(), sourceColumn.getDimensionName()), oids);
            }
            else if (sourceColumn.isMeasure()) {
            	return reconciliationModelService.getWriteOffFieldMeasureValue(new Measure(sourceColumn.getDimensionId(), sourceColumn.getDimensionName()), oids);
            }
            else if (sourceColumn.isAttribute()) {
            	return reconciliationModelService.getWriteOffFieldAttributeValue(new Attribute(sourceColumn.getDimensionId(), sourceColumn.getDimensionName()), oids);
            }
		}
		return null;
	}
	
}

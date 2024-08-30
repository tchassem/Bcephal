/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.filters.MeasureFilter;
import com.moriset.bcephal.domain.filters.MeasureFilterItem;
import com.moriset.bcephal.domain.filters.MeasureOperator;
import com.moriset.bcephal.domain.filters.ReconciliationDataFilter;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleRowType;
import com.moriset.bcephal.reconciliation.domain.ReconciliationData;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModelSide;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Slf4j
@Service
@Scope(scopeName= ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AutoRecoZeroAmountRunner extends AutoRecoMethodRunner {
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void run() throws Exception {
		log.debug("Auto Reco : {} - Running....", this.reco.getName());
		try {
			if(listener != null) {
	        	listener.startSubInfo(4);
	        }
			if(stop) { return;}
			
			boolean useCreditDebit = this.reco.getModel().isUseDebitCredit();
			this.primaryBuilder = new ZeroAmountQueryBuilder(this.reco, false, entityManager); 
			this.primaryBuilder.setParameterRepository(parameterRepository);
			if(useCreditDebit && !stop) {
	        	buildCDValues(primaryBuilder);
	        }
			primaryBuilder.setFilter(buidPrimaryFilter());			
			List<Long> prows = getRowIds(primaryBuilder);			
			int pCount = prows.size();
			log.trace("Auto Reco : {} - Primary grid rows readed : {}", this.reco.getName(), pCount);
			if(listener != null) {
	        	listener.nextSubInfoStep(1);
	        }
						
			this.secondaryBuilder = new ZeroAmountQueryBuilder(this.reco, false, entityManager); 
			this.secondaryBuilder.setParameterRepository(parameterRepository);
			if(useCreditDebit && !stop) {
	        	buildCDValues(secondaryBuilder);
	        }
			secondaryBuilder.setFilter(buidSecondaryFilter());			
			List<Long> srows = getRowIds(secondaryBuilder);
			int sCount = srows.size();
			log.trace("Auto Reco : {} - Secondary grid rows readed : {}", this.reco.getName(), sCount);
			if(listener != null) {
	        	listener.nextSubInfoStep(1);
	        }
			
			boolean leftGridIsprimary = getReco().isLeftGridPrimary();			
			ReconciliationData data = buildReconciliationData();
			addIds(data, prows, leftGridIsprimary ? ReconciliationModelSide.LEFT : ReconciliationModelSide.RIGHT);
			addIds(data, srows, leftGridIsprimary ? ReconciliationModelSide.RIGHT : ReconciliationModelSide.LEFT);
	        if(stop) { return;}	  
	        reco.setBatchChunk(1);
	        reconciliate(data, RunModes.A, true);
	        if(listener != null) {
	        	listener.nextSubInfoStep(1);
	        }
	        runScript();
	        if(listener != null) {
	        	listener.nextSubInfoStep(1);
	        }
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
	
	
	
	
	private List<Long> getRowIds(AutoRecoGridQueryBuilder builder) {
		String sql = builder.buildQuery();
        Query query = entityManager.createNativeQuery(sql);
        @SuppressWarnings("unchecked")
		List<Long> ids = query.getResultList();
        return ids;
	}




	private void addIds(ReconciliationData data, List<?> rows, ReconciliationModelSide side) {
		for(Object row : rows) {
			if(row != null) {
				if(row instanceof Number) {
					if(side.isLeft()) {
						data.getLeftids().add(((Number)row).longValue());
					}
					else {
						data.getRightids().add(((Number)row).longValue());
					}
				}
				else if(row instanceof Object[]) {
					Object[] tab = (Object[])row;
					if(tab.length > 0 && tab[0] instanceof Number) {
						if(side.isLeft()) {
							data.getLeftids().add(((Number)tab[0]).longValue());
						}
						else {
							data.getRightids().add(((Number)tab[0]).longValue());
						}
					}
				}
			}
		}
	}

	@Override
	protected GrilleDataFilter buidPrimaryFilter() {
		GrilleDataFilter filter = new GrilleDataFilter();	
		filter.setRowType(GrilleRowType.NOT_RECONCILIATED);
        filter.setRecoAttributeId(reco.getModel().getRecoAttributeId());
        filter.setGrid(reco.getPrimaryGrid());
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
        filter.setShowAll(true);
        
        MeasureFilter measureFilter = filter.getGrid().getUserFilter().getMeasureFilter();
        if(measureFilter == null) {
        	measureFilter = new MeasureFilter();
        	filter.getGrid().getUserFilter().setMeasureFilter(measureFilter);
        }
        MeasureFilterItem item = new MeasureFilterItem();
        item.setDimensionType(DimensionType.MEASURE);
        item.setDimensionId(reco.getPrimaryMeasureId());
        item.setOperator(MeasureOperator.EQUALS);
        item.setValue(BigDecimal.ZERO);
        item.setFilterVerb(FilterVerb.AND);
        measureFilter.addItem(item);        
        return filter;		
	}
	
	@Override
	protected GrilleDataFilter buidSecondaryFilter() {
		GrilleDataFilter filter = new GrilleDataFilter();	
		filter.setRowType(GrilleRowType.NOT_RECONCILIATED);
        filter.setRecoAttributeId(reco.getModel().getRecoAttributeId());
        filter.setGrid(reco.getSecondaryGrid());
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
        filter.setShowAll(true);
        
        MeasureFilter measureFilter = filter.getGrid().getUserFilter().getMeasureFilter();
        if(measureFilter == null) {
        	measureFilter = new MeasureFilter();
        	filter.getGrid().getUserFilter().setMeasureFilter(measureFilter);
        }
        MeasureFilterItem item = new MeasureFilterItem();
        item.setDimensionType(DimensionType.MEASURE);
        item.setDimensionId(reco.getSecondaryMeasureId());
        item.setOperator(MeasureOperator.EQUALS);
        item.setValue(BigDecimal.ZERO);
        item.setFilterVerb(FilterVerb.AND);
        measureFilter.addItem(item);        
        return filter;		
	}
	
	@Override
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
        return data;        
	}

	
}

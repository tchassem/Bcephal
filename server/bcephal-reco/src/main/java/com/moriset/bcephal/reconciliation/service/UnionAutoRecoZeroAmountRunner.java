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
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.filters.MeasureFilter;
import com.moriset.bcephal.domain.filters.MeasureFilterItem;
import com.moriset.bcephal.domain.filters.MeasureOperator;
import com.moriset.bcephal.domain.filters.ReconciliationDataFilter;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleRowType;
import com.moriset.bcephal.grid.domain.JoinConditionItemType;
import com.moriset.bcephal.grid.domain.UnionGridCondition;
import com.moriset.bcephal.grid.domain.UnionGridConditionItem;
import com.moriset.bcephal.grid.service.UnionGridFilter;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModelSide;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionData;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionModelGrid;
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
public class UnionAutoRecoZeroAmountRunner extends UnionAutoRecoMethodRunner {
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void run() throws Exception {
		log.debug("Auto Reco : {} - Running....", this.reco.getName());
		try {
			if(listener != null) {
	        	listener.startSubInfo(4);
	        }
			if(stop) { return;}
			
			buidPrimaryFilter();			
			List<String> prows = getRowIds(primaryBuilder);			
			int pCount = prows.size();
			log.trace("Auto Reco : {} - Primary grid rows readed : {}", this.reco.getName(), pCount);
			if(listener != null) {
	        	listener.nextSubInfoStep(1);
	        }
					
			buidSecondaryFilter();					
			List<String> srows = getRowIds(secondaryBuilder);
			int sCount = srows.size();
			log.trace("Auto Reco : {} - Secondary grid rows readed : {}", this.reco.getName(), sCount);
			if(listener != null) {
	        	listener.nextSubInfoStep(1);
	        }
			
			boolean leftGridIsprimary = getReco().isLeftGridPrimary();			
			ReconciliationUnionData data = buildReconciliationData();
			addIds(data, prows, leftGridIsprimary ? ReconciliationModelSide.LEFT : ReconciliationModelSide.RIGHT);
			addIds(data, srows, leftGridIsprimary ? ReconciliationModelSide.RIGHT : ReconciliationModelSide.LEFT);
	        if(stop) { return;}	  
	        reco.setBatchChunk(1);
	        reconciliate(data, RunModes.A, true);
	        if(listener != null) {
	        	listener.nextSubInfoStep(1);
	        }
	        refreshPublication();	
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
	
	
	
	
	private List<String> getRowIds(AutoRecoUnionQueryBuilder builder) throws Exception {
		String sql = builder.buildQuery();
        Query query = entityManager.createNativeQuery(sql);
        if(builder.getParameters() != null) {
	        for(String key : builder.getParameters().keySet()) {
	        	query.setParameter(key, builder.getParameters().get(key));
	        }   
        }
        @SuppressWarnings("unchecked")
		List<String> ids = query.getResultList();
        return ids;
	}




	private void addIds(ReconciliationUnionData data, List<?> rows, ReconciliationModelSide side) {
		for(Object row : rows) {
			if(row != null) {
				if(row instanceof String) {
					if(side.isLeft()) {
						data.getLeftids().add((String)row);
					}
					else {
						data.getRightids().add((String)row);
					}
				}
				else if(row instanceof Object[]) {
					Object[] tab = (Object[])row;
					if(tab.length > 0 && tab[0] instanceof String) {
						if(side.isLeft()) {
							data.getLeftids().add((String)tab[0]);
						}
						else {
							data.getRightids().add((String)tab[0]);
						}
					}
				}
			}
		}
	}

	@Override
	protected GrilleDataFilter buidPrimaryFilter() {
        ReconciliationUnionModelGrid grid = reco.getPrimaryUnionModelGrid();		
		boolean useCreditDebit = this.reco.getUnionModel().isUseDebitCredit();
        this.primaryBuilder = new ZeroAmountUnionQueryBuilder(this.reco, false, entityManager);
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
	        
	        UnionGridCondition condition = new UnionGridCondition(); 
	        condition.setPosition(filter.getUnionGrid().getConditionListChangeHandler().getItems().size());
	        condition.setVerb(FilterVerb.AND.name());
	        condition.setOpeningBracket("(");
	        condition.setClosingBracket(")");
	        condition.setComparator("=");
	        condition.setItem1(new UnionGridConditionItem());
	        condition.getItem1().setType(JoinConditionItemType.COLUMN);
	        condition.getItem1().setDimensionType(DimensionType.MEASURE);
	        condition.getItem1().setColumnId(grid.getMeasureColumnId());
	        
	        condition.setItem2(new UnionGridConditionItem());
	        condition.getItem2().setType(JoinConditionItemType.PARAMETER);
	        condition.getItem2().setDimensionType(DimensionType.MEASURE);
	        condition.getItem2().setDecimalValue(BigDecimal.ZERO);	        
	        filter.getUnionGrid().getConditionListChangeHandler().addNew(condition);
	        
	        this.primaryBuilder.setFilter(filter);
	        unionGridService.loadFilterClosures(filter, true);
		}
		else {
			GrilleDataFilter filter = new GrilleDataFilter();
	        filter.setGrid(grid.getGrille());
	        filter.setCredit(filter.getGrid().isCredit());
	        filter.setDebit(filter.getGrid().isDebit());	        	        
	        this.primaryBuilder.setGridBuilder(new ZeroAmountForUnionQueryBuilder(this.reco, false, entityManager)); 
	        this.primaryBuilder.getGridBuilder().setFilter(filter);
	        this.primaryBuilder.getGridBuilder().setParameterRepository(parameterRepository);
	        GrilleColumn column = filter.getGrid().getColumnById(this.reco.getPrimaryMeasureId());	        
	        this.primaryBuilder.getGridBuilder().setMeasure(new Measure(column != null ? column.getDimensionId() : null));
	        if(useCreditDebit && !stop) {
	        	this.primaryBuilder.getGridBuilder().creditValue = this.primaryBuilder.creditValue;
	        	this.primaryBuilder.getGridBuilder().debitValue = this.primaryBuilder.debitValue;
	        	this.primaryBuilder.getGridBuilder().cdAttribute = getPrimaryCdAttribute();      
	        	this.primaryBuilder.getGridBuilder().cdColumn = this.primaryBuilder.getGridBuilder().cdAttribute != null ? this.primaryBuilder.getGridBuilder().cdAttribute.getUniverseTableColumnName() : null; 
	        }
	        setReconciliationDataFilter(filter, grid);
	        result = filter;
	        
	        
	        MeasureFilter measureFilter = filter.getGrid().getAdminFilter() != null ? filter.getGrid().getAdminFilter().getMeasureFilter() : null;
	        if(measureFilter == null) {
	        	measureFilter = new MeasureFilter();
	        	if(filter.getGrid().getAdminFilter() == null) {
	        		filter.getGrid().setAdminFilter(new UniverseFilter());
	        	}
	        	filter.getGrid().getAdminFilter().setMeasureFilter(measureFilter);
	        }
	        MeasureFilterItem item = new MeasureFilterItem();
	        item.setDataSourceType(filter.getGrid().getDataSourceType());
	        item.setDataSourceId(filter.getGrid().getDataSourceId());
	        item.setDimensionType(DimensionType.MEASURE);
	        item.setDimensionId(filter.getGrid().getColumnById(grid.getMeasureColumnId()).getDimensionId());
	        item.setOperator(MeasureOperator.EQUALS);
	        item.setValue(BigDecimal.ZERO);
	        item.setFilterVerb(FilterVerb.AND);
	        measureFilter.addItem(item);
		}
		
		result.setRowType(GrilleRowType.NOT_RECONCILIATED);
		result.setFilter(reco.isLeftGridPrimary() ? reco.getLeftFilter() : reco.getRightFilter()); 
		result.getRecoData().setAllowPartialReco(reco.getUnionModel().isAllowPartialReco());
		result.setPage(1);
		result.setPageSize(10000);
        result.setShowAll(true);	
        return result;
        
        
        
        
        
        
        
             
//        MeasureFilter measureFilter = filter.getUnionGrid().getAdminFilter() != null ? filter.getUnionGrid().getAdminFilter().getMeasureFilter() : null;
//        if(measureFilter == null) {
//        	measureFilter = new MeasureFilter();
//        	if(filter.getUnionGrid().getAdminFilter() == null) {
//        		filter.getUnionGrid().setAdminFilter(new UniverseFilter());
//        	}
//        	filter.getUnionGrid().getAdminFilter().setMeasureFilter(measureFilter);
//        }
//        MeasureFilterItem item = new MeasureFilterItem();
//        item.setDataSourceType(grid.getGrid().getMainGrid().getGridType().GetDataSource());
//        item.setDataSourceId(grid.getGrid().getMainGrid().getGridId());
//        item.setDimensionType(DimensionType.MEASURE);
//        item.setDimensionId(reco.getPrimaryMeasureId());
//        item.setOperator(MeasureOperator.EQUALS);
//        item.setValue(BigDecimal.ZERO);
//        item.setFilterVerb(FilterVerb.AND);
//        measureFilter.addItem(item); 
	}
	
	@Override
	protected GrilleDataFilter buidSecondaryFilter() {
        ReconciliationUnionModelGrid grid = reco.getSecondaryUnionModelGrid();
		boolean useCreditDebit = this.reco.getUnionModel().isUseDebitCredit();
		this.secondaryBuilder = new ZeroAmountUnionQueryBuilder(this.reco, false, entityManager);
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
	        
	        UnionGridCondition condition = new UnionGridCondition(); 
	        condition.setPosition(filter.getUnionGrid().getConditionListChangeHandler().getItems().size());
	        condition.setVerb(FilterVerb.AND.name());
	        condition.setOpeningBracket("(");
	        condition.setClosingBracket(")");
	        condition.setComparator("=");
	        condition.setItem1(new UnionGridConditionItem());
	        condition.getItem1().setType(JoinConditionItemType.COLUMN);
	        condition.getItem2().setDimensionType(DimensionType.MEASURE);
	        condition.getItem2().setColumnId(grid.getMeasureColumnId());
	        
	        condition.setItem2(new UnionGridConditionItem());
	        condition.getItem2().setType(JoinConditionItemType.PARAMETER);
	        condition.getItem2().setDimensionType(DimensionType.MEASURE);
	        condition.getItem2().setDecimalValue(BigDecimal.ZERO);	        
	        filter.getUnionGrid().getConditionListChangeHandler().addNew(condition);
	        
	        this.secondaryBuilder.setFilter(filter);
	        unionGridService.loadFilterClosures(filter, true);
		}
		else {
			GrilleDataFilter filter = new GrilleDataFilter();
	        filter.setGrid(grid.getGrille());
	        filter.setCredit(filter.getGrid().isCredit());
	        filter.setDebit(filter.getGrid().isDebit());
	        
	        this.secondaryBuilder.setGridBuilder(new ZeroAmountForUnionQueryBuilder(this.reco, false, entityManager)); 
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
	        
	        MeasureFilter measureFilter = filter.getGrid().getAdminFilter() != null ? filter.getGrid().getAdminFilter().getMeasureFilter() : null;
	        if(measureFilter == null) {
	        	measureFilter = new MeasureFilter();
	        	if(filter.getGrid().getAdminFilter() == null) {
	        		filter.getGrid().setAdminFilter(new UniverseFilter());
	        	}
	        	filter.getGrid().getAdminFilter().setMeasureFilter(measureFilter);
	        }
	        MeasureFilterItem item = new MeasureFilterItem();
	        item.setDataSourceType(filter.getGrid().getDataSourceType());
	        item.setDataSourceId(filter.getGrid().getDataSourceId());
	        item.setDimensionType(DimensionType.MEASURE);
	        item.setDimensionId(filter.getGrid().getColumnById(grid.getMeasureColumnId()).getDimensionId());
	        item.setOperator(MeasureOperator.EQUALS);
	        item.setValue(BigDecimal.ZERO);
	        item.setFilterVerb(FilterVerb.AND);
	        measureFilter.addItem(item);
		}
		
		result.setRowType(GrilleRowType.NOT_RECONCILIATED);
		result.setFilter(reco.isLeftGridPrimary() ? reco.getRightFilter() : reco.getLeftFilter()); 
        result.getRecoData().setAllowPartialReco(reco.getUnionModel().isAllowPartialReco());  
		result.setPage(1);
		result.setPageSize(10000);
        result.setShowAll(true);	
        return result;		
		
        
//        MeasureFilter measureFilter = filter.getUnionGrid().getAdminFilter() != null ? filter.getUnionGrid().getAdminFilter().getMeasureFilter() : null;
//        if(measureFilter == null) {
//        	measureFilter = new MeasureFilter();
//        	if(filter.getUnionGrid().getAdminFilter() == null) {
//        		filter.getUnionGrid().setAdminFilter(new UniverseFilter());
//        	}
//        	filter.getUnionGrid().getAdminFilter().setMeasureFilter(measureFilter);
//        }
//        MeasureFilterItem item = new MeasureFilterItem();
//        item.setDataSourceType(grid.getGrid().getMainGrid().getGridType().GetDataSource());
//        item.setDataSourceId(grid.getGrid().getMainGrid().getGridId());
//        item.setDimensionType(DimensionType.MEASURE);
//        item.setDimensionId(reco.getSecondaryMeasureId());
//        item.setOperator(MeasureOperator.EQUALS);
//        item.setValue(BigDecimal.ZERO);
//        item.setFilterVerb(FilterVerb.AND);
//        measureFilter.addItem(item); 
	}
	
	@Override
	protected ReconciliationUnionData buildReconciliationData() {
		ReconciliationUnionData data = new ReconciliationUnionData();
        data.setReconciliationId(reco.getUnionModel().getId());
        data.setAddAutomaticManual(reco.getUnionModel().isAddAutomaticManual());
        data.setAddRecoDate(reco.getUnionModel().isAddRecoDate());
        data.setAddUser(reco.getUnionModel().isAddUser());
        data.setUseDebitCredit(reco.getUnionModel().isUseDebitCredit());
        data.setAllowReconciliatedAmountLog(reco.getUnionModel().getAllowReconciliatedAmountLog());
        data.setReconciliatedAmountLogGridId(reco.getUnionModel().getReconciliatedAmountLogGridId());
        
        data.setLeftRecoTypeId(reco.getUnionModel().getLeftGrid().getRecoTypeColumnId());
		data.setRightRecoTypeId(reco.getUnionModel().getRigthGrid().getRecoTypeColumnId());
                        
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
		reconciliationUnionModelService.loadSettings(data);
        return data;
	}

	
}

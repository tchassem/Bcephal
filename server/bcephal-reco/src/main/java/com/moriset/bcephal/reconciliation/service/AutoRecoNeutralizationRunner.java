package com.moriset.bcephal.reconciliation.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.reconciliation.domain.ReconciliationData;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Scope(scopeName= ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AutoRecoNeutralizationRunner extends AutoRecoMethodRunner {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void run() throws Exception {
		log.debug("Auto Reco : {} - Running....", this.reco.getName());
		int count = 7;
		try{	
			if(listener != null) {
	        	listener.startSubInfo(count);
	        }
			log.trace("Auto Reco : {} - Build views....", this.reco.getName());
			if(listener != null) {
            	listener.nextSubInfoStep(1);
            }
			count = buildViews();
			log.trace("Auto Reco : {} - Views builded!", this.reco.getName());
			if(listener != null) {
            	listener.nextSubInfoStep(1);
            }
			log.debug("Auto Reco : {} - Primary grid row count = {}", this.reco.getName(), count);
			if(stop) { return;}
			
						
			ReconciliationData data = buildReconciliationData();	        
	        //service.loadSettings(data);
	        if(stop) { return;}	 
	        if(listener != null) {
            	listener.nextSubInfoStep(1);
            }
	        
	        log.debug("Auto Reco : {} - Read data from primary grid : {}", this.reco.getName(), primaryView);
	        data.setPrimaryids(getViewIds(primaryView));
	        if(listener != null) {
            	listener.nextSubInfoStep(1);
            }   
	        
	        log.debug("Auto Reco : {} - Read data from secondary grid : {}", this.reco.getName(), SecondaryView);
	        data.setSecondaryids(getViewIds(SecondaryView));
	        if(listener != null) {
            	listener.nextSubInfoStep(1);
            }   
	        
	        if(this.reco.isLeftGridPrimary()) {
	        	data.setLeftids(data.getPrimaryids());
	        	data.setRightids(data.getSecondaryids());
	        }
	        else {
	        	data.setLeftids(data.getSecondaryids());
	        	data.setRightids(data.getPrimaryids());
	        }
	        
	        neutralize(data, RunModes.A, true);
	        if(listener != null) {
            	listener.nextSubInfoStep(1);
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
	private void neutralize(ReconciliationData data, RunModes mode, boolean refreshView) throws Exception {			
		data.setAllowPartialReco(reco.getModel().isAllowPartialReco());
		data.setPartialRecoAttributeId(reco.getModel().getPartialRecoAttributeId());
		data.setReconciliatedMeasureId(reco.getModel().getReconciliatedMeasureId());
		data.setRemainningMeasureId(reco.getModel().getRemainningMeasureId());		
		data.setPartialRecoItems(new ArrayList<>());
		data.setLeftMeasureId(reco.getModel().getLeftMeasureId());
		data.setRigthMeasureId(reco.getModel().getRigthMeasureId());
		data.setAllowPartialReco(reco.getModel().isAllowPartialReco());
		data.setPartialRecoAttributeId(reco.getModel().getPartialRecoAttributeId());
		data.setReconciliatedMeasureId(reco.getModel().getReconciliatedMeasureId());
		data.setRemainningMeasureId(reco.getModel().getRemainningMeasureId());
		boolean perf = data.isAllowPartialReco() && data.getPartialRecoAttributeId() != null && data.getReconciliatedMeasureId() != null && data.getRemainningMeasureId() != null;
		data.setPerformPartialReco(perf); 
		reconciliationModelService.neutralizeWithoutCommint(data, "B-CEPHAL", mode);
	}

	@Override
	protected ReconciliationData buildReconciliationData() {
		ReconciliationData data = super.buildReconciliationData();
        data.setNeutralizationAttributeId(reco.getModel().getNeutralizationAttributeId());
        data.setNeutralizationSequenceId(reco.getModel().getNeutralizationSequenceId());
        data.setNeutralizationValue(reco.getNeutralizationValue());
        data.setAllowNeutralization(true);
        data.setPerformNeutralization(true);
        return data;
	}
	
	@SuppressWarnings("unchecked")
	private List<Long> getViewIds(String viewName) throws Exception {
		try {
			String sql = "SELECT id FROM " + viewName;
			Query query = this.entityManager.createNativeQuery(sql);
			return query.getResultList();
		}
		catch (Exception e) {
			log.debug("Project: {}  - Auto Reco : {} - Unable to read view IDs.", TenantContext.getCurrentTenant(), this.reco.getName(), e);
			throw new BcephalException("Unable to read data...");
		}
	}	
	
}

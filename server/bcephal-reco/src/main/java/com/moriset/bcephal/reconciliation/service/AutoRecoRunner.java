/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.reconciliation.domain.AutoReco;
import com.moriset.bcephal.reconciliation.domain.AutoRecoLog;
import com.moriset.bcephal.reconciliation.domain.AutoRecoMethod;
import com.moriset.bcephal.reconciliation.domain.WriteOffField;
import com.moriset.bcephal.reconciliation.repository.AutoRecoLogRepository;
import com.moriset.bcephal.reconciliation.repository.ReconciliationModelRepository;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Data
@Slf4j
public class AutoRecoRunner {
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	AutoRecoLogRepository autoRecoLogRepository;
	
	@Autowired
	ParameterRepository parameterRepository;
	
	@Autowired
	ReconciliationModelRepository reconciliationModelRepository;
	
	AutoReco reco;
	
	boolean automatic;
	
	AutoRecoMethodRunner runner;
	
	TaskProgressListener listener;
	
	
	/**
	 * Run automatic reconciliation
	 */
	public void run() {	
		log.debug("Try to run auto reco : {}", reco != null ? reco.getName() : null);
		AutoRecoLog autoRecoLog = null;
		try {
			if(this.reco.getRecoId() == null) {
				log.debug("Auto Reco : {} - Reconciliation model id is NULL!", this.reco.getName());
				throw new BcephalException("Unable to run Auto Reco : " + this.reco.getName() + ".\nReconciliation model is undefined!");
			}
			this.reco.setModel(reconciliationModelRepository.getReferenceById(this.reco.getRecoId()));
			if(this.reco.getModel() == null) {
				log.debug("Auto Reco : {} - Unknown reconciliation model!", this.reco.getName());
				throw new BcephalException("Unable to run Auto Reco : " + this.reco.getName() + ".\nUnknown reconciliation model : " + this.reco.getRecoId());
			}
			if(this.reco.getModel().getLeftMeasureId() == null) {
				log.debug("Auto Reco : {} - Left measure is NULL!", this.reco.getName());
				throw new BcephalException("Unable to run Auto Reco : " + this.reco.getName() + ".\nLeft measure is undefined!");
			}
			if(this.reco.getModel().getRigthMeasureId() == null) {
				log.debug("Auto Reco : {} - Right measure is NULL!", this.reco.getName());
				throw new BcephalException("Unable to run Auto Reco : " + this.reco.getName() + ".\nRight measure is undefined!");
			}
			if(this.reco.getModel().getRecoAttributeId() == null) {
				log.debug("Auto Reco : {} - Reco attribute id is NULL!", this.reco.getName());
				throw new BcephalException("Unable to run Auto Reco : " + this.reco.getName() + ".\nReco attribute is undefined!");
			}
			loadClosures(this.reco);
			autoRecoLog = initLog();
			boolean isPartial = this.reco.isPartialReco();
			if(this.reco.getMethod() == null || this.reco.getMethod() == AutoRecoMethod.ZERO_AMOUNT) {
				this.runner = new AutoRecoZeroAmountRunner();			
			}
			else if(this.reco.getMethod() == null || this.reco.getMethod() == AutoRecoMethod.ONE_ON_ONE) {
				this.runner = isPartial ? new PartialAutoRecoOneOnOneRunner() : new AutoRecoOneOnOneRunner();			
			}
			else if(this.reco.getMethod() == AutoRecoMethod.BOTH_CUMULATED) {
				this.runner = new AutoRecoBothCumulatedRunner();			
			}
			else if(this.reco.getMethod() == AutoRecoMethod.NEUTRALIZATION) {
				this.runner = new AutoRecoNeutralizationRunner();			
			}
			else {
				this.runner = isPartial ? new PartialAutoRecoCumulatedRunner() : new AutoRecoCumulatedRunner();
			}
			log.trace("Auto Reco : {}  -  Method : {}", this.reco.getName(), this.reco.getMethod());
			this.runner.setReco(reco);
			this.runner.setParameterRepository(parameterRepository);
			this.runner.setAutoRecoLog(autoRecoLog);
			this.runner.setListener(listener);
			this.runner.run();	
			endLog(autoRecoLog, null);
			if(getListener() != null) {
				getListener().end();
			}
		}
		catch (Exception e) {
			log.error("Auto Reco : {} : unexpected error", reco.getName(), e);			
			String message = "Unable to run auto reco. \nUnexpected error!";
			if(e instanceof BcephalException) {
				message = e.getMessage();
			}
			endLog(autoRecoLog, message);
			if(getListener() != null) {
				getListener().error(message, true);
			}
		}		
	}
	
	private void loadClosures(AutoReco autoReco) {
		autoReco.setConditions(autoReco.getSortedConditions());
		autoReco.getModel().setEnrichments(autoReco.getModel().getSortedEnrichments());
		if(autoReco.getModel().getWriteOffModel() != null) {
			autoReco.getModel().getWriteOffModel().setFields(autoReco.getModel().getWriteOffModel().getSortedFields());
			for(WriteOffField field : autoReco.getModel().getWriteOffModel().getFields()) {
				field.setValues(field.getSortedValues());
			}
		}
	}

	protected AutoRecoLog initLog() {
		log.trace("Auto Reco : {} : Init Log...!", reco.getName());
		AutoRecoLog autoRecoLog = new AutoRecoLog();
		autoRecoLog.setRecoId(this.reco.getId());
		autoRecoLog.setRecoName(reco.getName());
		autoRecoLog.setMode(automatic ? RunModes.A : RunModes.M);
		autoRecoLog.setRecoAttributeId(this.reco.getModel().getRecoAttributeId());
		//autoRecoLog.setRecoAttributeName(this.reco.getModel().getr);
		autoRecoLog.setStatus(RunStatus.IN_PROGRESS);
		//autoRecoLog.setUsername(username);
		autoRecoLog.setLeftRowCount(0L);
		autoRecoLog.setRigthRowCount(0L);
		autoRecoLog.setReconciliatedLeftRowCount(0L);
		autoRecoLog.setReconciliatedRigthRowCount(0L);		
		autoRecoLog = autoRecoLogRepository.save(autoRecoLog);		
		log.trace("Auto Reco : {} : Log builded and saved!", reco.getName());
		return autoRecoLog;
	}
	
	protected AutoRecoLog endLog(AutoRecoLog autoRecoLog, String message) {
		log.trace("Auto Reco : {} : End Log...!", reco.getName());
		//autoRecoLog.set = new Timestamp(System.currentTimeMillis());
		autoRecoLog.setStatus(RunStatus.ENDED);
		autoRecoLog = autoRecoLogRepository.save(autoRecoLog);					
		log.trace("Auto Reco : {} : Log ended and saved!", reco.getName());
		return autoRecoLog;
	}

	
	

}

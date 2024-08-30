package com.moriset.bcephal.reconciliation.service;

import java.util.List;
import java.util.Optional;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.parameter.IncrementalNumber;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.reconciliation.domain.ReconciliationData;
import com.moriset.bcephal.repository.filters.AttributeRepository;
import com.moriset.bcephal.repository.filters.IncrementalNumberRepository;
import com.moriset.bcephal.repository.filters.MeasureRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ReconciliationOperation {

	private ReconciliationData data;
	private boolean allowPartialReco;
	
	private Attribute recoAttribute;
	private IncrementalNumber recoSequence;
	private String recoNumber;
	
	private Attribute partialRecoAttribute;
	private IncrementalNumber partialRecoSequence;
	private String partialRecoNumber;
	
	private Attribute neutralizeAttribute;
	private IncrementalNumber neutralizeSequence;
	private String neutralizeNumber;
	
	private Measure leftMeasure;
	private Measure rigthMeasure;
	private Measure reconciliatedMeasure;
	private Measure remainningMeasure;
	
	private IncrementalNumberRepository incrementalNumberRepository;
	private AttributeRepository attributeRepository;
	private MeasureRepository measureRepository;
	
	private EntityManager entityManager;
	
	
	public ReconciliationOperation(ReconciliationData data) {
		this.data = data;
		this.allowPartialReco = data.isAllowPartialReco() && data.getPartialRecoAttributeId() != null;
		this.neutralizeNumber = data.getNeutralizationValue();
	}
	
	public void build(List<Long> ids) throws Exception {
		buildReconciliationNumber();
		buildPartialReconciliationNumber(ids);
		if(data.isPerformNeutralization()) {
			buildNeutralizeNumber();
		}
		readMeasures();
	}

	public String buildReconciliationNumber() throws Exception {
		log.debug("Try to build reconciliation number...");
		Optional<Attribute> response = attributeRepository.findById(data.getRecoTypeId());
		if(response.isEmpty()) {
			log.debug("Unable to reconciliate. The reco type is unknown!. Not found attribute with : {}", data.getRecoTypeId());
			throw new BcephalException("Unknown reco type : " + data.getRecoTypeId());
		}
		recoAttribute = response.get();
		recoNumber =  null;
		recoSequence = null;
		log.debug("Try to build reconciliation number...");
		if(data.getRecoSequenceId() != null) {
			log.debug("Try to read reconciliation sequence by ID : {}", data.getRecoSequenceId());
			Optional<IncrementalNumber> resp = incrementalNumberRepository.findById(data.getRecoSequenceId());
			if(!resp.isEmpty()) {
				recoSequence = resp.get();
				log.debug("Sequence found : {}", recoSequence.getName());
			}
			else {
				log.debug("Sequence not found! : {}", data.getRecoSequenceId());
			}
			if(recoSequence != null) {
				log.debug("Try to build sequence next number : {}", recoSequence.getName());
				recoNumber =  recoSequence.buildNextValue();
			}
		}		
		if(!StringUtils.hasText(recoNumber)) {
			if( recoAttribute.getIncremantalValue() == null) {
				recoAttribute.setIncremantalValue((long) 0); 
			}
			Long number =  recoAttribute.getIncremantalValue() + 1;
			recoAttribute.setIncremantalValue(number);
			recoNumber = "" + number;
		}		
		log.debug("Reconciliation number : {}", recoNumber);
		return recoNumber;
	}
	
	
	public String buildNeutralizeNumber() throws Exception {
		log.debug("Try to build neutralization number...");
		Optional<Attribute> response = attributeRepository.findById(data.getNeutralizationAttributeId());
		if(response.isEmpty()) {
			log.debug("Unable to reconciliate. The neutralization attribute is unknown!. Attribute not found : {}", data.getRecoTypeId());
			throw new BcephalException("Unknown neutralization attribute : " + data.getNeutralizationAttributeId());
		}
		neutralizeAttribute = response.get();	
		if(StringUtils.hasText(data.getNeutralizationValue()) && !data.getNeutralizationValue().trim().isBlank()) {
			neutralizeNumber = data.getNeutralizationValue();
			return neutralizeNumber;
		}
			
		neutralizeSequence = null;
		if(neutralizeNumber == null) {
			log.debug("Try to build neutralization number...");
			if(data.getNeutralizationSequenceId() != null) {
				log.debug("Try to read neutralization sequence by ID : {}", data.getNeutralizationSequenceId());
				Optional<IncrementalNumber> resp = incrementalNumberRepository.findById(data.getNeutralizationSequenceId());
				if(!resp.isEmpty()) {
					neutralizeSequence = resp.get();
					log.debug("Sequence found : {}", neutralizeSequence.getName());
				}
				else {
					log.debug("Sequence not found! : {}", data.getNeutralizationSequenceId());
				}
				if(neutralizeSequence != null) {
					log.debug("Try to build sequence next number : {}", neutralizeSequence.getName());
					neutralizeNumber =  neutralizeSequence.buildNextValue();
				}
			}		
			if(!StringUtils.hasText(neutralizeNumber)) {
				if( neutralizeAttribute.getIncremantalValue() == null) {
					neutralizeAttribute.setIncremantalValue((long) 0); 
				}
				Long number =  neutralizeAttribute.getIncremantalValue() + 1;
				neutralizeAttribute.setIncremantalValue(number);
				neutralizeNumber = "" + number;
			}	
		}
		log.debug("Neutralization number : {}", neutralizeNumber);
		return neutralizeNumber;
	}
	
	private String buildPartialReconciliationNumber(List<Long> ids) throws Exception {
		log.debug("Try to build partial reconciliation number...");
		partialRecoNumber =  null;
		partialRecoSequence = null;
		if(this.allowPartialReco) {
			Optional<Attribute> response = attributeRepository.findById(data.getPartialRecoAttributeId());
			if(response.isEmpty()) {
				log.debug("Unable to reconciliate. The partial reco type is unknown!. Not found attribute with : {}", data.getRecoTypeId());
				throw new BcephalException("Unknown partial reco type : " + data.getRecoTypeId());
			}
			partialRecoAttribute = response.get();
			partialRecoNumber = getPartialRecoNbr(ids);
			if(!StringUtils.hasText(partialRecoNumber)) {
				log.debug("Try to build partial reconciliation number...");
				if(data.getPartialRecoSequenceId() != null) {
					log.debug("Try to read partial reconciliation sequence by ID : {}", data.getPartialRecoSequenceId());
					Optional<IncrementalNumber> resp = incrementalNumberRepository.findById(data.getPartialRecoSequenceId());
					if(!resp.isEmpty()) {
						partialRecoSequence = resp.get();
						log.debug("Sequence found : {}", partialRecoSequence.getName());
					}
					else {
						log.debug("Sequence not found! : {}", data.getPartialRecoSequenceId());
					}
					if(partialRecoSequence != null) {
						log.debug("Try to build sequence next number : {}", partialRecoSequence.getName());
						partialRecoNumber =  partialRecoSequence.buildNextValue();
					}
				}		
				if(!StringUtils.hasText(partialRecoNumber)) {
					if( recoAttribute.getIncremantalValue() == null) {
						recoAttribute.setIncremantalValue((long) 0); 
					}
					Long number =  recoAttribute.getIncremantalValue() + 1;
					recoAttribute.setIncremantalValue(number);
					partialRecoNumber = "" + number;
				}		
			}
			log.debug("Partial reconciliation number : {}", partialRecoNumber);
		}		
		return partialRecoNumber;
	}
	
	private void readMeasures() throws Exception {
		log.debug("Try to read measures...");
		
		Optional<Measure> response = measureRepository.findById(data.getLeftMeasureId());
		if(response.isEmpty()) {
			log.debug("Unable to reconciliate. The left measure is unknown!. Measure not found : {}", data.getLeftMeasureId());
			throw new BcephalException("Unknown left measure : " + data.getLeftMeasureId());
		}
		this.leftMeasure = response.get();
		
		response = measureRepository.findById(data.getRigthMeasureId());
		if(response.isEmpty()) {
			log.debug("Unable to reconciliate. The rigth measure is unknown!. Measure not found : {}", data.getRigthMeasureId());
			throw new BcephalException("Unknown rigth measure : " + data.getRigthMeasureId());
		}
		this.rigthMeasure = response.get();
		
		if(this.allowPartialReco) {
			response = measureRepository.findById(data.getReconciliatedMeasureId());
			if(response.isEmpty()) {
				log.debug("Unable to reconciliate. The reconciliated measure is unknown!. Measure not found : {}", data.getReconciliatedMeasureId());
				throw new BcephalException("Unknown reconciliated measure : " + data.getReconciliatedMeasureId());
			}
			this.reconciliatedMeasure = response.get();
			
			response = measureRepository.findById(data.getRemainningMeasureId());
			if(response.isEmpty()) {
				log.debug("Unable to reconciliate. The remainning measure is unknown!. Measure not found : {}", data.getRemainningMeasureId());
				throw new BcephalException("Unknown remainning measure : " + data.getRemainningMeasureId());
			}
			this.remainningMeasure = response.get();
		}
	}
	
	private String getPartialRecoNbr(List<Long> ids) {
		if(this.allowPartialReco) {				
			String sql = "SELECT " + partialRecoAttribute.getUniverseTableColumnName()
				+ " FROM " + UniverseParameters.UNIVERSE_TABLE_NAME;
							
			String or = " WHERE (";
			for (Number id : ids) {
				sql += or + " id = " + id;
				or = " OR ";
			}
			sql += ") AND " + partialRecoAttribute.getUniverseTableColumnName() + " IS NOT NULL LIMIT 1";
			Query query = entityManager.createNativeQuery(sql);			
			try {
				String pn = (String)query.getSingleResult();
				return pn;
			}
			catch (NoResultException e) { }					
		}	
		return null;
	}
	
}

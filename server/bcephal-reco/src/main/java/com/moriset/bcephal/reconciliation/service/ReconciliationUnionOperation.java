/**
 * 4 avr. 2024 - ReconciliationUnionOperation.java
 *
 */
package com.moriset.bcephal.reconciliation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.parameter.IncrementalNumber;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.SmartMaterializedGridColumn;
import com.moriset.bcephal.grid.domain.UnionGrid;
import com.moriset.bcephal.grid.domain.UnionGridItem;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionData;
import com.moriset.bcephal.repository.filters.AttributeRepository;
import com.moriset.bcephal.repository.filters.IncrementalNumberRepository;
import com.moriset.bcephal.repository.filters.MeasureRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Emmanuel Emmeni
 *
 */
@Data
@Slf4j
public class ReconciliationUnionOperation {

	private ReconciliationUnionData data;
	private boolean allowPartialReco;
	
	private IncrementalNumber recoSequence;
	private String recoNumber;
	
	private IncrementalNumber partialRecoSequence;
	private String partialRecoNumber;
	
	private IncrementalNumber neutralizeSequence;
	private String neutralizeNumber;
		
	private IncrementalNumberRepository incrementalNumberRepository;
	private AttributeRepository attributeRepository;
	private MeasureRepository measureRepository;
	
	private EntityManager entityManager;
	
	
	public ReconciliationUnionOperation(ReconciliationUnionData data) {
		this.data = data;
		this.allowPartialReco = data.isAllowPartialReco() && data.getLeftPartialRecoAttributeId() != null && data.getRightPartialRecoAttributeId() != null;
		this.neutralizeNumber = data.getNeutralizationValue();
	}
	
	public void build(List<String> leftIds, List<String> rightIds) throws Exception {
		buildReconciliationNumber();
		buildPartialReconciliationNumber(leftIds, rightIds);
		if(data.isPerformNeutralization()) {
			buildNeutralizeNumber();
		}
	}

	public String buildReconciliationNumber() throws Exception {
		log.debug("Try to build reconciliation number...");		
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
		log.debug("Reconciliation number : {}", recoNumber);
		return recoNumber;
	}
	
	
	public String buildNeutralizeNumber() throws Exception {
		log.debug("Try to build neutralization number...");
		
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
		}
		log.debug("Neutralization number : {}", neutralizeNumber);
		return neutralizeNumber;
	}
	
	private String buildPartialReconciliationNumber(List<String> leftIds, List<String> rightIds) throws Exception {
		log.debug("Try to build partial reconciliation number...");
		partialRecoNumber =  null;
		partialRecoSequence = null;
		if(this.allowPartialReco) {			
			partialRecoNumber = getPartialRecoNbr(leftIds, rightIds);
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
			}
			log.debug("Partial reconciliation number : {}", partialRecoNumber);
		}		
		return partialRecoNumber;
	}
		
	private String getPartialRecoNbr(List<String> leftIds, List<String> rightIds) {
		String prn = null;
		if(this.allowPartialReco) {				
			if(prn == null && data.getLeftGridType().isUnionGrid() && data.getLeftGrid() != null && leftIds != null && !leftIds.isEmpty()) {
				prn = getPartialRecoNbr(data.getLeftGrid(), data.getLeftPartialRecoAttributeId(), leftIds);
			}
			if(prn == null && data.getRightGridType().isUnionGrid() && data.getRightGrid() != null && rightIds != null && !rightIds.isEmpty()) {
				prn = getPartialRecoNbr(data.getRightGrid(), data.getRightPartialRecoAttributeId(), rightIds);
			}
			
			if(prn == null && !data.getLeftGridType().isUnionGrid() && data.getLeftGrille() != null && leftIds != null && !leftIds.isEmpty()) {
				prn = getPartialRecoNbr(data.getLeftGrille(), data.getLeftPartialRecoAttributeId(), leftIds);
			}
			if(prn == null && !data.getRightGridType().isUnionGrid() && data.getRightGrille() != null && rightIds != null && !rightIds.isEmpty()) {
				prn = getPartialRecoNbr(data.getRightGrille(), data.getRightPartialRecoAttributeId(), rightIds);
			}
		}	
		return prn;
	}
	
	private String getPartialRecoNbr(Grille grid, Long partialRecoColId, List<String> ids) {
		String col = new Attribute(partialRecoColId).getUniverseTableColumnName();
		String sql = "SELECT " + col
			+ " FROM " + UniverseParameters.UNIVERSE_TABLE_NAME;
						
		String or = " WHERE (";
		List<Number> result = buildUniverseIds(ids);
		for (Number id : result) {
			sql += or + " id = " + id;
			or = " OR ";
		}
		sql += ") AND " + col + " IS NOT NULL AND " + col + " != '' LIMIT 1";
		Query query = entityManager.createNativeQuery(sql);			
		try {
			String pn = (String)query.getSingleResult();
			return pn;
		}
		catch (NoResultException e) { }		
		return null;
	}
	
	private String getPartialRecoNbr(UnionGrid grid, Long partialRecoColId, List<String> ids) {
		for(UnionGridItem item : grid.getItemListChangeHandler().getItems()) {
			String pn = getPartialRecoNbr(grid, item, partialRecoColId, ids);
			if(StringUtils.hasText(pn)) {
				return pn;
			}
		}	
		return null;
	}
	
	private String getPartialRecoNbr(UnionGrid grid, UnionGridItem item, Long partialRecoColId, List<String> ids) {
		String gridDbName = item.getGrid().getDbTableName();
		String col = getDbColumnName(partialRecoColId, "Partial reco type", DimensionType.ATTRIBUTE, grid, item, true);
		String sql = "SELECT " + col + " FROM " + gridDbName;
		String or = " WHERE (";
		List<Number> result = buildUnionIds(item.getGrid().getId(), ids);
		if(result.isEmpty()) {
			return null;
		}
		for (Number id : result) {
			sql += or + " id = " + id;
			or = " OR ";
		}
		sql += ") AND " + col + " IS NOT NULL AND " + col + " != '' LIMIT 1";
		Query query = entityManager.createNativeQuery(sql);			
		try {
			String pn = (String)query.getSingleResult();
			return pn;
		}
		catch (NoResultException e) { }	
		return null;
	}
	
	private String getDbColumnName(Long columnId, String columnDescription, DimensionType type, UnionGrid grid, UnionGridItem item, boolean mandatory) {
		SmartMaterializedGridColumn column = item.getGridColumn(columnId);		
		if(column == null) {
			if(mandatory) {
				String massage = "The grid (" + item.getGrid().getName() +") don't contains a column : '" + columnDescription + "' whith id : " + columnId;
				throw new BcephalException(massage);
			}
			return null;
		}
		return column.getDbColumnName();
	}
	
	private List<Number> buildUnionIds(Long gridId, List<String> ids){
		List<Number> result = new ArrayList<>();
		for (String id : ids) {
			String[] val = id.split("_");
			if(val.length > 0 && val[1] != null && gridId.equals(Long.valueOf(val[1]))) {
				result.add(Long.valueOf(val[0]));
			}
		}	
		return result;
	}
	
	private List<Number> buildUniverseIds(List<String> ids){
		List<Number> result = new ArrayList<>();
		for (String id : ids) {
			String[] val = id.split("_");
			if(val.length > 0 && val[0] != null) {
				result.add(Long.valueOf(val[0]));
			}
		}	
		return result;
	}

}

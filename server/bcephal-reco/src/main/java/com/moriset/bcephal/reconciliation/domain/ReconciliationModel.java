/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.grid.domain.Grille;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Moriset
 *
 */
@Entity(name = "ReconciliationModel")
@Table(name = "BCP_RECONCILIATION_MODEL")
@Data
@EqualsAndHashCode(callSuper = false)
public class ReconciliationModel extends MainObject {
	
	private static final long serialVersionUID = -3817508268504627212L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reconciliation_model_seq")
	@SequenceGenerator(name = "reconciliation_model_seq", sequenceName = "reconciliation_model_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	

	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name = "leftGrid")
	private Grille leftGrid;
	private Long leftMeasureId;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name = "rigthGrid")
	private Grille rigthGrid;
	private Long rigthMeasureId;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name = "bottomGrid")
	private Grille bottomGrid;
	
	private Long recoAttributeId;
	private Long recoSequenceId;
	
	private boolean allowPartialReco;
	private Long partialRecoAttributeId;
	private Long partialRecoSequenceId;
	private Long reconciliatedMeasureId;
	private Long remainningMeasureId;
	
	private boolean allowFreeze;
	private Long freezeAttributeId;
	private Long freezeSequenceId;
	
	private boolean allowNeutralization;
	private Long neutralizationAttributeId;
	private Long neutralizationSequenceId;
	private boolean neutralizationRequestSelectValue;
	private boolean neutralizationAllowCreateNewValue;
	private boolean neutralizationInsertNote;
	private boolean neutralizationMandatoryNote;
	private String neutralizationMessage;
	
	private Long noteAttributeId;
	
	@Enumerated(EnumType.STRING) 
	private RecoGridPosition rigthGridPosition;
		
	private boolean allowWriteOff;
		
	@ManyToOne @JoinColumn(name = "writeOffModel")
	private WriteOffModel writeOffModel;
		
	@Enumerated(EnumType.STRING) 
	private ReconciliationModelBalanceFormula balanceFormula;

	private boolean useDebitCredit;
	private boolean allowDebitCreditLineColor;
	private Integer debitLineColor;	
	private Integer creditLineColor;
	
	private boolean addRecoDate;
	private boolean addUser;
	private boolean addAutomaticManual;
	
	private boolean addNote;
	private boolean mandatoryNote;
	
	private boolean confirmAction;
	
	private Long recoPeriodId;
	
	@Enumerated(EnumType.STRING) 
	private AutoRecoMethod method;
	
	private Boolean allowReconciliatedAmountLog;
	
	private Long reconciliatedAmountLogGridId;
	
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "model")
	private List<ReconciliationModelEnrichment> enrichments;

	@Transient 
	private ListChangeHandler<ReconciliationModelEnrichment> enrichmentListChangeHandler;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "recoModelId")
	private List<ReconciliationCondition> conditions;

	@Transient 
	public ListChangeHandler<ReconciliationCondition> conditionListChangeHandler;
	
	
	public ReconciliationModel() {
		this.enrichments = new ArrayList<ReconciliationModelEnrichment>();		
		this.enrichmentListChangeHandler = new ListChangeHandler<ReconciliationModelEnrichment>();
		this.conditions = new ArrayList<ReconciliationCondition>();		
		this.conditionListChangeHandler = new ListChangeHandler<ReconciliationCondition>();		
		this.balanceFormula = ReconciliationModelBalanceFormula.LEFT_MINUS_RIGHT;
		this.method = AutoRecoMethod.BOTH_CUMULATED;
		this.allowReconciliatedAmountLog = false; 
	}

	public void setEnrichments(List<ReconciliationModelEnrichment> enrichments) {
		this.enrichments = enrichments;
		enrichmentListChangeHandler.setOriginalList(enrichments);
	}
	
	public void setConditions(List<ReconciliationCondition> conditions) {
		this.conditions = conditions;
		conditionListChangeHandler.setOriginalList(conditions);
	}
	
	public boolean getAllowReconciliatedAmountLog() {
		if(allowReconciliatedAmountLog == null) {
			allowReconciliatedAmountLog = false;
		}
		return allowReconciliatedAmountLog;
	}
		
	@PostLoad
	public void initListChangeHandler() {
		this.enrichments.size();
		this.enrichmentListChangeHandler.setOriginalList(enrichments);	
		this.conditions.size();
		this.conditionListChangeHandler.setOriginalList(conditions);
	}

	@JsonIgnore
	public List<ReconciliationModelEnrichment> getSortedEnrichments() {
		List<ReconciliationModelEnrichment> conditions = getEnrichmentListChangeHandler().getItems();
		Collections.sort(conditions, new Comparator<ReconciliationModelEnrichment>() {
			@Override
			public int compare(ReconciliationModelEnrichment item1, ReconciliationModelEnrichment item2) {
				return item1.getPosition() - item2.getPosition();
			}
		});
		return conditions;
	}

	@JsonIgnore
	@Override
	public ReconciliationModel copy() {
		ReconciliationModel copy = new ReconciliationModel();
		copy.setName(this.getName() + System.currentTimeMillis());
		copy.setGroup(this.getGroup());
		copy.setVisibleInShortcut(isVisibleInShortcut());
		
		
		copy.setLeftGrid(leftGrid != null ? leftGrid.copy() : null);
		if(leftGrid != null) {
			copy.getLeftGrid().setName(leftGrid.getName());
		}		
		copy.setLeftMeasureId(leftMeasureId);		
		copy.setRigthGrid(rigthGrid != null ? rigthGrid.copy() : null);
		if(rigthGrid != null) {
			copy.getRigthGrid().setName(rigthGrid.getName());
		}
		copy.setRigthMeasureId(rigthMeasureId);		
		copy.setBottomGrid(bottomGrid != null ? bottomGrid.copy() : null);		
		if(bottomGrid != null) {
			copy.getBottomGrid().setName(bottomGrid.getName());
		}	
		
		copy.setRecoAttributeId(recoAttributeId);
		copy.setRecoSequenceId(recoSequenceId);
		
		copy.setAllowPartialReco(allowPartialReco);
		copy.setPartialRecoAttributeId(partialRecoAttributeId);
		copy.setPartialRecoSequenceId(partialRecoSequenceId);
		copy.setReconciliatedMeasureId(reconciliatedMeasureId);
		copy.setRemainningMeasureId(remainningMeasureId);
		
		copy.setAllowFreeze(allowFreeze);
		copy.setFreezeAttributeId(freezeAttributeId);
		copy.setFreezeSequenceId(freezeSequenceId);
		
		copy.setAllowNeutralization(allowNeutralization);
		copy.setNeutralizationAttributeId(neutralizationAttributeId);
		copy.setNeutralizationSequenceId(neutralizationSequenceId);
		copy.setNeutralizationRequestSelectValue(neutralizationRequestSelectValue);
		copy.setNeutralizationAllowCreateNewValue(neutralizationAllowCreateNewValue);
		copy.setNeutralizationInsertNote(neutralizationInsertNote);
		copy.setNeutralizationMandatoryNote(neutralizationMandatoryNote);
		copy.setNeutralizationMessage(neutralizationMessage);
		
		copy.setNoteAttributeId(noteAttributeId);		
		copy.setRigthGridPosition(rigthGridPosition);
		copy.setAllowWriteOff(allowWriteOff);
			
		copy.setWriteOffModel(writeOffModel != null ? writeOffModel.copy() : null);
			
		copy.setBalanceFormula(balanceFormula);
		copy.setUseDebitCredit(useDebitCredit);
		copy.setAllowDebitCreditLineColor(allowDebitCreditLineColor);
		copy.setDebitLineColor(debitLineColor);
		copy.setCreditLineColor(creditLineColor);		
		copy.setAddRecoDate(addRecoDate);
		copy.setAddUser(addUser);
		copy.setAddAutomaticManual(addAutomaticManual);		
		copy.setAddNote(addNote);
		copy.setMandatoryNote(mandatoryNote);		
		copy.setRecoPeriodId(recoPeriodId);
		copy.setAllowReconciliatedAmountLog(allowReconciliatedAmountLog);
		copy.setReconciliatedAmountLogGridId(reconciliatedAmountLogGridId);
		copy.setMethod(method);
		
		for(ReconciliationModelEnrichment enrichment : getEnrichmentListChangeHandler().getItems()) {
			copy.getEnrichmentListChangeHandler().addNew(enrichment.copy());
		}		
		for(ReconciliationCondition condition : getConditionListChangeHandler().getItems()) {
			copy.getConditionListChangeHandler().addNew(condition.copy());
		}
		
		return copy;
	}

	
	
}

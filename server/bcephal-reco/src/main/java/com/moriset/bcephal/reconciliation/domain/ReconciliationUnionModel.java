package com.moriset.bcephal.reconciliation.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.MainObject;

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

@Entity(name = "ReconciliationUnionModel")
@Table(name = "BCP_RECONCILIATION_UNION_MODEL")
@Data
@EqualsAndHashCode(callSuper = false)
public class ReconciliationUnionModel extends MainObject {
	
	private static final long serialVersionUID = -3817508268504627212L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reconciliation_union_model_seq")
	@SequenceGenerator(name = "reconciliation_union_model_seq", sequenceName = "reconciliation_union_model_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	

	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name = "leftGrid")
	private ReconciliationUnionModelGrid leftGrid;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name = "rigthGrid")
	private ReconciliationUnionModelGrid rigthGrid;
		
	private Long recoSequenceId;
	
	private boolean allowPartialReco;
	private Long partialRecoSequenceId;
	
	private boolean allowFreeze;
	private Long freezeSequenceId;
	
	private boolean allowNeutralization;
	private Long neutralizationSequenceId;
	private boolean neutralizationRequestSelectValue;
	private boolean neutralizationAllowCreateNewValue;
	private boolean neutralizationInsertNote;
	private boolean neutralizationMandatoryNote;
	private String neutralizationMessage;
		
	@Enumerated(EnumType.STRING) 
	private RecoGridPosition rigthGridPosition;
		
	private boolean allowWriteOff;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name = "writeOffModel")
	private WriteOffUnionModel writeOffModel;
		
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
	
	@Enumerated(EnumType.STRING) 
	private AutoRecoMethod method;
	
	private Boolean allowReconciliatedAmountLog;
	
	private Long reconciliatedAmountLogGridId;
	
	private Long balanceFormatNumber;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "model")
	private List<ReconciliationUnionModelEnrichment> enrichments;

	@Transient 
	private ListChangeHandler<ReconciliationUnionModelEnrichment> enrichmentListChangeHandler;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "model")
	private List<ReconciliationUnionModelButtonColumn> buttonColumns;

	@Transient 
	public ListChangeHandler<ReconciliationUnionModelButtonColumn> buttonColumnListChangeHandler;
	
	
	public ReconciliationUnionModel() {
		this.enrichments = new ArrayList<>();		
		this.enrichmentListChangeHandler = new ListChangeHandler<>();
		this.buttonColumns = new ArrayList<>();		
		this.buttonColumnListChangeHandler = new ListChangeHandler<>();		
		this.balanceFormula = ReconciliationModelBalanceFormula.LEFT_MINUS_RIGHT;
		this.method = AutoRecoMethod.BOTH_CUMULATED;
		this.allowReconciliatedAmountLog = false; 
	}

	public void setEnrichments(List<ReconciliationUnionModelEnrichment> enrichments) {
		this.enrichments = enrichments;
		enrichmentListChangeHandler.setOriginalList(enrichments);
	}
	
	public void setButtonColumns(List<ReconciliationUnionModelButtonColumn> buttonColumns) {
		this.buttonColumns = buttonColumns;
		buttonColumnListChangeHandler.setOriginalList(buttonColumns);
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
		this.buttonColumns.size();
		this.buttonColumnListChangeHandler.setOriginalList(buttonColumns);
	}

	@JsonIgnore
	public List<ReconciliationUnionModelEnrichment> getSortedEnrichments() {
		List<ReconciliationUnionModelEnrichment> conditions = getEnrichmentListChangeHandler().getItems();
		Collections.sort(conditions, new Comparator<ReconciliationUnionModelEnrichment>() {
			@Override
			public int compare(ReconciliationUnionModelEnrichment item1, ReconciliationUnionModelEnrichment item2) {
				return item1.getPosition() - item2.getPosition();
			}
		});
		return conditions;
	}
	

	public void sort() {
		setEnrichments(getSortedEnrichments());
		if(getLeftGrid() != null) getLeftGrid().sort();
		if(getRigthGrid() != null) getRigthGrid().sort();
	}

	@JsonIgnore
	@Override
	public ReconciliationUnionModel copy() {
		ReconciliationUnionModel copy = new ReconciliationUnionModel();
		copy.setName(this.getName() + System.currentTimeMillis());
		copy.setGroup(this.getGroup());
		copy.setVisibleInShortcut(isVisibleInShortcut());
		
		
		copy.setLeftGrid(leftGrid != null ? leftGrid.copy() : null);
		copy.setRigthGrid(rigthGrid != null ? rigthGrid.copy() : null);
		copy.setRecoSequenceId(recoSequenceId);
		
		copy.setAllowPartialReco(allowPartialReco);
		copy.setPartialRecoSequenceId(partialRecoSequenceId);
		
		copy.setAllowFreeze(allowFreeze);
		copy.setFreezeSequenceId(freezeSequenceId);
		
		copy.setAllowNeutralization(allowNeutralization);
		copy.setNeutralizationSequenceId(neutralizationSequenceId);
		copy.setNeutralizationRequestSelectValue(neutralizationRequestSelectValue);
		copy.setNeutralizationAllowCreateNewValue(neutralizationAllowCreateNewValue);
		copy.setNeutralizationInsertNote(neutralizationInsertNote);
		copy.setNeutralizationMandatoryNote(neutralizationMandatoryNote);
		copy.setNeutralizationMessage(neutralizationMessage);
			
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
		copy.setAllowReconciliatedAmountLog(allowReconciliatedAmountLog);
		copy.setReconciliatedAmountLogGridId(reconciliatedAmountLogGridId);
		copy.setMethod(method);
		
		for(ReconciliationUnionModelEnrichment enrichment : getEnrichmentListChangeHandler().getItems()) {
			copy.getEnrichmentListChangeHandler().addNew(enrichment.copy());
		}
		for(ReconciliationUnionModelButtonColumn column : getButtonColumnListChangeHandler().getItems()) {
			copy.getButtonColumnListChangeHandler().addNew(column.copy());
		}
		
		return copy;
	}


	
	
}

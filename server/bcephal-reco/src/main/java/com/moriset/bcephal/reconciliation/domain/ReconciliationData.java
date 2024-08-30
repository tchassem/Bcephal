/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.AttributeValue;
import com.moriset.bcephal.domain.dimension.EnrichmentValue;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class ReconciliationData {

	private Long reconciliationId;
	private List<Long> leftids;
	private List<Long> rightids;
	
	private boolean selectAllRowsInLeftGrid;
	private GrilleDataFilter leftBrowserDataFilter;
	
	private boolean selectAllRowsInRightGrid;
	private GrilleDataFilter rightBrowserDataFilter;
	
	private boolean useDebitCredit;
	
	private Long recoTypeId;
	
	private Long recoSequenceId;
	
	private Long leftMeasureId;	
	private Long rigthMeasureId;

	private boolean performPartialReco;
    private boolean allowPartialReco;
    private Long partialRecoAttributeId;
    private Long partialRecoSequenceId;
    private Long reconciliatedMeasureId;
    private Long remainningMeasureId;
    public List<PartialRecoItem> partialRecoItems;

    private boolean allowFreeze;
    private Long freezeAttributeId;
    private Long freezeSequenceId;

    private boolean performNeutralization;
    private boolean allowNeutralization;
    private Long neutralizationAttributeId;
    private Long neutralizationSequenceId;
    private String neutralizationValue;
	
    private Long noteAttributeId;
	
	private Long writeOffMeasureId;
	private BigDecimal writeOffAmount;
	private BigDecimal leftAmount;
	private BigDecimal rigthAmount;	
	private BigDecimal balanceAmount;
	private BigDecimal reconciliatedAmount;
	private List<WriteOffField> writeOffFields;
	
	private boolean addRecoDate;	
	private boolean addUser;	
	private boolean addNote;	
	private boolean mandatoryNote;	
	private String note;	
	private boolean addAutomaticManual;	
	private Long recoDateId;
	
	private Attribute autoManualAttribute;
	private AttributeValue automaticValue;
	private AttributeValue manualValue;
	private Attribute userAttribute;
	
	private Integer defaultRecoValue = null;
	
	private String primaryView;
	private String SecondaryView;
	private List<Long> primaryids;
	private List<Long> secondaryids;
	
	private List<EnrichmentValue> enrichmentItemDatas;
	
	
	private boolean allowReconciliatedAmountLog;	
	private Long reconciliatedAmountLogGridId;
	
	
	public ReconciliationData() {
		leftids = new ArrayList<Long>(0);
		rightids = new ArrayList<Long>(0);
		writeOffAmount = BigDecimal.ZERO;
		leftAmount = BigDecimal.ZERO;
		rigthAmount = BigDecimal.ZERO;	
		balanceAmount = BigDecimal.ZERO;
		reconciliatedAmount = BigDecimal.ZERO;
		primaryids = new ArrayList<Long>(0);
		secondaryids = new ArrayList<Long>(0);
		writeOffFields = new ArrayList<WriteOffField>(0);
		enrichmentItemDatas = new ArrayList<EnrichmentValue>(0);
		partialRecoItems = new ArrayList<PartialRecoItem>(0);
	}
}

/**
 * 
 */
package com.moriset.bcephal.domain.filters;

import lombok.Data;

/**
 * @author Moriset
 *
 */
@Data
public class ReconciliationDataFilter {
	
	private Long mainGridId;
	private String mainGridType;
	
	private Long recoAttributeId;
    private Long recoSequenceId;
    private Long amountMeasureId;

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
    public String neutralizationMessage;

    private Long noteAttributeId;
    
    private Long debitCreditAttributeId;

    private boolean conterpart;

    private boolean credit;

    private boolean debit;
    
    private boolean useDebitCredit;

    
	
}

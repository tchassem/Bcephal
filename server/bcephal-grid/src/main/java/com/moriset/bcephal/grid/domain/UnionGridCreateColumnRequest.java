package com.moriset.bcephal.grid.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnionGridCreateColumnRequest {

	private Long modelId;
	
	private String leftRecoName;
	private String leftPartialRecoName;
	private String leftMeasureName;
	private String leftReconciliatedAmountName;
	private String leftRemainingAmountName;
	private String leftNoteName;
	private String leftNeutralizationName;
	private String leftFreezeName;
	private String leftRecoDateName;
	private String leftUserName;
	private String leftAutomaticName;
	private String leftDeltaMeasureName;
	private String leftDebitCreditName;
	
	private String rightRecoName;
	private String rightPartialRecoName;
	private String rightMeasureName;
	private String rightReconciliatedAmountName;
	private String rightRemainingAmountName;
	private String rightNoteName;
	private String rightNeutralizationName;
	private String rightFreezeName;
	private String rightRecoDateName;
	private String rightUserName;
	private String rightAutomaticName;
	private String rightDeltaMeasureName;
	private String rightDebitCreditName;
	
	@JsonIgnore
	public UnionGridCreateColumnRequestData getLeftData() {
		return UnionGridCreateColumnRequestData.builder()
				.automaticName(leftAutomaticName)
				.debitCreditName(leftDebitCreditName)
				.deltaMeasureName(leftDeltaMeasureName)
				.freezeName(leftFreezeName)
				.measureName(leftMeasureName)
				.neutralizationName(leftNeutralizationName)
				.noteName(leftNoteName)
				.partialRecoName(leftPartialRecoName)
				.recoDateName(leftRecoDateName)
				.recoName(leftRecoName)
				.reconciliatedAmountName(leftReconciliatedAmountName)
				.remainingAmountName(leftRemainingAmountName)
				.userName(leftUserName)
				.build();
	}
	
	@JsonIgnore
	public UnionGridCreateColumnRequestData getRightData() {
		return UnionGridCreateColumnRequestData.builder()
				.automaticName(rightAutomaticName)
				.debitCreditName(rightDebitCreditName)
				.deltaMeasureName(rightDeltaMeasureName)
				.freezeName(rightFreezeName)
				.measureName(rightMeasureName)
				.neutralizationName(rightNeutralizationName)
				.noteName(rightNoteName)
				.partialRecoName(rightPartialRecoName)
				.recoDateName(rightRecoDateName)
				.recoName(rightRecoName)
				.reconciliatedAmountName(rightReconciliatedAmountName)
				.remainingAmountName(rightRemainingAmountName)
				.userName(rightUserName)
				.build();
	}
	
	
}



package com.moriset.bcephal.grid.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnionGridCreateColumnRequestData {

	private String recoName;
	private String partialRecoName;
	private String measureName;
	private String reconciliatedAmountName;
	private String remainingAmountName;
	private String noteName;
	private String neutralizationName;
	private String freezeName;
	private String recoDateName;
	private String userName;
	private String automaticName;
	private String deltaMeasureName;
	private String debitCreditName;
	
}

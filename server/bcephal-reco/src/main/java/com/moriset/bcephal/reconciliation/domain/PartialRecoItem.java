/**
 * 
 */
package com.moriset.bcephal.reconciliation.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * @author Moriset
 *
 */
@Data
public class PartialRecoItem {

	private Long gridId;
	private Long id;
	private boolean left;
	private BigDecimal amount;
	private BigDecimal reconciliatedAmount;
	private BigDecimal remainningAmount;
	
	@JsonIgnore
	private boolean primary;
	
	@JsonIgnore
	private BigDecimal realAmount;
	
	@JsonIgnore
	public BigDecimal buildRealAmount(boolean useCreditDebit, boolean isDebit) {
		realAmount = remainningAmount;
		if(useCreditDebit && isDebit) {
			realAmount = BigDecimal.ZERO.subtract(remainningAmount);
		}
		return realAmount;
	}
	
}

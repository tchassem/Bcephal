/**
 * 
 */
package com.moriset.bcephal.accounting.domain;

import java.math.BigDecimal;

import com.moriset.bcephal.accounting.domain.BookingModelLogItem.BookingModelLogItemStatus;
import com.moriset.bcephal.domain.BrowserData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author MORISET-004
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class BookingModelLogItemBrowserData extends BrowserData {
	
	private BigDecimal creditAmount;
	
	private BigDecimal debitAmount;
	
	private BigDecimal balanceAmount;
			
	private BookingModelLogItemStatus status;
				
	private Long accountCount;
	
	private Long postingEntryCount;
	
	private String message;
	
	
	public BookingModelLogItemBrowserData(BookingModelLogItem log) {
		super(log.getId(),log.getName());
		setCreditAmount(log.getCreditAmount());
		setDebitAmount(debitAmount);
		this.debitAmount = log.getDebitAmount();
		this.balanceAmount = log.getBalanceAmount();
		this.postingEntryCount = log.getPostingEntryCount();
		this.status = log.getStatus();
		this.accountCount = log.getAccountCount();
		this.message = log.getMessage();
	}
	
}

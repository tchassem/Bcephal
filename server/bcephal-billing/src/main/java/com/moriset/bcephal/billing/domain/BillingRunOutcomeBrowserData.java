/**
 * 
 */
package com.moriset.bcephal.billing.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.utils.JsonDateDeserializer;
import com.moriset.bcephal.utils.JsonDateSerializer;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BillingRunOutcomeBrowserData extends BrowserData {
	
	private String runNumber;
	
	@Enumerated(EnumType.STRING) 
	private InvoiceStatus status;
	
	@Enumerated(EnumType.STRING) 
    private RunModes mode;
	
	private String username;
	
	private long invoiceCount;
	
	private BigDecimal invoiceAmount;
	
	private long creditNoteCount;
	
	private BigDecimal creditNoteAmount;
		
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date periodFrom;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date periodTo;
	
	public BillingRunOutcomeBrowserData() {
		
	}
	
	public BillingRunOutcomeBrowserData(BillingRunOutcome item) {
		super(item);
		this.runNumber = item.getRunNumber();
		this.mode = item.getMode();
		this.status = item.getStatus();
		this.username = item.getUsername();
		this.invoiceCount = item.getInvoiceCount();
		this.invoiceAmount = item.getInvoiceAmount();
		this.creditNoteCount = item.getCreditNoteCount();
		this.creditNoteAmount = item.getCreditNoteAmount();
		this.periodFrom = item.getPeriodFrom();
		this.periodTo = item.getPeriodTo();
	}

	
}

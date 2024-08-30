/**
 * 
 */
package com.moriset.bcephal.billing.domain;


import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.MailSendingStatus;
import com.moriset.bcephal.utils.JsonDateDeserializer;
import com.moriset.bcephal.utils.JsonDateSerializer;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class InvoiceBrowserData extends BrowserData {

	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date billingDate;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date invoiceDate;
	
	private String reference;
	
	private String runNumber;
	
	private String clientNumber;
	
	private String clientName;
	
	private BigDecimal amountWithoutVat;
	
	private BigDecimal vatAmount;
	
	private int version;
	
	private boolean manuallyModified;
	
	private InvoiceStatus status;
	
	private MailSendingStatus mailStatus;
	
	
	public InvoiceBrowserData(Invoice invoice) {
		super(invoice);
		this.reference = invoice.getReference();
		this.runNumber = invoice.getRunNumber();
		this.billingDate = invoice.getBillingDate();
		this.invoiceDate = invoice.getInvoiceDate();
		this.clientNumber = invoice.getClientNumber();
		this.clientName = invoice.getClientName();
		this.amountWithoutVat = invoice.getAmountWithoutVat();
		
		this.vatAmount = invoice.getVatAmount();
		this.version = invoice.getVersion();
		this.manuallyModified = invoice.isManuallyModified();
		this.status = invoice.getStatus();
		this.mailStatus = invoice.getMailStatus();
	}
}

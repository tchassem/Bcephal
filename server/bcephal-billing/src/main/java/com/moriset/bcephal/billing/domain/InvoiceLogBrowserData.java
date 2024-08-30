package com.moriset.bcephal.billing.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.utils.JsonDateDeserializer;
import com.moriset.bcephal.utils.JsonDateSerializer;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class InvoiceLogBrowserData extends BrowserData {
	
	private String username;
	
	private InvoiceStatus status;
	
	private String file;
	
	private int version;
	
	private BigDecimal amountWithoutVatBefore;
	
	private BigDecimal vatAmountBefore;
	
	private BigDecimal totalAmountBefore;
	
	private BigDecimal amountWithoutVatAfter;
	
	private BigDecimal vatAmountAfter;
	
	private BigDecimal totalAmountAfter;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date date;
	
	private String oldValue;
	
	private String newValue;
	
	private RunModes mode;
	
	public InvoiceLogBrowserData(InvoiceLog invoiceLog) {
		setUsername(invoiceLog.getUsername());
		setStatus(invoiceLog.getStatus());
		setFile(invoiceLog.getFile());
		setAmountWithoutVatBefore(invoiceLog.getAmountWithoutVatBefore());
		setVatAmountBefore(invoiceLog.getVatAmountBefore());
		setTotalAmountBefore(invoiceLog.getTotalAmountBefore());
		setAmountWithoutVatAfter(invoiceLog.getAmountWithoutVatAfter());
		setVatAmountAfter(invoiceLog.getVatAmountAfter());
		setTotalAmountAfter(invoiceLog.getTotalAmountAfter());
		setDate(invoiceLog.getDate());
		setOldValue(invoiceLog.getOldValue());
		setNewValue(invoiceLog.getNewValue());
		setMode(invoiceLog.getMode());
	}

}

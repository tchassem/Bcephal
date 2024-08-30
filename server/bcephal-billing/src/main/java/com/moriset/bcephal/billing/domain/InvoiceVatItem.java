/**
 * 
 */
package com.moriset.bcephal.billing.domain;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class InvoiceVatItem {

	public BigDecimal vatRate;
	
	public BigDecimal amountWithoutVat;
	
	public String unit;
	
	
	
	public InvoiceVatItem() { 
		this.vatRate = BigDecimal.ZERO;
		this.amountWithoutVat = BigDecimal.ZERO;
	}
	
	public InvoiceVatItem(BigDecimal vatRate) {
		this();
		this.vatRate = vatRate;
	}
	
	public InvoiceVatItem(BigDecimal vatRate, BigDecimal amountWithoutVat) {
		this(vatRate);
		this.amountWithoutVat = amountWithoutVat;
	}
	
	public InvoiceVatItem(BigDecimal vatRate, BigDecimal amountWithoutVat, String unit) {
		this(vatRate, amountWithoutVat);
		this.unit = unit;
	}

	public BigDecimal getVatAmount() {
		if(this.vatRate != null && this.amountWithoutVat != null) {
			return this.vatRate.multiply(this.amountWithoutVat).divide(new BigDecimal(100));
		}
		return BigDecimal.ZERO;
	}
	
	public String getName() {
		return vatRate != null ? "" + vatRate + "%" : null;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}

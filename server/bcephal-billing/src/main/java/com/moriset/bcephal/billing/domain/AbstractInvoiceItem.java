/**
 * 
 */
package com.moriset.bcephal.billing.domain;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@jakarta.persistence.MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractInvoiceItem extends Persistent {
	 
	private static final long serialVersionUID = -6023927711751386249L;
	
	private int position;
	
	private String category;
	
	private String description;
	
	private boolean includeQuantity;
	
	private BigDecimal quantity;
	
	private String unit;
	
	private boolean includeUnitCost;
	
	private BigDecimal unitCost;
	
	private BigDecimal amount;
		
	private BigDecimal vatRate;

	private String currency;
	
	
	private BigDecimal billingQuantity;
	
	private BigDecimal billingUnitCost;
	
	private BigDecimal billingAmount;
		
	private BigDecimal billingVatRate;
	
	
	private boolean subjectToVat;
	
	private boolean useUnitCost;
	
	
	private Integer driverDecimalNumber;

	private Integer unitCostDecimalNumber;

	private Integer billingAmountDecimalNumber;
	
	
	@Transient @JsonIgnore
	private List<Long> eventIds;
	
	@Transient @JsonIgnore
	private InvoiceItemBillingElements billingElements;
	
	
	public AbstractInvoiceItem() {
		eventIds = new ArrayList<Long>(0);
		setUseUnitCost(true);
		setIncludeQuantity(true);
		setIncludeUnitCost(true);
	}
	
	public BigDecimal getQuantity() {
		if(quantity == null) {
			quantity = isUseUnitCost() ? BigDecimal.ONE : BigDecimal.ZERO;
		}
		return quantity;
	}
	
	public BigDecimal getAmount() {
		if(amount == null) {
			amount = BigDecimal.ZERO;
		}
		return amount;
	}
	

	
	public BigDecimal getUnitCost() {
		if(unitCost == null) {
			unitCost = BigDecimal.ZERO;
		}
		return unitCost;
	}
	
	
	public BigDecimal getVatRate() {
		if(vatRate == null) {
			vatRate = BigDecimal.ZERO;
		}
		return vatRate;
	}
	
	private String getPattern(Integer len) {
		if(len == null) {
			len = 2;
		}
		String baseExp = "##,##0";
		String exp = baseExp;
		int offset = 0;
		while(offset < len) {
			if(exp.equals(baseExp)) {
				exp += ".0";
			}else {
				exp += "0";
			}
			offset++;
		}
		return exp;
	}
	
	public String getFormatQuantity(Locale locale) {
		if(locale == null) {
			locale = Locale.US;
		}
		DecimalFormat format = new DecimalFormat(getPattern(driverDecimalNumber), DecimalFormatSymbols.getInstance(locale));
		return format.format(getQuantity());
	}
	

	
	public String getFormatUnitCost(Locale locale) {
		if(locale == null) {
			locale = Locale.US;
		}
		DecimalFormat format = new DecimalFormat(getPattern(unitCostDecimalNumber), DecimalFormatSymbols.getInstance(locale));
		return format.format(getUnitCost());
	}
	
	
	public String getFormatAmount(Locale locale) {
		if(locale == null) {
			locale = Locale.US;
		}
		DecimalFormat format = new DecimalFormat(getPattern(billingAmountDecimalNumber), DecimalFormatSymbols.getInstance(locale));
		return format.format(getAmount());
	}
	
	public String getFormatVatRate(Locale locale) {
		if(locale == null) {
			locale = Locale.US;
		}
		DecimalFormat format = new DecimalFormat(getPattern(billingAmountDecimalNumber), DecimalFormatSymbols.getInstance(locale));
		return format.format(getVatRate());
	}
	
	public String getFormatValue(Locale locale,BigDecimal value, int decimalNumber) {
		if(locale == null) {
			locale = Locale.US;
		}
		DecimalFormat format = new DecimalFormat(getPattern(decimalNumber), DecimalFormatSymbols.getInstance(locale));
		return format.format(value);
	}
	
}

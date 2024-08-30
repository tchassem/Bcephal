package com.moriset.bcephal.billing.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class InvoiceItemBillingElements {

	private int billingEventCount;
	
	private CalculateBillingItem calculateBillingItem;
	
	private List<BigDecimal> amounts;
	private List<BigDecimal> unitCosts;
	private List<BigDecimal> drivers;
	
	private BigDecimal amount;
	private BigDecimal unitCost;
	private BigDecimal driver;
	
	public InvoiceItemBillingElements() {
		billingEventCount = 0;
		amounts = new ArrayList<>();
		unitCosts = new ArrayList<>();
		drivers = new ArrayList<>();
	}
	
	public InvoiceItemBillingElements(CalculateBillingItem calculateBillingItem) {
		this();
		this.calculateBillingItem = calculateBillingItem;
	}
		
	public void buildElements() {
		if(calculateBillingItem != null) {
			if(isBillingAmount()) {
				if(driver == null) {
					driver = buildPrimary(drivers, calculateBillingItem.getDriverFunction(), "AVG", BigDecimal.ONE);
				}
				if(unitCost == null) {
					unitCost = buildPrimary(unitCosts, calculateBillingItem.getUnitCostFunction(), "MAX", BigDecimal.ZERO);
				}
				amount = driver.multiply(unitCost);
			}
			else if(isUnitCost()) {
				if(driver == null) {
					driver = buildPrimary(drivers, calculateBillingItem.getDriverFunction(), "AVG", BigDecimal.ONE);
				}
				if(amount == null) {
					amount = buildPrimary(amounts, calculateBillingItem.getBillingAmountFunction(), "SUM", BigDecimal.ZERO);
				}
				unitCost = amount.divide(driver, new MathContext(14));
			}
			else if(isDriver()) {
				if(unitCost == null) {
					unitCost = buildPrimary(unitCosts, calculateBillingItem.getUnitCostFunction(), "MAX", BigDecimal.ZERO);
				}
				if(amount == null) {
					amount = buildPrimary(amounts, calculateBillingItem.getBillingAmountFunction(), "SUM", BigDecimal.ZERO);
				}
				driver = amount.divide(unitCost, new MathContext(14));
			}
			else if(isUnitCostEqualsBillingAmount()) {
				if(unitCost == null) {
					unitCost = buildPrimary(unitCosts, calculateBillingItem.getUnitCostFunction(), "MAX", BigDecimal.ZERO);
				}
				if(amount == null) {
					amount = buildPrimary(amounts, calculateBillingItem.getBillingAmountFunction(), "MAX", BigDecimal.ZERO);
				}
				driver = BigDecimal.ONE;
			}
		}
	}
	
	private BigDecimal buildPrimary(List<BigDecimal> values, String function, String defaultFunction, BigDecimal defaultValue) {
		BigDecimal value = BigDecimal.ZERO;
		if(!StringUtils.hasText(function)) {
			 function = defaultFunction;
		}
		if("SUM".equalsIgnoreCase(function)) {
			for(BigDecimal v : values) {
				value = value.add(v);
			}
		}
		else if("AVG".equalsIgnoreCase(function)) {
			for(BigDecimal v : values) {
				value = value.add(v);
			}
			value = value.divide(new BigDecimal(billingEventCount), new MathContext(14));
		}
		else if("MAX".equalsIgnoreCase(function) || "MIN".equalsIgnoreCase(function)) {
			for(BigDecimal v : values) {
				if(v != null && "MAX".equalsIgnoreCase(function) && v.compareTo(value) > 0) {
					value = v;
				}
				else if(v != null && "MIN".equalsIgnoreCase(function) && v.compareTo(value) < 0) {
					value = v;
				}
			}
		}	
		if(value == null) {
			value = defaultValue;
		}
		return value;
	}

	public void incrementBillingEventCount() {
		incrementBillingEventCount(1);
	}
	
	public void incrementBillingEventCount(int nbr) {
		billingEventCount += nbr;
	}

	public void merge(InvoiceItemBillingElements billingElements) {
		if(billingElements != null) {
			this.incrementBillingEventCount(billingElements.getBillingEventCount());
			this.amounts.addAll(billingElements.getAmounts());
			this.unitCosts.addAll(billingElements.getUnitCosts());
			this.drivers.addAll(billingElements.getDrivers());
		}
	}
	
	public boolean isBillingAmount() {
		return calculateBillingItem != null && calculateBillingItem.getType() != null && calculateBillingItem.getType().isBillingAmount();
	}
	
	public boolean isDriver() {
		return calculateBillingItem != null && calculateBillingItem.getType() != null && calculateBillingItem.getType().isDriver();
	}
	
	public boolean isUnitCost() {
		return calculateBillingItem != null && calculateBillingItem.getType() != null && calculateBillingItem.getType().isUnitCost();
	}
	
	public boolean isUnitCostEqualsBillingAmount() {
		return calculateBillingItem != null && calculateBillingItem.getType() != null && calculateBillingItem.getType().isUnitCostEqualsBillingAmount();
	}
	
}

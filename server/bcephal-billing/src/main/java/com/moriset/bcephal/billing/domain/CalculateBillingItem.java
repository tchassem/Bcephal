package com.moriset.bcephal.billing.domain;

import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.billing.enumeration.BillingElementType;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "CalculateBillingItem")
@Table(name = "BCP_CALCULATE_BILLING_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class CalculateBillingItem extends Persistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7816505538048649550L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calc_bill_item__seq")
	@SequenceGenerator(name = "calc_bill_item__seq", sequenceName = "calc_bill_item__seq", initialValue = 1,  allocationSize = 1)
	private Long id;	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@jakarta.persistence.JoinColumn(name = "modelId")
	public BillingModel modelId;
	
	@Enumerated(EnumType.STRING)
	private BillingElementType type;

	private String driverFunction;

	private String unitCostFunction;

	private String billingAmountFunction;

	private int position;
	
	private Integer driverDecimalNumber;

	private Integer unitCostDecimalNumber;

	private Integer billingAmountDecimalNumber;

	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "calculateEltId")
	private List<CalculateBillingFilterItem> billingFilterItems;
	@Transient 
	private ListChangeHandler<CalculateBillingFilterItem> billingFilterListChangeHandler;

	private CalculateBillingItem()
    {
		billingFilterListChangeHandler = new ListChangeHandler<CalculateBillingFilterItem>();
    }

	public void setItems(List<CalculateBillingFilterItem> billingElementFilterItems) {
		this.billingFilterItems = billingElementFilterItems;
		billingFilterListChangeHandler.setOriginalList(billingElementFilterItems);
	}
	
	@PostLoad
	public void initListChangeHandler() {
		billingFilterItems.size();
		this.billingFilterListChangeHandler.setOriginalList(billingFilterItems);	
	}
	
	public Integer getDriverDecimalNumber() {
		if(driverDecimalNumber == null) {
			driverDecimalNumber = 2;
		}
		return driverDecimalNumber;
	}

	public Integer getUnitCostDecimalNumber() {
		if(unitCostDecimalNumber == null) {
			unitCostDecimalNumber = 2;
		}
		return unitCostDecimalNumber;
	}

	public Integer getBillingAmountDecimalNumber() {
		if(billingAmountDecimalNumber == null) {
			billingAmountDecimalNumber = 2;
		}
		return billingAmountDecimalNumber;
	}
	
	public boolean matchs(Map<Long, String> parameters) {
		boolean isOk = true;
		for(CalculateBillingFilterItem item : getBillingFilterListChangeHandler().getItems()) {
			if(StringUtils.hasText(item.getValue())) {
				if(!parameters.containsKey(item.getGroupId())) {
					return false;
				}
				String val = parameters.get(item.getGroupId());
				if(val != item.getValue() && !val.equals(item.getValue())) {
					return false;
				}
			}
			else {
				continue;
			}			
		}
		if(isOk) {
			return isOk;
		}
		return getBillingFilterListChangeHandler().getItems().size() == 0 ? true : false;
	}
	
	@Override
	public CalculateBillingItem copy() {
		CalculateBillingItem copy = new CalculateBillingItem();
		copy.setType(getType());
		copy.setDriverFunction(getDriverFunction());
		copy.setUnitCostFunction(getUnitCostFunction());
		copy.setBillingAmountFunction(getBillingAmountFunction());
		for(CalculateBillingFilterItem item : getBillingFilterListChangeHandler().getItems()) {
			copy.getBillingFilterListChangeHandler().addNew(item);
		}
		return copy;
	}

}

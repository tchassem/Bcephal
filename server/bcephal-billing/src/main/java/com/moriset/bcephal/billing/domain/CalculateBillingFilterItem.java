package com.moriset.bcephal.billing.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "CalculateBillingFilterItem")
@Table(name = "BCP_CALCULATE_BILLING_FILTER_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class CalculateBillingFilterItem extends Persistent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5232167882948364164L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calc_bill_filter_item__seq")
	@SequenceGenerator(name = "calc_bill_filter_item__seq", sequenceName = "calc_bill_filter_item__seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@jakarta.persistence.JoinColumn(name = "calculateEltId")
	public CalculateBillingItem calculateEltId;
	
	public Long groupId;

    public int position;

    public String value;

    public CalculateBillingFilterItem()
    {
		super();
    }

	@Override
	public Persistent copy() {
		return null;
	}

}

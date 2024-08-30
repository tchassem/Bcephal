package com.moriset.bcephal.billing.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "BillingDescription")
@Table(name = "BCP_BILLING_DESCRIPTION")
@Data
@EqualsAndHashCode(callSuper = false)
public class BillingDescription extends Persistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2873730328138054271L;
	private String locale;
	private String description;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_description_seq")
	@SequenceGenerator(name = "billing_description_seq", sequenceName = "billing_description_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing")
	public BillingModel billing;
	
	@Override
	public BillingDescription copy() {
		BillingDescription copy = new BillingDescription();
		copy.setLocale(getLocale());
		copy.setDescription(getDescription());
		return copy;
	}			 
    
}

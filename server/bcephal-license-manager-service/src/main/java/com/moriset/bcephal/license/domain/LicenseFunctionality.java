package com.moriset.bcephal.license.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

@Entity(name = "LicenseFunctionality")
@Table(name = "BCP_LICENSE_FUNCTIONALITY")
@Data
@EqualsAndHashCode(callSuper = false)
public class LicenseFunctionality  extends Persistent {

	private static final long serialVersionUID = -3450600570501934100L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "license_functionality_seq")
	@SequenceGenerator(name = "license_functionality_seq", sequenceName = "license_functionality_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "license")
	private License license;
	
	private String code;
	
	private String description;
	
	private boolean active;
	
	
	public LicenseFunctionality() {
		active = true;
	}
	
	public LicenseFunctionality(ProductFunctionality product) {
		this();
		setCode(product.getCode());
		setActive(product.isActive());
		setDescription(product.getDescription());
	}
	
	
	@Override
	public String toString() {
		return code;		
	}
	
	@Override
	public LicenseFunctionality copy() {
		LicenseFunctionality copy = new LicenseFunctionality();
		copy.setCode(code);
		copy.setActive(active);
		copy.setDescription(description);
		return null;
	}
	
	
}

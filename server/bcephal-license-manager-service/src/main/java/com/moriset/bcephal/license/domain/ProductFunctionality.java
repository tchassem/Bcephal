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

@Entity(name = "ProductFunctionality")
@Table(name = "BCP_PRODUCT_FUNCTIONALITY")
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductFunctionality extends Persistent {

	private static final long serialVersionUID = 4290982524201187365L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_functionality_seq")
	@SequenceGenerator(name = "product_functionality_seq", sequenceName = "product_functionality_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product")
	private Product product;
	
	private String code;
	
	private String description;
	
	private boolean active;
	
	
	public ProductFunctionality() {
		active = true;
	}
	
	
	@Override
	public String toString() {
		return code;		
	}
	
	@Override
	public ProductFunctionality copy() {
		ProductFunctionality copy = new ProductFunctionality();
		copy.setCode(code);
		copy.setActive(active);
		copy.setDescription(description);
		return null;
	}
	
	
}

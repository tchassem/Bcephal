package com.moriset.bcephal.license.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity(name = "ProductParameter")
@Table(name = "BCP_PRODUCT_PARAMETER")
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductParameter extends Persistent {

	private static final long serialVersionUID = -2380750914630568481L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_parameter_seq")
	@SequenceGenerator(name = "product_parameter_seq", sequenceName = "product_parameter_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product")
	private Product product;
	
	private String code;
	
	private String description;
	
	private boolean mandatory;
	
	@Enumerated(EnumType.STRING) 
	private ParameterType type = ParameterType.STRING;
	
	
	@Override
	public String toString() {
		return code;		
	}

	@Override
	public ProductParameter copy() {
		ProductParameter copy = new ProductParameter();
		copy.setCode(code);
		copy.setMandatory(mandatory);
		copy.setType(type);
		copy.setDescription(description);
		return copy;
	}
	
}

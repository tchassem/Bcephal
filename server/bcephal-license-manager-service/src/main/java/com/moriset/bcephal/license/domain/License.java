package com.moriset.bcephal.license.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "License")
@Table(name = "BCP_LICENSE")
@Data
@EqualsAndHashCode(callSuper = false)
public class License extends MainObject {

	private static final long serialVersionUID = -985237845410723398L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "license_seq")
	@SequenceGenerator(name = "license_seq", sequenceName = "license_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product")
	private Product product;
	
	@Transient
	private Long productId;
	
	private String code;
		
	@Embedded
	private Keys keys;
	
	@Enumerated(EnumType.STRING) 
	private LicenseValidityType validityType;
	
	private int days;
	
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "license")
	private List<LicenseParameter> parameters;
	@Transient 
	private ListChangeHandler<LicenseParameter> parameterListChangeHandler;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "license")
	private List<LicenseFunctionality> functionalities;
	@Transient 
	private ListChangeHandler<LicenseFunctionality> functionalityListChangeHandler;
		
	
	public License(){
		keys = new Keys();
		this.parameterListChangeHandler = new ListChangeHandler<>();
		this.functionalityListChangeHandler = new ListChangeHandler<>();
		validityType = LicenseValidityType.TEMPORARY;
	}
	
	public License(Product product){
		this();
		initialize(product);
	}
	
	public Long getProductId(){
		if(productId == null) {
			productId = product != null ? product.getId() : null;
		}
		return productId;
	}
	
	public String getProductName(){
		return product != null ? product.getName() : null;
	}
	
	public void setParameters(List<LicenseParameter> parameters) {
		this.parameters = parameters;
		this.parameterListChangeHandler.setOriginalList(parameters);
	}
	
	public void setFunctionalities(List<LicenseFunctionality> functionalities) {
		this.functionalities = functionalities;
		this.functionalityListChangeHandler.setOriginalList(functionalities);
	}
	
	@JsonIgnore
	public boolean IsDefinitive() {
		return validityType != null && validityType.IsDefinitive();
	}
	
	@JsonIgnore
	public boolean IsTemporary() {
		return validityType == null || validityType.IsTemporary();
	}
	
	
	@PostLoad
	public void initListChangeHandler() {
		parameters.size();
		functionalities.size();
		this.functionalityListChangeHandler.setOriginalList(functionalities);
		this.parameterListChangeHandler.setOriginalList(parameters);
		productId = product != null ? product.getId() : null;
	}
	
	public void initialize(Product product) {
		setProduct(product);
		if(product != null) {
			this.keys.setPrivateKey(product.getKeys() != null ? product.getKeys().getPrivateKey() : null);
			this.keys.setPublicKey(product.getKeys() != null ? product.getKeys().getPublicKey() : null);
			for(ProductFunctionality functionality : product.getFunctionalityListChangeHandler().getItems()) {
				if(functionality.isActive()) {
					getFunctionalityListChangeHandler().addNew(new LicenseFunctionality(functionality));
				}
			}
			for(ProductParameter parameter : product.getParameterListChangeHandler().getItems()) {
				getParameterListChangeHandler().addNew(new LicenseParameter(parameter));
			}
		}
		productId = product != null ? product.getId() : null;
	}
	
	@Override
	public String toString() {
		return getName();		
	}
	
	@Override
	public License copy() {
		License license = new License();
		license.setName(getName());
		license.setDescription(getDescription());
		license.setCode(code);
		license.setKeys(keys.copy());
		license.setValidityType(validityType);
		license.setDays(days);
		for(LicenseFunctionality functionality : this.functionalityListChangeHandler.getItems()) {
			license.functionalityListChangeHandler.addNew(functionality.copy());
		}
		for(LicenseParameter parameter : this.parameterListChangeHandler.getItems()) {
			license.parameterListChangeHandler.addNew(parameter.copy());
		}		
		return license;
	}

	
	
}

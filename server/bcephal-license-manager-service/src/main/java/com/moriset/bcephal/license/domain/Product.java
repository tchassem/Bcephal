package com.moriset.bcephal.license.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "Product")
@Table(name = "BCP_PRODUCT")
@Data
@EqualsAndHashCode(callSuper = false)
public class Product extends MainObject {
	
	private static final long serialVersionUID = 1100303674422909737L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
	@SequenceGenerator(name = "product_seq", sequenceName = "product_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private String code;
	
	private String version;
	
	@Embedded
	private Keys keys;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "product")
	private List<ProductParameter> parameters;
	@Transient 
	private ListChangeHandler<ProductParameter> parameterListChangeHandler;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "product")
	private List<ProductFunctionality> functionalities;
	@Transient 
	private ListChangeHandler<ProductFunctionality> functionalityListChangeHandler;
	
	
	
	public Product(){
		keys = new Keys();
		this.parameterListChangeHandler = new ListChangeHandler<>();
		this.functionalityListChangeHandler = new ListChangeHandler<>();
	}
	
	public void setParameters(List<ProductParameter> parameters) {
		this.parameters = parameters;
		this.parameterListChangeHandler.setOriginalList(parameters);
	}
	
	public void setFunctionalities(List<ProductFunctionality> functionalities) {
		this.functionalities = functionalities;
		this.functionalityListChangeHandler.setOriginalList(functionalities);
	}
	
	
	@PostLoad
	public void initListChangeHandler() {
		parameters.size();
		functionalities.size();
		this.functionalityListChangeHandler.setOriginalList(functionalities);
		this.parameterListChangeHandler.setOriginalList(parameters);
	}
	
	@Override
	public String toString() {
		return getName();		
	}

	@Override
	public Product copy() {
		Product product = new Product();
		product.setName(getName());
		product.setDescription(getDescription());
		product.setCode(code);
		product.setVersion(version);
		product.setKeys(keys.copy());
		for(ProductFunctionality functionality : this.functionalityListChangeHandler.getItems()) {
			product.functionalityListChangeHandler.addNew(functionality.copy());
		}
		for(ProductParameter parameter : this.parameterListChangeHandler.getItems()) {
			product.parameterListChangeHandler.addNew(parameter.copy());
		}		
		return product;
	}

}

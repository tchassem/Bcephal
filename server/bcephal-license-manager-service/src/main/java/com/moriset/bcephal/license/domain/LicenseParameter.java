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
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Entity(name = "LicenseParameter")
@Table(name = "BCP_LICENSE_PARAMETER")
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
public class LicenseParameter extends Persistent {

	private static final long serialVersionUID = 4456771157486387386L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "license_parameter_seq")
	@SequenceGenerator(name = "license_parameter_seq", sequenceName = "license_parameter_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "license")
	private License license;
	
	private String code;
	
	private String description;
	
	private boolean mandatory;
	
	@Enumerated(EnumType.STRING) 
	private ParameterType type;
	
	@JsonIgnore
	private String value;
	
	@Transient
	private LicenseParameterValue parameterValue;
	
	public LicenseParameter() {
		type = ParameterType.STRING;
		parameterValue = new LicenseParameterValue();
	}
	
	public LicenseParameter(ProductParameter product) {
		this();
		setCode(product.getCode());
		setMandatory(product.isMandatory());
		setDescription(product.getDescription());
		setType(product.getType());
	}
	
	
	@PostLoad
	public void initListChangeHandler() {
		try {
			parameterValue.setValue(value, type);
		} catch (Exception e) {
			log.error("Unable to parse value", e);
		}
	}
	
	public void initValue() {
		setValue(parameterValue.getValueAsString(type));
	}
	
	@Override
	public String toString() {
		return code;		
	}

	@Override
	public LicenseParameter copy() {
		LicenseParameter copy = new LicenseParameter();
		copy.setCode(code);
		copy.setMandatory(mandatory);
		copy.setType(type);
		copy.setDescription(description);
		copy.setValue(value);
		return copy;
	}
	
}

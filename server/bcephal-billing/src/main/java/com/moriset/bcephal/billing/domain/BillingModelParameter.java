/**
 * 
 */
package com.moriset.bcephal.billing.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;

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
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "BillingModelParameter")
@Table(name = "BCP_BILLING_MODEL_PARAMETER")
@Data
@EqualsAndHashCode(callSuper = false)
public class BillingModelParameter extends Persistent {

	private static final long serialVersionUID = -3015346234693378229L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_model_parameter_seq")
	@SequenceGenerator(name = "billing_model_parameter_seq", sequenceName = "billing_model_parameter_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing")
	public BillingModel billing;
	
	private String name;
	
	private int position;
	
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;
		
	private Long dimensionId;
	
	private String dimensionName;
	
	private String functions;
	
	private boolean addBillingFilters;
	
	private boolean globalParameter;
	
	@Transient @JsonIgnore
	public int columnPosition;
	

	@Override
	public BillingModelParameter copy() {
		BillingModelParameter copy = new BillingModelParameter();
		copy.setName(this.getName());
		copy.setPosition(getPosition());
		copy.setDimensionId(getDimensionId());
		copy.setDimensionType(getDimensionType());
		copy.setDimensionName(getDimensionName());
		copy.setFunctions(getFunctions());
		copy.setAddBillingFilters(isAddBillingFilters());
		copy.setGlobalParameter(isGlobalParameter());
		return copy;
	}
	
}

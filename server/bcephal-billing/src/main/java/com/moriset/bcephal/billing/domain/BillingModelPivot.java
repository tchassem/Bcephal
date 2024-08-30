/**
 * 
 */
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

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "BillingModelPivot")
@Table(name = "BCP_BILLING_MODEL_PIVOT")
@Data
@EqualsAndHashCode(callSuper = false)
public class BillingModelPivot extends Persistent {

	private static final long serialVersionUID = -7172305017427325329L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_model_pivot_seq")
	@SequenceGenerator(name = "billing_model_pivot_seq", sequenceName = "billing_model_pivot_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing")
	public BillingModel billing;
	
	private String name;
	
	private int position;
		
	private Long attributeId;
	
	private boolean show;
	
	

	@Override
	public BillingModelPivot copy() {
		BillingModelPivot copy = new BillingModelPivot();
		copy.setName(this.getName());
		copy.setPosition(getPosition());
		copy.setAttributeId(getAttributeId());
		copy.setShow(isShow());
		return copy;
	}
	
}

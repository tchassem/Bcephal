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
@Entity(name = "BillingModelDriverGroupItem")
@Table(name = "BCP_BILLING_MODEL_DRIVER_GROUP_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class BillingModelDriverGroupItem extends Persistent {

	private static final long serialVersionUID = 7420604826819788790L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_model_driver_group_item_seq")
	@SequenceGenerator(name = "billing_model_driver_group_item_seq", sequenceName = "billing_model_driver_group_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bGroup")
	private BillingModelDriverGroup bGroup;
	
	private String value;
	
	private boolean excludeDriver;
	
	private boolean excludeUnitCost;
	
	private int position;

	@Override
	public BillingModelDriverGroupItem copy() {
		BillingModelDriverGroupItem copy = new BillingModelDriverGroupItem();
		copy.setValue(getValue());
		copy.setExcludeDriver(isExcludeDriver());
		copy.setExcludeUnitCost(isExcludeUnitCost());
		copy.setPosition(getPosition());
		return copy;
	}
	
}

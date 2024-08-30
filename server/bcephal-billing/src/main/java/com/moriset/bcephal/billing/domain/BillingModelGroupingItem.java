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
@Entity(name = "BillingModelGroupingItem")
@Table(name = "BCP_BILLING_MODEL_GROUPING_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class BillingModelGroupingItem extends Persistent {

	private static final long serialVersionUID = 6758425200440903363L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_model_grouping_item_seq")
	@SequenceGenerator(name = "billing_model_grouping_item_seq", sequenceName = "billing_model_grouping_item_seq", initialValue = 1,  allocationSize = 1)
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
	
	

	@Override
	public BillingModelGroupingItem copy() {
		BillingModelGroupingItem copy = new BillingModelGroupingItem();
		copy.setName(this.getName());
		copy.setPosition(getPosition());
		copy.setAttributeId(getAttributeId());
		return copy;
	}
	
}

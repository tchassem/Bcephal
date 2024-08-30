/**
 * 
 */
package com.moriset.bcephal.billing.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
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

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "BillingModelDriverGroup")
@Table(name = "BCP_BILLING_MODEL_DRIVER_GROUP")
@Data
@EqualsAndHashCode(callSuper = false)
public class BillingModelDriverGroup extends Persistent {

	private static final long serialVersionUID = -7583775529744417142L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_model_driver_group_seq")
	@SequenceGenerator(name = "billing_model_driver_group_seq", sequenceName = "billing_model_driver_group_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing")
	public BillingModel billing;
	
	private Long groupId;
	
	private String groupName;
	
	private int position;
		
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "bGroup")
	private List<BillingModelDriverGroupItem> items;
	@Transient 
	private ListChangeHandler<BillingModelDriverGroupItem> itemListChangeHandler;
	
	
	public BillingModelDriverGroup() {
		this.itemListChangeHandler = new ListChangeHandler<BillingModelDriverGroupItem>();		
	}

	public void setItems(List<BillingModelDriverGroupItem> items) {
		this.items = items;
		itemListChangeHandler.setOriginalList(items);
	}
	
	@PostLoad
	public void initListChangeHandler() {
		items.size();
		this.itemListChangeHandler.setOriginalList(items);
	}
	

	@JsonIgnore
	public BillingModelDriverGroupItem getItemByValue(String value) {
		if(value != null) {
			for(BillingModelDriverGroupItem item : getItemListChangeHandler().getItems()) {
				if(value.equals(item.getValue())) {
					return item;
				}
			}
		}
		return null;
	}

	@Override
	public BillingModelDriverGroup copy() {
		BillingModelDriverGroup copy = new BillingModelDriverGroup();
		copy.setGroupId(getGroupId());
		copy.setGroupName(getGroupName());
		copy.setPosition(getPosition());
		for(BillingModelDriverGroupItem item : getItemListChangeHandler().getItems()) {
			copy.getItemListChangeHandler().addNew(item.copy());
		}
		return copy;
	}
	
}

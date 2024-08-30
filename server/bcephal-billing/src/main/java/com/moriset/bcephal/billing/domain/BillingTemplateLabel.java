/**
 * 
 */
package com.moriset.bcephal.billing.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.filters.UniverseFilter;

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
@Entity(name = "BillingTemplateLabel")
@Table(name = "BCP_BILLING_MODEL_LABEL")
@Data
@EqualsAndHashCode(callSuper = false)
public class BillingTemplateLabel extends Persistent {

	private static final long serialVersionUID = 2012803935987382558L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_model_label_seq")
	@SequenceGenerator(name = "billing_model_label_seq", sequenceName = "billing_model_label_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing")
	private BillTemplate billing;
    
    private String code;
    
    @ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name="filter")
    private UniverseFilter filter; 
    
    private int position;
    
    @JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "label")
	private List<BillingTemplateLabelValue> values;
	@Transient 
	private ListChangeHandler<BillingTemplateLabelValue> valueListChangeHandler;

	public BillingTemplateLabel() {
		this.valueListChangeHandler = new ListChangeHandler<>();
	}

	public void setValues(List<BillingTemplateLabelValue> values) {
		this.values = values;
		valueListChangeHandler.setOriginalList(values);
	}
	
	@PostLoad
	public void initListChangeHandler() {
		values.size();
		this.valueListChangeHandler.setOriginalList(values);
	}
        
	@Override
	public BillingTemplateLabel copy() {
		BillingTemplateLabel copy = new BillingTemplateLabel();
		copy.setFilter(getFilter() != null ? getFilter().copy() : null);
		copy.setCode(getCode());
		copy.setPosition(getPosition());
		for(BillingTemplateLabelValue item : getValueListChangeHandler().getItems()) {
			copy.getValueListChangeHandler().addNew(item.copy());
		}		
		return copy;
	}
	
}

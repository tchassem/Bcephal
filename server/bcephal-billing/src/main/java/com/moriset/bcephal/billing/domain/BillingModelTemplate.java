/**
 * 
 */
package com.moriset.bcephal.billing.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.filters.UniverseFilter;

import jakarta.persistence.Entity;
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
 * @author Moriset teams
 *
 */
@Entity(name = "BillingModelTemplate")
@Table(name = "BCP_BILLING_MODEL_TEMPLATE")
@Data
@EqualsAndHashCode(callSuper = false)
public class BillingModelTemplate extends Persistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2970025420400332737L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_model_template_seq")
	@SequenceGenerator(name = "billing_model_template_seq", sequenceName = "billing_model_template_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "modelId")
	public BillingModel modelId;
	
	private Long templateId;
    
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne @JoinColumn(name = "filter")
	private UniverseFilter filter;
    
    private int position;
    
    @Transient @JsonIgnore
    private String code;

	@Override
	public BillingModelTemplate copy() {
		BillingModelTemplate copy = new BillingModelTemplate();
		copy.setTemplateId(getTemplateId());
		copy.setFilter(filter != null ? getFilter().copy() : null);
		copy.setPosition(getPosition());
		return copy;
	}

}

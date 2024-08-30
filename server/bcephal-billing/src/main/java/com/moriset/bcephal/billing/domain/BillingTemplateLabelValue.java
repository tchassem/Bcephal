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
@Entity(name = "BillingTemplateLabelValue")
@Table(name = "BCP_BILLING_MODEL_LABEL_VALUE")
@Data
@EqualsAndHashCode(callSuper = false)
public class BillingTemplateLabelValue extends Persistent {
	
	private static final long serialVersionUID = -6203551120465444563L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_model_label_value_seq")
	@SequenceGenerator(name = "billing_model_label_value_seq", sequenceName = "billing_model_label_value_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "label")
	private BillingTemplateLabel label;
    
    private String locale;
    
    private String value;
    
    private int position;

        
	@Override
	public BillingTemplateLabelValue copy() {
		BillingTemplateLabelValue copy = new BillingTemplateLabelValue();
		copy.setLocale(getLocale());
		copy.setValue(getValue());
		copy.setPosition(getPosition());
		return copy;
	}
	
}

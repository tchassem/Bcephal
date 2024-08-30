package com.moriset.bcephal.billing.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.MainObject;

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

@Entity(name = "BillTemplate")
@Table(name = "BCP_BILL_TEMPLATE")
@Data
@EqualsAndHashCode(callSuper = false)
public class BillTemplate extends MainObject {

	private static final long serialVersionUID = 7410130116966834915L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bill_template_seq")
	@SequenceGenerator(name = "bill_template_seq", sequenceName = "bill_template_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private String code;
	
	private String repository;
	
	private String mainFile;
		
	private boolean systemTemplate;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "billing")
	private List<BillingTemplateLabel> labels;
	@Transient 
	private ListChangeHandler<BillingTemplateLabel> labelListChangeHandler;
	
	public BillTemplate() {
		this.labelListChangeHandler = new ListChangeHandler<BillingTemplateLabel>();
	}
	
	public void setLabels(List<BillingTemplateLabel> labels) {
		this.labels = labels;
		labelListChangeHandler.setOriginalList(labels);
	}
	
	@PostLoad
	public void initListChangeHandler() {
		labels.size();
		this.labelListChangeHandler.setOriginalList(labels);
	}

	@Override
	public BillTemplate copy() {
		BillTemplate copy = new BillTemplate();
		copy.setName(this.getName() + System.currentTimeMillis());
		copy.setGroup(this.getGroup());
		copy.setVisibleInShortcut(isVisibleInShortcut());
		copy.setDescription(getDescription());
		copy.setCode(getCode());
		copy.setRepository(getRepository());
		copy.setMainFile(getMainFile());
		copy.setSystemTemplate(isSystemTemplate());
		for(BillingTemplateLabel item : getLabelListChangeHandler().getItems()) {
			copy.getLabelListChangeHandler().addNew(item.copy());
		}		
		return copy;
		
	}
	
}

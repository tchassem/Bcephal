package com.moriset.bcephal.grid.domain.form;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.grid.domain.JoinGridType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.ToString;

@Entity(name =  "FormModelFieldReference")
@Table(name = "BCP_FORM_MODEL_FIELD_REFERENCE")
@Data
@EqualsAndHashCode(callSuper=false)
public class FormModelFieldReference extends Persistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3566364051614632229L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "form_model_filed_reference_seq")
	@SequenceGenerator(name = "form_model_filed_reference_seq", sequenceName = "form_model_filed_reference_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	@Enumerated(EnumType.STRING) 
	private JoinGridType dataSourceType;
	
	private Long dataSourceId;	
	
	private Long sourceId;
	
	private Long dimensionId;
	
	private Boolean uniqueValue;
	
	private String formula;
		
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "reference")
	private List<FormModelFieldReferenceCondition> conditions;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient
	private ListChangeHandler<FormModelFieldReferenceCondition> conditionListChangeHandler;
	
	public FormModelFieldReference() {
		super();
		this.conditions = new ArrayList<>(0);
		this.conditionListChangeHandler = new ListChangeHandler<FormModelFieldReferenceCondition>();
		this.dataSourceType = JoinGridType.MATERIALIZED_GRID;
		this.uniqueValue = true;
    }
	
	public boolean isUniqueValue() {
		return getUniqueValue();
	}
	
	public boolean getUniqueValue() {
		if(uniqueValue == null) {
			uniqueValue = true;
		}
		return uniqueValue;
	}

	@Override
	public Persistent copy() {
		FormModelFieldReference p = new FormModelFieldReference();
		p.setDataSourceId(getDataSourceId());
		p.setDataSourceType(getDataSourceType());		
		p.setSourceId(dataSourceId);
		p.setUniqueValue(uniqueValue);
		p.setDimensionId(dimensionId);
		p.setFormula(formula);
		if(this.conditionListChangeHandler != null && this.conditionListChangeHandler.getItems().size() > 0) {
			this.conditionListChangeHandler.getItems().forEach(item ->{
				p.conditionListChangeHandler.addNew((FormModelFieldReferenceCondition) item.copy());
			});
		}
		return p;
	}
	
	@PostLoad
	public void initListChangeHandler() {
		this.conditions.size();
		this.conditionListChangeHandler.setOriginalList(conditions);
	}
	

}

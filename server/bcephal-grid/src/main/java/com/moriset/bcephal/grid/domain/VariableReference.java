package com.moriset.bcephal.grid.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;

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

@Entity(name =  "VariableReference")
@Table(name = "BCP_VARIABLE_REFERENCE")
@Data
@EqualsAndHashCode(callSuper=false)
public class VariableReference extends Persistent {

	private static final long serialVersionUID = -3553015427821641574L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "var_reference_seq")
	@SequenceGenerator(name = "var_reference_seq", sequenceName = "var_reference_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@Enumerated(EnumType.STRING) 
	private JoinGridType dataSourceType;
	
	private Long dataSourceId;	
	
	private Long sourceId;
	
	private Long dimensionId;
	
	private boolean uniqueValue;
	
	private String formula;
		
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "reference")
	private List<VariableReferenceCondition> conditions;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient
	private ListChangeHandler<VariableReferenceCondition> conditionListChangeHandler;
	
	public VariableReference() {
		super();
		this.conditions = new ArrayList<>(0);
		this.conditionListChangeHandler = new ListChangeHandler<>();
		this.dataSourceType = JoinGridType.MATERIALIZED_GRID;
		this.uniqueValue = true;
    }
	
	public void setConditions(List<VariableReferenceCondition> conditions) {
		this.conditions = conditions;
		this.conditionListChangeHandler.setOriginalList(conditions);
	}
	
	@Override
	public VariableReference copy() {
		VariableReference p = new VariableReference();
		p.setDataSourceId(getDataSourceId());
		p.setDataSourceType(getDataSourceType());		
		p.setSourceId(dataSourceId);
		p.setUniqueValue(uniqueValue);
		p.setDimensionId(dimensionId);
		p.setFormula(formula);
		if(this.conditionListChangeHandler != null && this.conditionListChangeHandler.getItems().size() > 0) {
			this.conditionListChangeHandler.getItems().forEach(item ->{
				p.conditionListChangeHandler.addNew(item.copy());
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

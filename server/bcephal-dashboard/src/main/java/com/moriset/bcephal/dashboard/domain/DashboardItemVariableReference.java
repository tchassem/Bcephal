package com.moriset.bcephal.dashboard.domain;

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

@Entity(name =  "DashboardItemVariableReference")
@Table(name = "DashboardItemVariableReference")
@Data
@EqualsAndHashCode(callSuper=false)
public class DashboardItemVariableReference extends Persistent {

	private static final long serialVersionUID = -3094482174304738357L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dashboard_item_var_reference_seq")
	@SequenceGenerator(name = "dashboard_item_var_reference_seq", sequenceName = "dashboard_item_var_reference_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "variable")
	private DashboardItemVariable variable;
	
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
	private List<DashboardItemVariableReferenceCondition> conditions;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient
	private ListChangeHandler<DashboardItemVariableReferenceCondition> conditionListChangeHandler;
	
	public DashboardItemVariableReference() {
		super();
		this.conditions = new ArrayList<>(0);
		this.conditionListChangeHandler = new ListChangeHandler<DashboardItemVariableReferenceCondition>();
		this.dataSourceType = JoinGridType.MATERIALIZED_GRID;
		this.uniqueValue = true;
    }
	
	@Override
	public DashboardItemVariableReference copy() {
		DashboardItemVariableReference p = new DashboardItemVariableReference();
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

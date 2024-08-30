package com.moriset.bcephal.grid.domain.form;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
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

@Entity(name =  "FormModelSpot")
@Table(name = "BCP_FORM_MODEL_SPOT")
@Data
@EqualsAndHashCode(callSuper=false)
public class FormModelSpot extends Persistent {

	private static final long serialVersionUID = 2087482960209880193L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "form_model_spot_seq")
	@SequenceGenerator(name = "form_model_spot_seq", sequenceName = "form_model_spot_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	@Enumerated(EnumType.STRING) 
	private DataSourceType dataSourceType;
	
	private Long dataSourceId;	
		
	private Long measureId;
		
	private String formula;
		
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "spot")
	private List<FormModelSpotItem> items;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@Transient
	private ListChangeHandler<FormModelSpotItem> itemsListChangeHandler;
	
	public FormModelSpot() {
		super();
		this.items = new ArrayList<>(0);
		this.itemsListChangeHandler = new ListChangeHandler<>();
		this.dataSourceType = DataSourceType.INPUT_GRID;
    }
	

	@PostLoad
	public void initListChangeHandler() {
		this.items.size();
		this.itemsListChangeHandler.setOriginalList(items);
	}
	
	@Override
	public FormModelSpot copy() {
		FormModelSpot p = new FormModelSpot();
		p.setDataSourceId(getDataSourceId());
		p.setDataSourceType(getDataSourceType());		
		p.setMeasureId(getMeasureId());
		p.setFormula(formula);
		if(this.itemsListChangeHandler != null && this.itemsListChangeHandler.getItems().size() > 0) {
			this.itemsListChangeHandler.getItems().forEach(item ->{
				p.itemsListChangeHandler.addNew((FormModelSpotItem) item.copy());
			});
		}
		return p;
	}
	
	
}

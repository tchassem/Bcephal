package com.moriset.bcephal.grid.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "SmartMaterializedGrid")
@Table(name = "BCP_MATERIALIZED_GRID")
@Data
@EqualsAndHashCode(callSuper = false)
public class SmartMaterializedGrid extends AbstractSmartGrid<SmartMaterializedGridColumn> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mat_grid_seq")
	@SequenceGenerator(name = "mat_grid_seq", sequenceName = "mat_grid_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "grid")
	private List<SmartMaterializedGridColumn> columns;  
	
	public SmartMaterializedGrid() {
		setGridType(JoinGridType.MATERIALIZED_GRID);
		this.columns = new ArrayList<>(); 
	}
	
	@Override
	public String getDbTableName() {
		return new MaterializedGrid(id).getMaterializationTableName();
	}
}

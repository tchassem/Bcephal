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

@Entity(name = "SmartUnionGrid")
@Table(name = "BCP_UNION_GRID")
@Data
@EqualsAndHashCode(callSuper = false)
public class SmartUnionGrid extends AbstractSmartGrid<SmartUnionGridColumn>{

	private static final long serialVersionUID = 6864419961299290336L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "union_grid_seq")
	@SequenceGenerator(name = "union_grid_seq", sequenceName = "union_grid_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "unionGrid")
	private List<SmartUnionGridColumn> columns;
	
	public SmartUnionGrid() {
		setGridType(JoinGridType.UNION_GRID);
		this.columns = new ArrayList<>(); 
	}
	
	@Override
	public String getDbTableName() {
		return new Join(id).getMaterializationTableName();
	}
	
}

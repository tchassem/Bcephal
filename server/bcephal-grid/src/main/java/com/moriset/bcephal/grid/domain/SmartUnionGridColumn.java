package com.moriset.bcephal.grid.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "SmartUnionGridColumn")
@Table(name = "BCP_UNION_GRID_COLUMN")
@Data 
@EqualsAndHashCode(callSuper = false)
public class SmartUnionGridColumn extends AbstractSmartGridColumn {

	private static final long serialVersionUID = 2150148958985372864L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "union_grid_column_seq")
	@SequenceGenerator(name = "union_grid_column_seq", sequenceName = "union_grid_column_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@jakarta.persistence.JoinColumn(name = "unionGrid")
	private SmartUnionGrid unionGrid;
	
	private Long dimensionId;
	
//	private String dimensionName;
//	
//	private String dimensionFunction;
	
	public SmartUnionGridColumn() {
		setGridType(JoinGridType.UNION_GRID);
	}

	@Override
	public Long getParentId() {
		return unionGrid.getId();
	}
	
	@JsonIgnore
	public String getDbColumnName() {
		return new UnionGridColumn(getId()).getDbColAliasName();
	}
	
	@Override
	public String getGridName() {
		return unionGrid != null ? unionGrid.getName() : null;
	}
	
}

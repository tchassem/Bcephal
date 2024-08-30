package com.moriset.bcephal.grid.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

@Entity(name = "SmartMaterializedGridColumn")
@Table(name = "BCP_MATERIALIZED_GRID_COLUMN")
@Data 
@EqualsAndHashCode(callSuper = false)
public class SmartMaterializedGridColumn extends AbstractSmartGridColumn {

	private static final long serialVersionUID = -9150866076458659548L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mat_grid_column_seq")
	@SequenceGenerator(name = "mat_grid_column_seq", sequenceName = "mat_grid_column_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	
		
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grid")
	private SmartMaterializedGrid grid;
		
	public SmartMaterializedGridColumn() {
		setGridType(JoinGridType.MATERIALIZED_GRID);
	}
	
	public SmartMaterializedGridColumn(Long id) {
		this();
		setId(id);
	}
	
	@Override
	public Long getParentId() {
		return grid.getId();
	}
	
	@JsonIgnore
	public String getDbColumnName() {
		return "column" + getId();
	}
		
	@Override
	public String getGridName() {
		return grid != null ? grid.getName() : null;
	}
	
}

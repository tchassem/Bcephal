/**
 * 
 */
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

/**
 * @author B-Cephal Team
 *
 *         29 mars 2021
 */
@Entity(name = "SmartGrilleColumn")
@Table(name = "BCP_GRID_COLUMN")
@Data 
@EqualsAndHashCode(callSuper = false)
public class SmartGrilleColumn extends AbstractSmartGridColumn {
	
	private static final long serialVersionUID = 2404853929559393596L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "grid_column_seq")
	@SequenceGenerator(name = "grid_column_seq", sequenceName = "grid_column_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grid")
	private SmartGrille grid;
	
	private String dimensionName;
	
	private String dimensionFunction;
	
	private String dimensionFormat;
	
	public SmartGrilleColumn() {
		setGridType(JoinGridType.GRID);
	}
	
	@Override
	public Long getParentId() {
		return grid.getId();
	}
	
	@JsonIgnore
	public String getDbColumnName() {
		return new GrilleColumn(id, getName(), getType(), getDimensionId(), dimensionName).getUniverseTableColumnName();
	}
	
	@Override
	public String getGridName() {
		return grid != null ? grid.getName() : null;
	}
		
}

/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "GrilleDimension")
@Table(name = "BCP_GRID_DIMENSION")
@Data
@EqualsAndHashCode(callSuper = false)
public class GrilleDimension extends Persistent {

	private static final long serialVersionUID = -5777051383417136427L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "grid_dimension_seq")
	@SequenceGenerator(name = "grid_dimension_seq", sequenceName = "grid_dimension_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grid")
	private Grille grid;
	
	@Enumerated(EnumType.STRING)
	private DimensionType type;
	
	private Long dimensionId;

	private String name;
	
	private int position;
	

	@Override
	public GrilleDimension copy() {
		GrilleDimension copy = new GrilleDimension();
		copy.setName(name);
		copy.setType(type);
		copy.setPosition(position);
		return copy;
	}
	
}

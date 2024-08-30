package com.moriset.bcephal.grid.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "UnionGridItem")
@Table(name = "BCP_UNION_GRID_ITEM")
@Data 
@EqualsAndHashCode(callSuper = false)
public class UnionGridItem extends Persistent {

	private static final long serialVersionUID = 3114410501416277411L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "union_grid_item_seq")
	@SequenceGenerator(name = "union_grid_item_seq", sequenceName = "union_grid_item_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unionGrid")
	private UnionGrid unionGrid;
	
	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gridid")
	private SmartMaterializedGrid grid;
	
	private int position;
	
	@Override
	public UnionGridItem copy() {
		UnionGridItem copy = new UnionGridItem();
		copy.setPosition(position);
		copy.setGrid(grid);
		return copy;
	}
	
	@PostLoad
	public void initListChangeHandler() {
		grid.getName();
	}

	public SmartMaterializedGridColumn getGridColumn(Long colId) {
		UnionGridColumn column = getUnionGrid().getColumnById(colId);
		return column != null && getGrid() != null ? column.getColumnByGridId(getGrid().getId()) : null;
	}
	
}

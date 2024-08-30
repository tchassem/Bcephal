/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

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
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "JoinGrid")
@Table(name = "BCP_JOIN_GRID")
@Data 
@EqualsAndHashCode(callSuper = false)
public class JoinGrid extends Persistent {
	
	private static final long serialVersionUID = 2685080338377519639L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "join_grid_seq")
	@SequenceGenerator(name = "join_grid_seq", sequenceName = "join_grid_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "joinId")
	private Join joinId;
	
	@Enumerated(EnumType.STRING)
	private JoinGridType gridType;
	
	private Long gridId;

	private String name;

	private int position;
		
	private Boolean mainGrid;
	
	public JoinGrid() {
		gridType = JoinGridType.JOIN;
		mainGrid = false;
	}
	
	public boolean isMainGrid() {
		if(mainGrid == null) {
			mainGrid = false;
		}
		return mainGrid;
	}
	
	public boolean getMainGrid() {
		if(mainGrid == null) {
			mainGrid = false;
		}
		return mainGrid;
	}
	
	@JsonIgnore
	public String getDbGridVarName() {
		String name = isMaterializedGrid() ? "mat_" : isJoin() ? "join_" : "grid_";	
		return name + getGridId();
	}
	
	@JsonIgnore
	public String getPublicationTableName(boolean withGridVarName) {
		String varCol = withGridVarName ? getDbGridVarName() : "";
		String name = isMaterializedGrid() ? new MaterializedGrid(getGridId()).getMaterializationTableName()
				: isJoin() ? new Join(getGridId()).getMaterializationTableName()
						: new Grille(getGridId()).getPublicationTableName();		
		return name + " " + varCol;
	}
	
	
	@JsonIgnore
	public boolean isMaterializedGrid() {
		return gridType == JoinGridType.MATERIALIZED_GRID;
	}
	
	@JsonIgnore
	public boolean isJoin() {
		return gridType == JoinGridType.JOIN;
	}
	
	@JsonIgnore
	public boolean isGrid() {
		return gridType == JoinGridType.GRID || gridType == JoinGridType.REPORT_GRID;
	}
	
	
	@Override
	public JoinGrid copy() {
		JoinGrid copy = new JoinGrid();
		copy.setName(getName());
		copy.setGridId(getGridId());
		copy.setGridType(getGridType());
		copy.setPosition(getPosition());
		copy.setMainGrid(getMainGrid());
		copy.setJoinId(getJoinId());
		return copy;
	}
}

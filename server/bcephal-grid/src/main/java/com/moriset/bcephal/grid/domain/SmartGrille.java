/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.universe.UniverseParameters;

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
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "SmartGrille")
@Table(name = "BCP_GRID")
@Data
@EqualsAndHashCode(callSuper = false)
public class SmartGrille extends AbstractSmartGrid<SmartGrilleColumn> {

	private static final long serialVersionUID = 2003294484734214801L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "grid_seq")
	@SequenceGenerator(name = "grid_seq", sequenceName = "grid_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
		
	@Enumerated(EnumType.STRING) 
	private GrilleType type;
	
	@Enumerated(EnumType.STRING)
	private DataSourceType dataSourceType;
			
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "grid")
	private List<SmartGrilleColumn> columns;

	
	public SmartGrille() {
		setGridType(JoinGridType.GRID);
		this.columns = new ArrayList<>(); 
	}
	
	@PostLoad
	@Override
	public void initListChangeHandler() {
		super.initListChangeHandler();
		setGridType(GrilleType.INPUT == type ? JoinGridType.GRID : JoinGridType.REPORT_GRID);
	}
	
	@Override
	public List<SmartGrilleColumn> getColumns(){
		return this.columns;
	}
	
	@JsonIgnore
	public boolean isReport() {
		return getType() != null && getType() == GrilleType.REPORT;
	}
	
	@JsonIgnore
	public boolean isDataSourceMaterialized() {
		return getDataSourceType() == DataSourceType.MATERIALIZED_GRID;
	}

	@Override
	public String getDbTableName() {
		return UniverseParameters.UNIVERSE_TABLE_NAME;
	}
}

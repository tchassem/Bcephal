package com.moriset.bcephal.grid.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "JoinKey")
@Table(name = "BCP_JOIN_KEY")
@Data 
@EqualsAndHashCode(callSuper = false)
public class JoinKey extends Persistent {
	
	private static final long serialVersionUID = -6426276527052691018L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "join_key_seq")
	@SequenceGenerator(name = "join_key_seq", sequenceName = "join_key_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@jakarta.persistence.JoinColumn(name = "joinId")
	private Join joinId;
	
	private int position;
	
	private DimensionType dimensionType;
	
	@Enumerated(EnumType.STRING)
	private JoinGridType gridType1;
	
	private Long gridId1;
	
	private Long columnId1;
	
	private Long valueId1;
	
	@Enumerated(EnumType.STRING)
	private JoinGridType gridType2;
	
	private Long gridId2;
	
	private Long columnId2;
	
	private Long valueId2;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@Transient
	private DataSourceType dataSourceType1;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@Transient
	private DataSourceType dataSourceType2;
	
		
	
	public boolean compatibleTo(JoinGrid grid, List<JoinGrid> previousGrids) {
		boolean ok = grid.getGridId().equals(getGridId1()) || grid.getGridId().equals(getGridId2());
		if(ok) {
			List<Long> ids = new ArrayList<>();
			for(JoinGrid previousGrid : previousGrids) {
				ids.add(previousGrid.getGridId());
			}
			ok = ids.contains(getGridId1()) || ids.contains(getGridId2());
		}
		
		return ok;
		
//		return (grid.getGridId().equals(getGridId1()) || grid.getGridId().equals(getGridId2()))
//				;//&& (previousGrid == null || previousGrid.getGridId().equals(getGridId1()) || previousGrid.getGridId().equals(getGridId2()));
	}


	public String getSql(boolean withGridVarName) {
		String sql = null;
		if(getColumnId1() != null && getColumnId2() != null) {
			sql = new JoinColumn(getGridType1(), getGridId1(), getColumnId1(), getValueId1(), "", getDimensionType(), getDataSourceType1()).getDbColName(true, false, false) 
					+ " = "
					+ new JoinColumn(getGridType2(), getGridId2(), getColumnId2(), getValueId2(), "", getDimensionType(), getDataSourceType2()).getDbColName(true, false, false);
		}
		return sql;
	}
	
	
	
	@Override
	public JoinKey copy() {
		JoinKey copy = new JoinKey();
		copy.setColumnId1(getColumnId1());
		copy.setColumnId2(getColumnId2());
		copy.setDimensionType(getDimensionType());
		copy.setGridId1(getGridId1());
		copy.setGridId2(getGridId2());
		copy.setGridType1(getGridType1());
		copy.setGridType2(getGridType2());
		copy.setJoinId(getJoinId());
		copy.setPosition(getPosition());
		copy.setValueId1(getValueId1());
		copy.setValueId2(getValueId2());
		
		return copy;
	}

}

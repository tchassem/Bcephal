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

@Entity(name = "SmartJoin")
@Table(name = "BCP_JOIN")
@Data
@EqualsAndHashCode(callSuper = false)
public class SmartJoin extends AbstractSmartGrid<SmartJoinColumn>{

	private static final long serialVersionUID = 6864419961299290336L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "join_seq")
	@SequenceGenerator(name = "join_seq", sequenceName = "join_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "joinId")
	private List<SmartJoinColumn> columns;
	
	public SmartJoin() {
		setGridType(JoinGridType.JOIN);
		this.columns = new ArrayList<>(); 
	}
	
	@Override
	public String getDbTableName() {
		return new Join(id).getMaterializationTableName();
	}
	
}

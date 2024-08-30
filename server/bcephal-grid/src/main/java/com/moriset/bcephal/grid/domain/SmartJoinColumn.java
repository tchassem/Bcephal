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

@Entity(name = "SmartJoinColumn")
@Table(name = "BCP_JOIN_COLUMN")
@Data 
@EqualsAndHashCode(callSuper = false)
public class SmartJoinColumn extends AbstractSmartGridColumn {

	private static final long serialVersionUID = 2150148958985372864L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "join_column_seq")
	@SequenceGenerator(name = "join_column_seq", sequenceName = "join_column_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@jakarta.persistence.JoinColumn(name = "joinId")
	private SmartJoin joinId;
	
	private Long dimensionId;
	
	private String dimensionName;
	
	private String dimensionFunction;
	
	public SmartJoinColumn() {
		setGridType(JoinGridType.JOIN);
	}

	@Override
	public Long getParentId() {
		return joinId.getId();
	}
	
	@JsonIgnore
	public String getDbColumnName() {
		return new JoinColumn(getId()).getDbColAliasName();
	}
	
	@Override
	public String getGridName() {
		return joinId != null ? joinId.getName() : null;
	}
	
}

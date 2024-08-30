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

@Entity(name = "JoinUnionKeyItem")
@Table(name = "BCP_JOIN_UNION_KEY_ITEM")
@Data 
@EqualsAndHashCode(callSuper = false)
public class JoinUnionKeyItem extends Persistent {

	private static final long serialVersionUID = -4381796770633322277L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "join_union_key_item_seq")
	@SequenceGenerator(name = "join_union_key_item_seq", sequenceName = "join_union_key_item_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unionId")
	private JoinUnionKey unionId;
	
	private int position;
	
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;
	
	@Enumerated(EnumType.STRING)
	private JoinGridType gridType;
	
	private Long gridId;
	
	private Long columnId;
	
	@Override
	public Persistent copy() {
		JoinUnionKeyItem copy = new JoinUnionKeyItem();
		copy.setUnionId(getUnionId());
		copy.setColumnId(getColumnId());
		copy.setGridId(getGridId());
		copy.setGridType(getGridType());
		copy.setDimensionType(getDimensionType());
		copy.setPosition(getPosition());
		
		return copy;
	}
	
}

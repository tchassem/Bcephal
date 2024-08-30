package com.moriset.bcephal.grid.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "JoinUnionKey")
@Table(name = "BCP_JOIN_UNION_KEY")
@Data 
@EqualsAndHashCode(callSuper = false)
public class JoinUnionKey extends Persistent {

	private static final long serialVersionUID = 5515891281455555901L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "join_union_key_seq")
	@SequenceGenerator(name = "join_union_key_seq", sequenceName = "join_union_key_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "joinId")
	private Join joinId;
	
	private int position;
	
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;
	
	@Enumerated(EnumType.STRING)
	private JoinGridType gridType;
	
	private Long gridId;
	
	private Long columnId;
	
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "unionId")
	private List<JoinUnionKeyItem> items;

	@Transient
	private ListChangeHandler<JoinUnionKeyItem> itemListChangeHandler;	
	
	
	public JoinUnionKey(){
		this.itemListChangeHandler = new ListChangeHandler<>();
	}
	
	public JoinUnionKey(Long id) {
		this();
		this.id = id;
	}
	
	public void setItems(List<JoinUnionKeyItem> items) {
		this.items = items;
		this.itemListChangeHandler.setOriginalList(items);
	}
	
	@PostLoad
	public void initListChangeHandler() {
		this.items.forEach(x->{});
		this.itemListChangeHandler.setOriginalList(items);		
	}

	@Override
	public JoinUnionKey copy() {
		JoinUnionKey copy = new JoinUnionKey();
		copy.setColumnId(getColumnId());
		copy.setDimensionType(getDimensionType());
		copy.setGridId(getGridId());
		copy.setGridType(getGridType());
		copy.setJoinId(getJoinId());
		copy.setPosition(getPosition());
		for (JoinUnionKeyItem item : this.getItemListChangeHandler().getItems()) {
			if (item == null)
				continue;
			JoinUnionKeyItem copyField = (JoinUnionKeyItem)item.copy();
			copy.getItemListChangeHandler().addNew(copyField);
		}
		
		return copy;
	}
	
}

package com.moriset.bcephal.grid.domain;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.dimension.DimensionType;

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
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "UnionGrid")
@Table(name = "BCP_UNION_GRID")
@Data
@EqualsAndHashCode(callSuper = false)
public class UnionGrid extends MainObject {
	
	private static final long serialVersionUID = -4196645896025103124L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "union_grid_seq")
	@SequenceGenerator(name = "union_grid_seq", sequenceName = "union_grid_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	private boolean published;

	private boolean showAllRowsByDefault;

	private boolean allowLineCounting;
	
	private int visibleColumnCount;
	
	@Enumerated(EnumType.STRING) 
	private GrilleType gridType;
	
	@Enumerated(EnumType.STRING) 
	private GrilleRowType rowType;
	
	private Boolean debit;

	private Boolean credit;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "unionGrid")
	private List<UnionGridItem> items;

	@Transient
	private ListChangeHandler<UnionGridItem> itemListChangeHandler;

	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "unionGrid")
	private List<UnionGridColumn> columns;

	@Transient
	private ListChangeHandler<UnionGridColumn> columnListChangeHandler;
	
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "unionGrid")
	private List<UnionGridCondition> conditions;

	@Transient
	private ListChangeHandler<UnionGridCondition> conditionListChangeHandler;
	
	
	public UnionGrid() {
		this.allowLineCounting = false;
		this.showAllRowsByDefault = false;
		this.visibleColumnCount = 5;
		this.itemListChangeHandler = new ListChangeHandler<>();		
		this.columnListChangeHandler = new ListChangeHandler<>();	
		this.conditionListChangeHandler = new ListChangeHandler<>();	
	}
	
	public boolean isDebit() {
		if(debit == null) {
			debit = false;
		}
		return debit;
	}
	
	public boolean isCredit() {
		if(credit == null) {
			credit = false;
		}
		return credit;
	}
	
	public void setItems(List<UnionGridItem> items) {
		this.items = items;
		this.itemListChangeHandler.setOriginalList(items);
	}
	
	public void setColumns(List<UnionGridColumn> columns) {
		this.columns = columns;
		this.columnListChangeHandler.setOriginalList(columns);
	}
	
	public void setConditions(List<UnionGridCondition> conditions) {
		this.conditions = conditions;
		this.conditionListChangeHandler.setOriginalList(conditions);
	}
	
	public UnionGridColumn getColumnById(Long columnId) {
		for(UnionGridColumn column : getColumnListChangeHandler().getItems()) {
			if(column.getId() != null && column.getId().equals(columnId)) {
				return column;
			}
		}
		return null;
	}
	
	@JsonIgnore
	public List<UnionGridColumn> getSortedColumns() {
		List<UnionGridColumn> conditions = getColumnListChangeHandler().getItems();
		Collections.sort(conditions, new Comparator<UnionGridColumn>() {
			@Override
			public int compare(UnionGridColumn item1, UnionGridColumn item2) {
				return item1.getPosition() - item2.getPosition();
			}
		});
		return conditions;
	}
	
	public void sort() {
		setColumns(getSortedColumns());
	}
	
	public UnionGridColumn getColumnByDimensionAndName(DimensionType dimensionType, String name) {
		for(UnionGridColumn column : getColumnListChangeHandler().getItems()) {
			if(column.getType() == dimensionType && column.getName() != null && column.getName().equals(name)) {
				return column;
			}
		}
		return null;
	}
		
	@PostLoad
	public void initListChangeHandler() {
		items.forEach(item -> { });			
		this.itemListChangeHandler.setOriginalList(items);
		columns.forEach( item -> { });			
		this.columnListChangeHandler.setOriginalList(columns);
		conditions.forEach( item -> { });			
		this.conditionListChangeHandler.setOriginalList(conditions);
	}
	
	@Override
	public UnionGrid copy() {
		UnionGrid copy = new UnionGrid();
		copy.setName(this.getName() + System.currentTimeMillis());
		copy.setGroup(this.getGroup());
		copy.setVisibleInShortcut(isVisibleInShortcut());
		copy.setShowAllRowsByDefault(isShowAllRowsByDefault());
		copy.setAllowLineCounting(isAllowLineCounting());
		copy.setPublished(false);
		copy.setGridType(gridType);
		copy.setRowType(rowType);
		for(UnionGridItem item : getItemListChangeHandler().getItems()) {
			copy.getItemListChangeHandler().addNew(item.copy());
		}
		for(UnionGridColumn column : getColumnListChangeHandler().getItems()) {
			copy.getColumnListChangeHandler().addNew(column.copy());
		}
		for(UnionGridCondition condition : getConditionListChangeHandler().getItems()) {
			copy.getConditionListChangeHandler().addNew(condition.copy());
		}
		return copy;
	}
}

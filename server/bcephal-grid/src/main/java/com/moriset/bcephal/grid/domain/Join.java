/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.SchedulableObject;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.domain.routine.RoutineExecutor;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "Join")
@Table(name = "BCP_JOIN")
@Data
@EqualsAndHashCode(callSuper = false)
public class Join extends SchedulableObject {
	 
	private static final long serialVersionUID = -2441378205643431569L;
	private static final int DEFAULT_PUBLICATION_PAGE_SIZE = 5000;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "join_seq")
	@SequenceGenerator(name = "join_seq", sequenceName = "join_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	
	private boolean showAllRowsByDefault;
	
	private boolean allowLineCounting;
	
	private boolean consolidated;
	
	private boolean refreshGridsBeforePublication;
	
	private boolean republishGridsBeforePublication;
	
	private boolean addPublicationRunNbr;
	
	private Long publicationRunAttributeId;
	
	private Long publicationRunSequenceId;
	
	@Enumerated(EnumType.STRING) 
	private GrilleType gridType;
	
	@Enumerated(EnumType.STRING) 
	private JoinPublicationMethod publicationMethod;
	
	@Enumerated(EnumType.STRING)
	private DataSourceType publicationDataSourceType;
	
	@Transient
	private Integer publicationPageSize;
	
	private Long publicationGridId;
	
	private Long reportId;
	
	private String publicationGridName;
	
	private boolean published;
	
	private Integer visibleColumnCount;
	
	private Boolean debit;

	private Boolean credit;

	private boolean confirmAction;
	
	@Enumerated(EnumType.STRING) 
	private GrilleRowType rowType;	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne @jakarta.persistence.JoinColumn(name = "userFilter")
	private UniverseFilter filter;	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne @jakarta.persistence.JoinColumn(name = "adminFilter")
	private UniverseFilter adminFilter;
	
	@Transient @JsonIgnore
	private UniverseFilter gridUserFilter;
	@Transient @JsonIgnore
	private UniverseFilter gridAdminFilter;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "joinId")
	private List<JoinGrid> grids;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<JoinGrid> gridListChangeHandler;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "joinId")
	private List<JoinKey> keys;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<JoinKey> keyListChangeHandler;
			
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "joinId")
	private List<JoinUnionKey> unionKeys;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<JoinUnionKey> unionKeyListChangeHandler;
			
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "joinId")
	private List<JoinColumn> columns;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Transient 
	private ListChangeHandler<JoinColumn> columnListChangeHandler;
			
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "joinId")
	private List<JoinCondition> conditions;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<JoinCondition> conditionListChangeHandler;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<RoutineExecutor> routineListChangeHandler;
	
	
	public Join(){
		this.confirmAction = true;	
		this.routineListChangeHandler = new ListChangeHandler<RoutineExecutor>();
		this.gridListChangeHandler = new ListChangeHandler<JoinGrid>();
		this.keyListChangeHandler = new ListChangeHandler<JoinKey>();
		this.unionKeyListChangeHandler = new ListChangeHandler<JoinUnionKey>();
		this.conditionListChangeHandler = new ListChangeHandler<JoinCondition>();
		this.columnListChangeHandler = new ListChangeHandler<JoinColumn>();
		this.publicationMethod = JoinPublicationMethod.NEW_GRID;
		this.publicationDataSourceType = DataSourceType.MATERIALIZED_GRID;
		this.visibleColumnCount = 5;
		this.rowType = GrilleRowType.NOT_RECONCILIATED;
		this.publicationPageSize = DEFAULT_PUBLICATION_PAGE_SIZE;
	}
	
	public Join(Long id) {
		this();
		this.id = id;
	}
	
	public int getPublicationPageSize() {
		if(publicationPageSize == null) {
			publicationPageSize = DEFAULT_PUBLICATION_PAGE_SIZE;
		}
		return publicationPageSize;
	}
	
	public boolean getDebit() {
		if(debit == null) {
			debit = false;
		}
		return debit;
	}
	
	public boolean getCredit() {
		if(credit == null) {
			credit = false;
		}
		return credit;
	}
	
	public int getVisibleColumnCount(){
		if(visibleColumnCount == null) {
			visibleColumnCount = 5;
		}
		return visibleColumnCount;
	}
	
	public void setGrids(List<JoinGrid> grids) {
		this.grids = grids;
		this.gridListChangeHandler.setOriginalList(grids);
	}
	
	public void setKeys(List<JoinKey> keys) {
		this.keys = keys;
		this.keyListChangeHandler.setOriginalList(keys);
	}
	
	public void setUnionKeys(List<JoinUnionKey> unionKeys) {
		this.unionKeys = unionKeys;
		this.unionKeyListChangeHandler.setOriginalList(unionKeys);
	}
	
	public void setColumns(List<JoinColumn> columns) {
		this.columns = columns;
		this.columnListChangeHandler.setOriginalList(columns);
	}
	
	public void setConditions(List<JoinCondition> conditions) {
		this.conditions = conditions;
		this.conditionListChangeHandler.setOriginalList(conditions);
	}
	
	
	@PostLoad
	public void initListChangeHandler() {
		grids.forEach(x->{});
		keys.forEach(x->{});
		unionKeys.forEach(x->{});
		conditions.forEach(x->{});
		columns.forEach(x->{});
		this.gridListChangeHandler.setOriginalList(grids);
		this.keyListChangeHandler.setOriginalList(keys);
		this.unionKeyListChangeHandler.setOriginalList(unionKeys);
		this.conditionListChangeHandler.setOriginalList(conditions);
		this.columnListChangeHandler.setOriginalList(columns);		
	}
	
	@JsonIgnore
	public String getMaterializationTableName() {
		return "JOIN_" + getId();
	}
	
	public JoinGrid getGriByGridId(Long gridId) {
		for(JoinGrid grid : getGridListChangeHandler().getItems()) {
			if(grid.getGridId() != null && grid.getGridId().equals(gridId)) {
				return grid;
			}
		}
		return null;
	}
	
	public JoinColumn getColumnByOid(Long columnId) {
		for(JoinColumn column : getColumnListChangeHandler().getItems()) {
			if(column.getId() != null && column.getId().equals(columnId)) {
				return column;
			}
		}
		return null;
	}
	
	public JoinColumn getColumn(JoinGridType gridType, Long gridId, Long columnId) {
		for(JoinColumn column : getColumnListChangeHandler().getItems()) {
			if((column.getGridType() == gridType || (gridType == JoinGridType.REPORT_GRID && column.getGridType() == JoinGridType.GRID)) 
					&& gridId.equals(column.getGridId()) && columnId.equals(column.getColumnId())) {
				return column;
			}
		}
		return null;
	}
	
	public JoinColumn getColumn(GrilleColumn grilleColumn) {
		for(JoinColumn column : getColumnListChangeHandler().getItems()) {
			if(column.getType() == grilleColumn.getType() && column.getName().equals(grilleColumn.getName())) {
				return column;
			}
		}
		return null;
	}
	
	public JoinColumn getColumnByPlublication(GrilleColumn grilleColumn) {
		for(JoinColumn column : getColumnListChangeHandler().getItems()) {
			if(column.getType() == grilleColumn.getType() 
					&& column.getPublicationDimensionId() != null && column.isUsedForPublication()
					&& column.getPublicationDimensionId().equals(grilleColumn.getDimensionId())) {
//					&& column.getPublicationDimensionId().equals(grilleColumn.getId())) {
				return column;
			}
		}
		return null;
	}
	
	public JoinColumn getColumn(MaterializedGridColumn matGridColumn) {
		for(JoinColumn column : getColumnListChangeHandler().getItems()) {
			if(column.getType() == matGridColumn.getType() && column.getName().equals(matGridColumn.getName())) {
				return column;
			}
		}
		return null;
	}
	
	public JoinColumn getColumnByPlublication(MaterializedGridColumn matGridColumn, boolean forNewGrid) {
		for(JoinColumn column : getColumnListChangeHandler().getItems()) {
			if(forNewGrid) {
				if(column.getType() == matGridColumn.getType()
					&& column.getName().equals(matGridColumn.getName())) {
						return column;
				}
			}
			else {
				if(column.getType() == matGridColumn.getType() 
						&& column.getPublicationDimensionId() != null && column.isUsedForPublication()
						&& column.getPublicationDimensionId().equals(matGridColumn.getId())) {
					return column;
				}
			}
		}
		return null;
	}
	
	
	public JoinColumn getColumn(Long dimensionId, DimensionType type) {
		for(JoinColumn column : getColumnListChangeHandler().getItems()) {
			if(column.getType() == type && column.getDimensionId().equals(dimensionId)) {
				return column;
			}
		}
		return null;
	}
	
	public JoinColumn getColumn(Long columnId, DimensionType type, JoinGridType gridType) {
		for(JoinColumn column : getColumnListChangeHandler().getItems()) {
			if(column.getType() == type && column.getGridType() == gridType && column.getColumnId().equals(columnId)) {
				return column;
			}
		}
		return null;
	}
	
	@JsonIgnore
	public JoinGrid getMainGrid() {
		JoinGrid mainGrid = null;
		for(JoinGrid grid : getGridListChangeHandler().getItems()) {
			if(mainGrid == null && grid.getPosition() == 0) {
				mainGrid = grid;
			}
			if(grid.isMainGrid()) {
				mainGrid = grid;
				break;
			}
		}
		return mainGrid;
	}
	

	@JsonIgnore
	public List<JoinColumn> getSortedColumns() {
		List<JoinColumn> conditions = getColumnListChangeHandler().getItems();
		Collections.sort(conditions, new Comparator<JoinColumn>() {
			@Override
			public int compare(JoinColumn item1, JoinColumn item2) {
				return item1.getPosition() - item2.getPosition();
			}
		});
		return conditions;
	}

	public void sort() {
		setColumns(getSortedColumns());
	}
	
	public void sortRoutines() {
		List<RoutineExecutor> routines = getRoutineListChangeHandler().getItems();
		Collections.sort(routines, new Comparator<RoutineExecutor>() {
			@Override
			public int compare(RoutineExecutor o1, RoutineExecutor o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		getRoutineListChangeHandler().setOriginalList(routines);
	}
	
	
		

	public Join copy(HashMap<Long, JoinColumn> columns) {
		Join copy = new Join();
		copy.setName(getName());
		copy.setDescription(getDescription());
		copy.setVisibleInShortcut(isVisibleInShortcut());
		copy.setGroup(getGroup());
		copy.setShowAllRowsByDefault(isShowAllRowsByDefault());
		copy.setAllowLineCounting(isAllowLineCounting());
		copy.setConsolidated(isConsolidated());
		copy.setRefreshGridsBeforePublication(isRefreshGridsBeforePublication());
		copy.setAddPublicationRunNbr(isAddPublicationRunNbr());
		copy.setShowAllRowsByDefault(isShowAllRowsByDefault());
		copy.setPublicationRunAttributeId(getPublicationRunAttributeId());
		copy.setPublicationRunSequenceId(getPublicationRunSequenceId());
		copy.setGridType(getGridType());
		copy.setPublicationMethod(getPublicationMethod());
		copy.setPublicationDataSourceType(getPublicationDataSourceType());
		copy.setPublicationGridId(getPublicationGridId());
		copy.setReportId(getReportId());
		copy.setPublicationGridName(getPublicationGridName());
		copy.setPublished(false);
		copy.setVisibleColumnCount(getVisibleColumnCount());
		copy.setDebit(getDebit());
		copy.setCredit(getCredit());
		copy.setRowType(getRowType());
		copy.setFilter(getFilter());
		copy.setAdminFilter(getAdminFilter());
		
		for (JoinGrid item : this.getGridListChangeHandler().getItems()) {
			if (item == null) continue;
			JoinGrid copyField = item.copy();
			copy.getGridListChangeHandler().addNew(copyField);
		}
		
		for (JoinKey item : this.getKeyListChangeHandler().getItems()) {
			if (item == null) continue;
			JoinKey copyField = item.copy();
			copy.getKeyListChangeHandler().addNew(copyField);
		}
		for (JoinUnionKey item : this.getUnionKeyListChangeHandler().getItems()) {
			if (item == null) continue;
			JoinUnionKey copyField = item.copy();
			copy.getUnionKeyListChangeHandler().addNew(copyField);
		}
		for (JoinColumn item : this.getColumnListChangeHandler().getItems()) {
			if (item == null) continue;
			JoinColumn copyField = item.copy();
			copyField.setOriginalColumn(item);
			columns.put(item.getId(), copyField);
			copy.getColumnListChangeHandler().addNew(copyField);
		}
		
		for (JoinCondition item : this.getConditionListChangeHandler().getItems()) {
			if (item == null) continue;
			JoinCondition copyField = item.copy();
			copy.getConditionListChangeHandler().addNew(copyField);
		}
		for (RoutineExecutor item : this.getRoutineListChangeHandler().getItems()) {
			if (item == null) continue;
			RoutineExecutor copyField = item.copy();
			copy.getRoutineListChangeHandler().addNew(copyField);
		}
		return copy;
		
	}
	
	public void updateCustomColumns(Join copy, HashMap<Long, JoinColumn> columns) {
		for (JoinColumn item : copy.getColumns()) {
			if (item == null) continue;
			if(item.isCustom()) {
				item.updateCopy(copy, columns);
			}
		}
	}
	
	@Override
	public Persistent copy() {
		return null;
	}
	
	

}

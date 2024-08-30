/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.UniverseFilter;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
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

/**
 * @author B-Cephal Team
 *
 *         29 mars 2021
 */
@Inheritance(strategy = InheritanceType.JOINED)
@JsonSubTypes({ @Type(value = Grille.class, name = "Grille") })
@Entity(name = "Grille")
@Table(name = "BCP_GRID")
@Data
@EqualsAndHashCode(callSuper = false)
public class Grille extends MainObject {

	private static final long serialVersionUID = -4558463610223968350L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "grid_seq")
	@SequenceGenerator(name = "grid_seq", sequenceName = "grid_seq", initialValue = 1,  allocationSize = 1)
	private Long id;	
	
	@Enumerated(EnumType.STRING)
	private DataSourceType dataSourceType;
	
	private Long dataSourceId;
	
	@Transient
	private String dataSourceName;

	@Enumerated(EnumType.STRING) 
	private GrilleType type;
		
	@Enumerated(EnumType.STRING) 
	private GrilleCategory category;

	@Enumerated(EnumType.STRING) 
	private GrilleStatus status;
	
	@Enumerated(EnumType.STRING) 
	private GrilleSource sourceType;
	
	private Long sourceId;
	
	private Long reportId;
	
	private boolean editable;	
	
	private boolean showAllRowsByDefault;
	
	private boolean allowLineCounting;
	
	private boolean consolidated;
		
	//private boolean useLink;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@ManyToOne @JoinColumn(name = "userFilter")
	private UniverseFilter userFilter;	
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@ManyToOne @JoinColumn(name = "adminFilter")
	private UniverseFilter adminFilter;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@Transient @JsonIgnore
	private UniverseFilter gridUserFilter;
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@Transient @JsonIgnore
	private UniverseFilter gridAdminFilter;
	
	private boolean debit;

	private boolean credit;

	@Enumerated(EnumType.STRING) 
	private GrilleRowType rowType;
	
	private Boolean published;
	
	private Integer visibleColumnCount;
	
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "grid")
	private List<GrilleDimension> dimensions;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<GrilleDimension> dimensionListChangeHandler;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "grid")
	private List<GrilleColumn> columns;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@Transient 
	private ListChangeHandler<GrilleColumn> columnListChangeHandler;
	

	/**
	 * Default constructor
	 */
	public Grille() {
		this.columnListChangeHandler = new ListChangeHandler<GrilleColumn>();		
		this.dimensionListChangeHandler = new ListChangeHandler<GrilleDimension>();
		editable = false;
		this.category = GrilleCategory.USER;
		this.status = GrilleStatus.LOADED;
		this.sourceType = GrilleSource.USER;
		this.dataSourceType = DataSourceType.UNIVERSE;
		this.visibleColumnCount = 5;
		this.published = false;
	}
	
	public Grille(Long id) {
		this();
		this.id = id;
	}
	
	public int getVisibleColumnCount(){
		if(visibleColumnCount == null) {
			visibleColumnCount = 5;
		}
		return visibleColumnCount;
	}
	
	public boolean isUseLink() {
		return false;
	}
	
	public DataSourceType getDataSourceType(){
		if(dataSourceType == null) {
			dataSourceType = DataSourceType.UNIVERSE;
		}
		return dataSourceType;
	}
	
	public Boolean getPublished() {
		if(published == null) {
			published = false;
		}
		return published;
	}
	
	public void setDimensions(List<GrilleDimension> dimensions) {
		this.dimensions = dimensions;
		dimensionListChangeHandler.setOriginalList(dimensions);
	}

	public void setColumns(List<GrilleColumn> columns) {
		this.columns = columns;
		columnListChangeHandler.setOriginalList(columns);
	}
		
	
	@JsonIgnore
	public boolean isInput() {
		return getType() != null && getType() == GrilleType.INPUT;
	}
	
	@JsonIgnore
	public boolean isReport() {
		return getType() != null && getType() == GrilleType.REPORT;
	}
	
	@JsonIgnore
	public boolean isReconciliation() {
		return getType() != null && getType() == GrilleType.RECONCILIATION;
	}
	
	@JsonIgnore
	public boolean isBillingEventRepo() {
		return getType() != null && getType() == GrilleType.BILLING_EVENT_REPOSITORY;
	}
	
	@JsonIgnore
	public boolean isClientRepo() {
		return getType() != null && getType() == GrilleType.CLIENT_REPOSITORY;
	}
	
	@JsonIgnore
	public boolean isInvoiceRepo() {
		return getType() != null && getType() == GrilleType.INVOICE_REPOSITORY;
	}
	
	@JsonIgnore
	public boolean isCreditNoteRepo() {
		return getType() != null && getType() == GrilleType.CREDIT_NOTE_REPOSITORY;
	}
	
	@JsonIgnore
	public boolean isPostingEntryRepo() {
		return getType() != null && getType() == GrilleType.POSTING_ENTRY_REPOSITORY;
	}
	
	@JsonIgnore
	public boolean isBookingRepo() {
		return getType() != null && getType() == GrilleType.BOOKING_REPOSITORY;
	}
	
	@JsonIgnore
	public boolean isArchiveBackup() {
		return getType() != null && getType() == GrilleType.ARCHIVE_BACKUP;
	}
	
	@JsonIgnore
	public boolean isArchiveReplacement() {
		return getType() != null && getType() == GrilleType.ARCHIVE_REPLACEMENT;
	}
	
	@JsonIgnore
	public boolean isDashboardReport() {
		return getType() != null && getType() == GrilleType.DASHBOARD_REPORT;
	}
	
	@JsonIgnore
	public boolean isReporting() {
		return isDashboardReport() || isReport() || isReconciliation() || isBillingEventRepo() || isClientRepo() || isPostingEntryRepo() || isBookingRepo() || isArchiveBackup() || isArchiveReplacement();
	}
	
	

	

	@PostLoad
	public void initListChangeHandler() {
		columns.forEach( item -> { });	
		dimensions.forEach( item -> { });		
		this.columnListChangeHandler.setOriginalList(columns);
		this.dimensionListChangeHandler.setOriginalList(dimensions);
	}
	
	public GrilleColumn getColumnAt(int position) {
		for(GrilleColumn column : getColumnListChangeHandler().getItems()) {
			if(position == column.getPosition()) {
				return column;
			}
		}
		return null;
	}
	
	public GrilleColumn getColumnById(Long id) {
		for (GrilleColumn column : this.getColumnListChangeHandler().getItems()) {
			if (column.getId().equals(id)) {				
				return column;
			}
		}
		return null;
	}
	
	public List<GrilleColumn> getColumns(DimensionType dimensionType, Long dimensionId) {
		List<GrilleColumn> columns = new ArrayList<>();
		for(GrilleColumn column : getColumnListChangeHandler().getItems()) {
			if(dimensionType == column.getType() && dimensionId.equals(column.getDimensionId())) {
				columns.add(column);
			}
		}
		return columns;
	}
	
	public GrilleColumn getColumnByDimensionAndName(DimensionType dimensionType, String name) {
		for(GrilleColumn column : getColumnListChangeHandler().getItems()) {
			if(column.getType() == dimensionType && column.getName() != null && column.getName().equals(name)) {
				return column;
			}
		}
		return null;
	}
	
	@JsonIgnore
	public String getPublicationTableName() {
		return "GRID_" + getId();
	}
		

	@JsonIgnore
	@Override
	public Grille copy() {
		Grille copy = new Grille();
		copy.setName(this.getName() + System.currentTimeMillis());
		copy.setDataSourceId(getDataSourceId());
		copy.setDataSourceType(getDataSourceType());
		copy.setDataSourceName(getDataSourceName());
		copy.setGroup(this.getGroup());
		copy.setVisibleInShortcut(isVisibleInShortcut());
		copy.setShowAllRowsByDefault(isShowAllRowsByDefault());
		copy.setStatus(getStatus());
		copy.setType(getType());
		copy.setSourceType(getSourceType());
		copy.setSourceId(getSourceId());
		copy.setReportId(getReportId());
		copy.setCategory(getCategory());
		copy.setEditable(isEditable());	
		copy.setAllowLineCounting(isAllowLineCounting());
		copy.setConsolidated(isConsolidated());
		copy.setDebit(isDebit());
		copy.setCredit(isCredit());
		copy.setRowType(getRowType());
		copy.setPublished(getPublished());
		copy.setUserFilter(userFilter != null ? userFilter.copy() : null);
		copy.setAdminFilter(adminFilter != null ? adminFilter.copy() : null);
		for(GrilleColumn column : getColumnListChangeHandler().getItems()) {
			copy.getColumnListChangeHandler().addNew(column.copy());
		}
		for(GrilleDimension dimension : getDimensionListChangeHandler().getItems()) {
			copy.getDimensionListChangeHandler().addNew(dimension.copy());
		}		
		return copy;
	}



}

package com.moriset.bcephal.archive.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.grid.domain.Grille;

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

@Entity(name = "ArchiveConfiguration")
@Table(name = "BCP_ARCHIVE_CONFIGURATION")
@Data
@EqualsAndHashCode(callSuper = false)
public class ArchiveConfiguration extends MainObject {

	private static final long serialVersionUID = -5044795446057728667L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "archive_conf_seq")
	@SequenceGenerator(name = "archive_conf_seq", sequenceName = "archive_conf_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private String archiveName;
	private String description;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne
	@JoinColumn(name = "backupGrid")
	private Grille backupGrid;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne
	@JoinColumn(name = "replacementGrid")
	private Grille replacementGrid;
	
	private boolean allowReplacementGrid;
	
	@Enumerated(EnumType.STRING)
	private DataSourceType replacementTargetType;
	
	@Enumerated(EnumType.STRING)
	private ArchiveGridCreationType replacementGridCreationtType;
	
	private Long replacementMatGridId;
	
	private String replacementMatGridName;

	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "configurationId")
	private List<ArchiveConfigurationEnrichmentItem> enrichmentItems;

	@Transient
	private ListChangeHandler<ArchiveConfigurationEnrichmentItem> enrichmentItemListChangeHandler;
	
	public ArchiveConfiguration() {
		this.enrichmentItemListChangeHandler = new ListChangeHandler<ArchiveConfigurationEnrichmentItem>();
		this.enrichmentItems = new ArrayList<ArchiveConfigurationEnrichmentItem>();
		this.allowReplacementGrid = true;
		this.replacementTargetType = DataSourceType.UNIVERSE;
		this.replacementGridCreationtType = ArchiveGridCreationType.NEW;
	}
	
	
	public void setEnrichmentItems(List<ArchiveConfigurationEnrichmentItem> enrichmentItems) {
		this.enrichmentItems = enrichmentItems;
		this.enrichmentItemListChangeHandler.setOriginalList(enrichmentItems);
	}
	
	@PostLoad
	public void initListChangeHandler() {
		this.enrichmentItems.forEach( item -> { });
		this.enrichmentItemListChangeHandler.setOriginalList(this.enrichmentItems);
	}

	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}

}

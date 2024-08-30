package com.moriset.bcephal.archive.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * archive model
 * 
 * @author MORISET-004
 *
 */

@Entity(name = "Archive")
@Table(name = "BCP_ARCHIVE")
@Data
@EqualsAndHashCode(callSuper = false)
public class Archive extends MainObject {

	private static final long serialVersionUID = -4452792967921052941L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "archive_seq")
	@SequenceGenerator(name = "archive_seq", sequenceName = "archive_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@JsonIgnore
	private Long configurationId;
	private String code;
	private String description;
	private String userName;
	private String tableName;
	private long lineCount;

	@Enumerated(EnumType.STRING)
	private ArchiveStatus status;
	
	private boolean allowReplacementGrid;
	
	@Enumerated(EnumType.STRING)
	private DataSourceType replacementTargetType;
	
	private Long replacementMatGridId;
	
	public Archive() {
		this.allowReplacementGrid = true;
		this.replacementTargetType = DataSourceType.UNIVERSE;
	}
	
	@Override
	public Persistent copy() {		
		return null;
	}

}

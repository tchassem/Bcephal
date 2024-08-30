/**
 * 
 */
package com.moriset.bcephal.loader.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Moriset
 *
 */
@Entity(name = "FileLoaderLog")
@Table(name = "BCP_FILE_LOADER_LOG")
@Data
@EqualsAndHashCode(callSuper = false)
public class FileLoaderLog extends Persistent {

	private static final long serialVersionUID = -8225931588222973810L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_loader_log_seq")
	@SequenceGenerator(name = "file_loader_log_seq", sequenceName = "file_loader_log_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private Long loaderId;

	private String loaderName;

	@Enumerated(EnumType.STRING)
	private FileLoaderMethod uploadMethod;

	@Enumerated(EnumType.STRING)
	private RunModes mode;

	@Enumerated(EnumType.STRING)
	private RunStatus status;

	private String username;

	private int fileCount;

	private int emptyFileCount;

	private int errorFileCount;

	private int loadedFileCount;

	private boolean error;

	private String message;

	private Date startDate;

	private Date endDate;
	
	private String operationCode;
	
	@ToString.Exclude
	@Transient
	@JsonIgnore
	private List<String> files = new ArrayList<>();	

	@Override
	public Persistent copy() {
		return null;
	}

}

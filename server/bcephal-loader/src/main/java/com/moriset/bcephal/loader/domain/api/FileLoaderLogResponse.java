package com.moriset.bcephal.loader.domain.api;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.loader.domain.FileLoaderMethod;

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
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "FileLoaderLogResponse")
@Table(name = "BCP_FILE_LOADER_LOG")
@Data
@EqualsAndHashCode(callSuper = false)
public class FileLoaderLogResponse {

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_loader_log_seq")
	@SequenceGenerator(name = "file_loader_log_seq", sequenceName = "file_loader_log_seq", initialValue = 1, allocationSize = 1)
	private Long id;

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
	
	@JsonIgnore
	private String operationCode;
	
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "log")
	private List<FileLoaderLogItemResponse> items;
	
	@PostLoad
	public void initListChangeHandler() {		
		items.forEach(x->{});
	}
	
}

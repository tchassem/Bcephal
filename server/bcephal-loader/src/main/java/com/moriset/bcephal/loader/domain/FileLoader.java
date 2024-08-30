/**
 * 
 */
package com.moriset.bcephal.loader.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.SchedulableObject;
import com.moriset.bcephal.domain.routine.RoutineExecutor;

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
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "FileLoader")
@Table(name = "BCP_FILE_LOADER")
@Data
@EqualsAndHashCode(callSuper = false)
public class FileLoader extends SchedulableObject {

	private static final long serialVersionUID = -8715311767062891116L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_loader_seq")
	@SequenceGenerator(name = "file_loader_seq", sequenceName = "file_loader_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private boolean hasHeader;

	private int headerRowCount;

	private String file;

	private boolean indentifySheetByPosition;

	private boolean loadAllSheets;
	
	private boolean checkFileNameDuplication;

	private String sheetName;

	private int sheetIndex;

//	private String repository;
//
//	private String repositoryOnServer;
	
	
	private Long templateId;

	private String fileSeparator;

	private String fileExtension;
	
	private String dateFormat;
	
	private String thousandSeparator;
	private String decimalSeparator;
	
	@Transient
	private String columnDelimiter;

	@Enumerated(EnumType.STRING)
	private FileLoaderMethod uploadMethod;

	private Long targetId;

	private String targetName;

	private boolean allowBackup;

	private int maxBackupCount;
	
	@Enumerated(EnumType.STRING)
	private FileLoaderSource source;
	
	private boolean confirmAction;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "loaderId")
	private List<FileLoaderRepository> repositories;
		
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<FileLoaderRepository> repositoryListChangeHandler;


	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "loader")
	private List<FileLoaderColumn> columns;
	
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<FileLoaderColumn> columnListChangeHandler;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<RoutineExecutor> routineListChangeHandler;

	public FileLoader() {
		this.confirmAction = true;	
		this.repositories = new ArrayList<>();
		this.repositoryListChangeHandler = new ListChangeHandler<>();
		this.columns = new ArrayList<FileLoaderColumn>();
		this.columnListChangeHandler = new ListChangeHandler<>();
		this.routineListChangeHandler = new ListChangeHandler<>();		
		this.allowBackup = true;
		this.maxBackupCount = 5;
		this.hasHeader = true;
		this.headerRowCount = 1;
		this.fileSeparator = ";";
		this.source = FileLoaderSource.SERVER;
		this.indentifySheetByPosition = true;
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

	@PostLoad
	public void initListChangeHandler() {
		repositories.size();
		this.repositoryListChangeHandler.setOriginalList(repositories);
		Collections.sort(columns, new Comparator<FileLoaderColumn>() {
			@Override
			public int compare(FileLoaderColumn column1, FileLoaderColumn column2) {
				return column1.getPosition() - column2.getPosition();
			}
		});
		columns.size();
		this.columnListChangeHandler.setOriginalList(columns);
	}

	@Override
	public FileLoader copy() {
		FileLoader copy = new FileLoader();
		copy.setName(getName() + System.currentTimeMillis());
		copy.setGroup(this.getGroup());
		copy.setVisibleInShortcut(isVisibleInShortcut());
		copy.setActive(isActive());
		copy.setScheduled(isScheduled());
		copy.setCronExpression(getCronExpression());
		copy.setHasHeader(isHasHeader());
		copy.setHeaderRowCount(getHeaderRowCount());
		copy.setFile(getFile());
		copy.setIndentifySheetByPosition(isIndentifySheetByPosition());
		copy.setLoadAllSheets(isLoadAllSheets());
		copy.setSheetName(getSheetName());
		copy.setSheetIndex(getSheetIndex());
//		copy.setRepository(getRepository());
//		copy.setRepositoryOnServer(getRepositoryOnServer());
		copy.setFileSeparator(getFileSeparator());
		copy.setFileExtension(getFileExtension());
		copy.setDateFormat(getDateFormat());
		copy.setUploadMethod(getUploadMethod());
		copy.setTargetId(getTargetId());
		copy.setTargetName(getTargetName());
		copy.setAllowBackup(isAllowBackup());
		copy.setMaxBackupCount(getMaxBackupCount());
		copy.setCheckFileNameDuplication(isCheckFileNameDuplication());
		for(FileLoaderRepository item : getRepositoryListChangeHandler().getItems()) {
			copy.getRepositoryListChangeHandler().addNew(item.copy());
		}
		for(FileLoaderColumn item : getColumnListChangeHandler().getItems()) {
			copy.getColumnListChangeHandler().addNew(item.copy());
		}
		for(RoutineExecutor item : getRoutineListChangeHandler().getItems()) {
			copy.getRoutineListChangeHandler().addNew(item.copy());
		}
		return copy;
	}

	

}

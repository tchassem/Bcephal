package com.moriset.bcephal.loader.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
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

@Entity(name = "FileLoaderRepository")
@Table(name = "BCP_FILE_LOADER_REPOSITORY")
@EqualsAndHashCode(callSuper = false)
@Data
public class FileLoaderRepository extends Persistent {
	
	private static final long serialVersionUID = -9065505240341879125L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_loader_repository_seq")
	@SequenceGenerator(name = "file_loader_repository_seq", sequenceName = "file_loader_repository_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "loaderId")
	private FileLoader loaderId;

	private String repository;

	private String repositoryOnServer;
	
	private int position;
		
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "loader")
	private List<FileLoaderNameCondition> conditions;
		
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<FileLoaderNameCondition> conditionListChangeHandler;
	
	
	public FileLoaderRepository() {
		this.conditions = new ArrayList<FileLoaderNameCondition>();
		this.conditionListChangeHandler = new ListChangeHandler<FileLoaderNameCondition>();
	}

	@PostLoad
	public void initListChangeHandler() {
		conditions.size();
		this.conditionListChangeHandler.setOriginalList(conditions);
	}
	
	
	public boolean validateFileName(String file) {
		for (FileLoaderNameCondition condition : this.getConditionListChangeHandler().getItems()) {
			if (!condition.validateFileName(file)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public FileLoaderRepository copy() {
		FileLoaderRepository copy = new FileLoaderRepository();
		
		copy.setRepository(getRepository());
		copy.setRepositoryOnServer(getRepositoryOnServer());
		copy.setPosition(getPosition());
		for(FileLoaderNameCondition item : getConditionListChangeHandler().getItems()) {
			copy.getConditionListChangeHandler().addNew(item.copy());
		}
		return copy;
	}
	
}

package com.moriset.bcephal.loader.domain.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "FileLoaderLogItemResponse")
@Table(name = "BCP_FILE_LOADER_LOG_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class FileLoaderLogItemResponse {

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_loader_log_item_seq")
	@SequenceGenerator(name = "file_loader_log_item_seq", sequenceName = "file_loader_log_item_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne
	@JoinColumn(name = "log")
	private FileLoaderLogResponse log;

	private String file;

	private int lineCount;

	private boolean empty;

	private boolean loaded;

	private boolean error;

	private String message;
	
}

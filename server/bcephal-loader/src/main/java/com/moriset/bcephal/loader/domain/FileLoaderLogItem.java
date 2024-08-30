/**
 * 
 */
package com.moriset.bcephal.loader.domain;

import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Entity(name = "FileLoaderLogItem")
@Table(name = "BCP_FILE_LOADER_LOG_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class FileLoaderLogItem extends Persistent {

	private static final long serialVersionUID = 6502729689761611000L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_loader_log_item_seq")
	@SequenceGenerator(name = "file_loader_log_item_seq", sequenceName = "file_loader_log_item_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private Long log;

	private String file;

	private int lineCount;

	private boolean empty;

	private boolean loaded;

	private boolean error;

	private String message;

	@Override
	public Persistent copy() {
		return null;
	}
}

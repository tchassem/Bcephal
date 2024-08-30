/**
 * 
 */
package com.moriset.bcephal.loader.domain;

import org.apache.commons.io.FilenameUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "FileLoaderNameCondition")
@Table(name = "BCP_FILE_LOADER_NAME_CONDITION")
@Data
@EqualsAndHashCode(callSuper = false)
public class FileLoaderNameCondition extends Persistent {

	private static final long serialVersionUID = 2665146866030338684L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_loader_name_cond_seq")
	@SequenceGenerator(name = "file_loader_name_cond_seq", sequenceName = "file_loader_name_cond_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "loader")
	private FileLoaderRepository loader;

	@Enumerated(EnumType.STRING)
	private FileNameCondition condition;

	private String filter;

	private int position;

	public boolean validateFileName(String file) {
		if (this.getCondition() != null) {
			if (this.getCondition() == FileNameCondition.BEGINS_WITH) {
				return file.toUpperCase().startsWith(this.filter.toUpperCase());
			} else if (this.getCondition() == FileNameCondition.CONTAINS) {
				return file.toUpperCase().contains(this.filter.toUpperCase());
			} else if (this.getCondition() == FileNameCondition.DO_NOT_CONTAINS) {
				return !file.toUpperCase().contains(this.filter.toUpperCase());
			} else if (this.getCondition() == FileNameCondition.ENDS_WITH) {
				String name = FilenameUtils.removeExtension(file);
				return name.toUpperCase().endsWith(this.filter.toUpperCase());
			}
		}
		return true;
	}

	@Override
	public FileLoaderNameCondition copy() {
		FileLoaderNameCondition copy = new FileLoaderNameCondition();
		copy.setLoader(getLoader());
		copy.setCondition(getCondition());
		copy.setFilter(getFilter());
		copy.setPosition(getPosition());
		return copy;
	}

}

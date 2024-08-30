/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * @author Joseph Wambo
 *
 */
@Entity(name = "FileLoaderColumnSoft")
@Table(name = "BCP_FILE_LOADER_COLUMN")
@Data
@EqualsAndHashCode(callSuper = false)
public class FileLoaderColumnSoft extends Persistent {

	private static final long serialVersionUID = 4255379824166045689L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_loader_column_seq")
	@SequenceGenerator(name = "file_loader_column_seq", sequenceName = "file_loader_column_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@JsonIgnore
	private Long loader;

	private Long grilleColumn;


	@Override
	public FileLoaderColumnSoft copy() {
		FileLoaderColumnSoft copy = new FileLoaderColumnSoft();
		copy.setLoader(getLoader());
		copy.setGrilleColumn(getGrilleColumn());
		return copy;
	}

}

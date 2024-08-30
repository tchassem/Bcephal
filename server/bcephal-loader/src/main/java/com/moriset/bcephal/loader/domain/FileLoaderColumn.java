/**
 * 
 */
package com.moriset.bcephal.loader.domain;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.grid.domain.GrilleColumn;

import jakarta.persistence.Entity;
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
@Entity(name = "FileLoaderColumn")
@Table(name = "BCP_FILE_LOADER_COLUMN")
@Data
@EqualsAndHashCode(callSuper = false)
public class FileLoaderColumn extends Persistent {

	private static final long serialVersionUID = 5584222971805585954L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_loader_column_seq")
	@SequenceGenerator(name = "file_loader_column_seq", sequenceName = "file_loader_column_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "loader")
	private FileLoader loader;

	private DimensionType type;

	private Long dimensionId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne
	@JoinColumn(name = "grilleColumn")
	private GrilleColumn grilleColumn;

	private String fileColumn;

	private int position;

	private Integer sheetPposition;

	private String sheetName;

	@Override
	public FileLoaderColumn copy() {
		FileLoaderColumn copy = new FileLoaderColumn();
		copy.setLoader(getLoader());
		copy.setType(getType());
		copy.setDimensionId(getDimensionId());
		copy.setGrilleColumn(getGrilleColumn() != null ? getGrilleColumn().copy() : null);
		copy.setFileColumn(getFileColumn());
		copy.setPosition(getPosition());
		copy.setSheetName(getSheetName());
		copy.setSheetPposition(getSheetPposition());
		return copy;
	}

}

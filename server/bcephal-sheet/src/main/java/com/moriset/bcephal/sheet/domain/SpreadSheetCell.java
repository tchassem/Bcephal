/**
 * 
 */
package com.moriset.bcephal.sheet.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.filters.MeasureFilterItem;
import com.moriset.bcephal.domain.filters.UniverseFilter;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Entity(name = "SpreadSheetCell")
@Table(name = "BCP_SPREADSHEET_CELL")
@Data
@EqualsAndHashCode(callSuper = false)
public class SpreadSheetCell extends Persistent {

	private static final long serialVersionUID = 1585486346524003012L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "spreadsheet_cell_seq")
	@SequenceGenerator(name = "spreadsheet_cell_seq", sequenceName = "spreadsheet_cell_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@JsonIgnore
	private Long spreadSheet;

	@Enumerated(EnumType.STRING)
	private SpreadSheetType type;

	private String name;

	private int col;

	private int row;

	private String sheetName;

	private int sheetIndex;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne
	@JoinColumn(name = "cellMeasure")
	private MeasureFilterItem cellMeasure;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne
	@JoinColumn(name = "filter")
	private UniverseFilter filter;

	private boolean refreshWhenEdit;

	public SpreadSheetCell() {
		this.refreshWhenEdit = false;
	}

	public SpreadSheetCell(Cell cell) {
		this();
		this.name = cell.getName();
		this.row = cell.getRow();
		this.col = cell.getColumn();
		this.sheetIndex = cell.getSheet();
	}

	@JsonIgnore
	public boolean isReport() {
		return this.type == SpreadSheetType.REPORT;
	}

	@JsonIgnore
	public boolean isInput() {
		return this.type == SpreadSheetType.INPUT;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public Persistent copy() {
		return null;
	}

}

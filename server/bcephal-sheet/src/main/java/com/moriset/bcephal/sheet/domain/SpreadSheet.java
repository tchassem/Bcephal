/**
 * 
 */
package com.moriset.bcephal.sheet.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.Persistent;
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
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "SpreadSheet")
@Table(name = "BCP_SPREADSHEET")
@Data
@EqualsAndHashCode(callSuper = false)
public class SpreadSheet extends MainObject {

	private static final long serialVersionUID = -3386585442888774953L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "spreadsheet_seq")
	@SequenceGenerator(name = "spreadsheet_seq", sequenceName = "spreadsheet_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@Enumerated(EnumType.STRING)
	private SpreadSheetType type;

	@Enumerated(EnumType.STRING)
	private SpreadSheetSource sourceType;

	private Long sourceId;

	private boolean useLink;

	private boolean active;

	private boolean template;

	private String fileName;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude	
	@ManyToOne
	@JoinColumn(name = "filter")
	private UniverseFilter filter;

	@JsonIgnore
	@Transient
	private ListChangeHandler<SpreadSheetCell> cellListChangeHandler;

	public SpreadSheet() {
		this.cellListChangeHandler = new ListChangeHandler<SpreadSheetCell>();
	}

	@JsonIgnore
	public boolean isReport() {
		return this.type == SpreadSheetType.REPORT;
	}

	@JsonIgnore
	public boolean isInput() {
		return this.type == SpreadSheetType.INPUT;
	}

	public void addCell(SpreadSheetCell cell) {
		cellListChangeHandler.addNew(cell);
	}

	public void UpdateCell(SpreadSheetCell cell) {
		cellListChangeHandler.addUpdated(cell);
	}

	public void deleteOrForgetCell(SpreadSheetCell cell) {
		if (cell.isPersistent()) {
			deleteCell(cell);
		} else {
			forgetCell(cell);
		}
	}

	public void deleteCell(SpreadSheetCell cell) {
		cellListChangeHandler.addDeleted(cell);
	}

	public void forgetCell(SpreadSheetCell cell) {
		cellListChangeHandler.forget(cell);
	}

	public SpreadSheetCell getCell(Cell refetrence) {
		return getCellAt(refetrence.getRow(), refetrence.getColumn(), refetrence.getSheet());
	}

	public SpreadSheetCell getCellAt(int row, int column, int sheet) {
		for (SpreadSheetCell cell : cellListChangeHandler.getItems()) {
			if (cell.getSheetIndex() == sheet && cell.getCol() == column && cell.getRow() == row) {
				return cell;
			}
		}
		return null;
	}

	@JsonIgnore
	public String getDbTableName() {
		return "SPREADSHEET_" + getId();
	}

	@Override
	public Persistent copy() {
		return null;
	}

}

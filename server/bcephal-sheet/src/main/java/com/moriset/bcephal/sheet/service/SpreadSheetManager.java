/**
 * 
 */
package com.moriset.bcephal.sheet.service;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.filters.MeasureFilterItem;
import com.moriset.bcephal.sheet.domain.Cell;
import com.moriset.bcephal.sheet.domain.RangeUtil;
import com.moriset.bcephal.sheet.domain.SpreadSheet;
import com.moriset.bcephal.sheet.domain.SpreadSheetCell;
import com.moriset.bcephal.sheet.domain.SpreadSheetCellChange;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class SpreadSheetManager {

	@JsonIgnore
	private SpreadSheetService service;

	private SpreadSheet spreadSheet;

	private List<SpreadSheetCellChange> changes;

	public SpreadSheetManager() {
		this.changes = new ArrayList<SpreadSheetCellChange>();
	}

	public void buildCells() {
		if (spreadSheet.getId() != null) {
			spreadSheet.getCellListChangeHandler().setOriginalList(service.getCells(spreadSheet.getId()));
		}
		for (SpreadSheetCellChange change : this.changes) {
			for (Cell reference : change.getRange().getCells()) {
				SpreadSheetCell cell = this.spreadSheet.getCell(reference);
				if (cell == null) {
					cell = createCell(reference);
					this.spreadSheet.getCellListChangeHandler().addNew(cell);
					applyChanged(cell);
				}
				if (change.isReset()) {
					this.spreadSheet.deleteOrForgetCell(cell);
					cell = null;
				} else {
					if (cell.getId() != null) {
						this.spreadSheet.getCellListChangeHandler().addUpdated(cell);
					}
					applyChanged(cell);
				}
			}
		}
	}

	public void clear() {
		this.changes.clear();
	}

	public void addChange(SpreadSheetCellChange cellChange, SpreadSheetCell cell) {
		this.changes.add(cellChange);
		if (cell.getId() != null) {
			this.spreadSheet.UpdateCell(cell);
		}
	}

	public SpreadSheetCell getCell(Cell reference) {
		SpreadSheetCell cell = this.spreadSheet.getCell(reference);
		if (cell == null && this.spreadSheet.getId() != null) {
			cell = retriveCellFormServer(reference);
			if (cell != null) {
				if (hasResetChange(cell)) {
					this.spreadSheet.deleteOrForgetCell(cell);
					cell = null;
				} else {
					this.spreadSheet.getCellListChangeHandler().getOriginalList().add(cell);
					applyChanged(cell);
				}
			}
		}
		if (cell == null) {
			cell = createCell(reference);
			applyChanged(cell);
		}
		return cell;
	}

	private SpreadSheetCell retriveCellFormServer(Cell reference) {
		return service.getCell(spreadSheet.getId(), reference.getColumn(), reference.getRow(), reference.getSheet());
	}

	private SpreadSheetCell createCell(Cell reference) {
		SpreadSheetCell cell = new SpreadSheetCell(reference);
		cell.setCellMeasure(new MeasureFilterItem());
		cell.setType(this.spreadSheet.getType());
		if (cell.getName() == null) {
			cell.setName(RangeUtil.getCellName(cell.getRow(), cell.getCol()));
		}
		return cell;
	}

	private void applyChanged(SpreadSheetCell cell) {
		List<SpreadSheetCellChange> changes = getApplicableChanges(cell);
		for (SpreadSheetCellChange change : changes) {
			change.apply(cell);
		}
	}

	private List<SpreadSheetCellChange> getApplicableChanges(SpreadSheetCell cell) {
		List<SpreadSheetCellChange> changes = new ArrayList<SpreadSheetCellChange>();
		for (int i = this.changes.size(); i > 0; i--) {
			SpreadSheetCellChange change = this.changes.get(i - 1);
			if (change.contains(cell)) {
				if (change.isReset()) {
					break;
				}
				changes.add(0, change);
			}
		}
		return changes;
	}

	private boolean hasResetChange(SpreadSheetCell cell) {
		for (SpreadSheetCellChange change : this.changes) {
			if (change.isReset() && change.contains(cell)) {
				return true;
			}
		}
		return false;
	}

}

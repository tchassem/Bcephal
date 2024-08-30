/**
 * 
 */
package com.moriset.bcephal.sheet.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class RangeItem {

	private int row1;

	private int row2;

	private int column1;

	private int column2;

	private int sheet;

	private String firstCellName;

	private String lastCellName;

	public RangeItem() {
	}

	public RangeItem(int row1, int row2, int column1, int column2, int sheet, String name1, String name2) {
		this();
		this.row1 = row1;
		this.row2 = row2;
		this.column1 = column1;
		this.column2 = column2;
		this.sheet = sheet;
		this.firstCellName = name1;
		this.lastCellName = name2;
	}

	@JsonIgnore
	public int getCellCount() {
		return (this.row2 - this.row1 + 1) * (this.column2 - this.column1 + 1);
	}

	@JsonIgnore
	public String getName() {
		String cell1 = firstCellName;
		String cell2 = lastCellName;
		if (cell1 == cell2)
			return cell1;
		return cell1 + ":" + cell2;
	}

	@JsonIgnore
	public Cell getFirstCell() {
		return new Cell(row1, column1, this.sheet, firstCellName);
	}

	@JsonIgnore
	public Cell getLastCell() {
		return new Cell(row2, column2, this.sheet, lastCellName);
	}

	@JsonIgnore
	public List<Cell> getCells() {
		List<Cell> cells = new ArrayList<Cell>(0);
		for (int row = this.row1; row <= this.row2; row++) {
			for (int col = this.column1; col <= this.column2; col++) {
				cells.add(new Cell(row, col, this.sheet, null));
			}
		}
		return cells;
	}

	public boolean contains(int row, int col, int sheet) {
		return sheet == this.sheet && row >= this.row1 && row <= this.row2 && col >= this.column1
				&& col <= this.column2;
	}

	@Override
	public String toString() {
		return getName();
	}

}

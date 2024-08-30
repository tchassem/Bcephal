/**
 * 
 */
package com.moriset.bcephal.sheet.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class Range {

	private Sheet sheet;

	private List<RangeItem> items;

	public Range() {
		this.items = new ArrayList<RangeItem>();
	}

	public Range(Sheet sheet) {
		this();
		this.sheet = sheet;
	}

	@JsonIgnore
	public int getCellCount() {
		int count = 0;
		for (RangeItem item : items)
			count += item.getCellCount();
		return count;
	}

	@JsonIgnore
	public List<Cell> getCells() {
		List<Cell> cells = new ArrayList<Cell>(0);
		for (RangeItem item : items)
			cells.addAll(item.getCells());
		return cells;
	}

	@JsonIgnore
	public Cell getFirstCell() {
		if (items == null || items.size() == 0)
			return null;
		return items.get(0).getFirstCell();
	}

	@JsonIgnore
	public Cell getLastCell() {
		if (items == null || items.size() == 0)
			return null;
		return items.get(items.size() - 1).getLastCell();
	}

	@JsonIgnore
	public String getName() {
		String name = "";
		for (RangeItem item : items) {
			name += StringUtils.hasText(name) ? ";" : "";
			name += item.getName();
		}
		return name;
	}

	@JsonIgnore
	public String getFullName() {
		String name = this.sheet != null && StringUtils.hasText(this.sheet.getName()) ? this.sheet.getName() + "!" : "";
		name += getName();
		return name;
	}

	public boolean contains(int row, int col, int sheet) {
		for (RangeItem item : items) {
			if (item.contains(row, col, sheet))
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return getFullName();
	}

}

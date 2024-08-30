/**
 * 
 */
package com.moriset.bcephal.sheet.domain;

import com.moriset.bcephal.domain.filters.AttributeFilter;
import com.moriset.bcephal.domain.filters.AttributeFilterItem;
import com.moriset.bcephal.domain.filters.MeasureFilter;
import com.moriset.bcephal.domain.filters.MeasureFilterItem;
import com.moriset.bcephal.domain.filters.PeriodFilter;
import com.moriset.bcephal.domain.filters.PeriodFilterItem;
import com.moriset.bcephal.domain.filters.SpotFilter;
import com.moriset.bcephal.domain.filters.SpotFilterItem;
import com.moriset.bcephal.domain.filters.UniverseFilter;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class SpreadSheetCellChange {

	private SpreadSheetCell cell;

	private Range range;

	private boolean reset;

	private boolean cellMeasureChanged;

	private boolean measureFilterChanged;

	private boolean attributeFilterChanged;

	private boolean periodFilterChanged;

	private boolean spotFilterChanged;

	public SpreadSheetCellChange() {
	}

	public SpreadSheetCellChange(Range range) {
		this();
		this.range = range;
	}

	public SpreadSheetCellChange(SpreadSheetCell cell) {
		this();
		this.cell = new SpreadSheetCell();
		this.cell.setRow(cell.getRow());
		this.cell.setCol(cell.getCol());
		this.cell.setName(cell.getName());
		this.cell.setSheetIndex(cell.getSheetIndex());
		this.cell.setSheetName(cell.getSheetName());
	}

	public SpreadSheetCell apply(SpreadSheetCell reference) {
		if (this.cell != null) {
			if (isCellMeasureChanged() && this.cell.getCellMeasure() != null) {
				if (reference.getCellMeasure() == null) {
					reference.setCellMeasure(new MeasureFilterItem());
				}
				String formula = this.cell.getCellMeasure().getFormula();
				formula = RangeUtil.buildRefFormula(this.cell, reference, formula);
				reference.getCellMeasure().synchronize(this.cell.getCellMeasure(), formula);
			}
			if (isMeasureFilterChanged() && this.cell.getFilter() != null) {
				if (reference.getFilter() == null) {
					reference.setFilter(new UniverseFilter());
				}
				if (reference.getFilter().getMeasureFilter() == null) {
					reference.getFilter().setMeasureFilter(new MeasureFilter());
				}
				synchronize(this.cell, reference, this.cell.getFilter().getMeasureFilter(),
						reference.getFilter().getMeasureFilter());
			}
			if (isPeriodFilterChanged() && this.cell.getFilter() != null) {
				if (reference.getFilter() == null) {
					reference.setFilter(new UniverseFilter());
				}
				if (reference.getFilter().getPeriodFilter() == null) {
					reference.getFilter().setPeriodFilter(new PeriodFilter());
				}
				synchronize(this.cell, reference, this.cell.getFilter().getPeriodFilter(),
						reference.getFilter().getPeriodFilter());
			}
			if (isAttributeFilterChanged() && this.cell.getFilter() != null) {
				if (reference.getFilter() == null) {
					reference.setFilter(new UniverseFilter());
				}
				if (reference.getFilter().getAttributeFilter() == null) {
					reference.getFilter().setAttributeFilter(new AttributeFilter());
				}
				synchronize(this.cell, reference, this.cell.getFilter().getAttributeFilter(),
						reference.getFilter().getAttributeFilter());
			}
			if (isSpotFilterChanged() && this.cell.getFilter() != null) {
				if (reference.getFilter() == null) {
					reference.setFilter(new UniverseFilter());
				}
				if (reference.getFilter().getSpotFilter() == null) {
					reference.getFilter().setSpotFilter(new SpotFilter());
				}
				synchronize(this.cell, reference, this.cell.getFilter().getSpotFilter(),
						reference.getFilter().getSpotFilter());
			}
		}
		return reference;
	}

	public boolean contains(SpreadSheetCell cell) {
		return this.range != null ? this.range.contains(cell.getRow(), cell.getCol(), cell.getSheetIndex()) : false;
	}

	protected void synchronize(SpreadSheetCell cell, SpreadSheetCell referenceCell, AttributeFilter filter,
			AttributeFilter referenceFilter) {
		for (AttributeFilterItem item : filter.getItemListChangeHandler().getNewItems()) {
			String formula = item.getFormula();
			formula = RangeUtil.buildRefFormula(cell, referenceCell, formula);
			AttributeFilterItem reference = referenceFilter.getItemAtPosition(item.getPosition());
			if (reference != null) {
				reference.synchronize(item, formula);
				referenceFilter.updateItem(reference);
			} else {
				reference = new AttributeFilterItem();
				reference.synchronize(item, formula);
				referenceFilter.addItem(reference);
			}
		}
		for (AttributeFilterItem item : filter.getItemListChangeHandler().getUpdatedItems()) {
			String formula = item.getFormula();
			formula = RangeUtil.buildRefFormula(cell, referenceCell, formula);
			AttributeFilterItem reference = referenceFilter.getItemAtPosition(item.getPosition());
			if (reference != null) {
				reference.synchronize(item, formula);
				referenceFilter.updateItem(reference);
			} else {
				reference = new AttributeFilterItem();
				reference.synchronize(item, formula);
				referenceFilter.addItem(reference);
			}
		}
		for (AttributeFilterItem item : filter.getItemListChangeHandler().getDeletedItems()) {
			AttributeFilterItem reference = referenceFilter.getItemAtPosition(item.getPosition());
			if (reference != null) {
				referenceFilter.deleteOrForgetItem(reference);
			}
		}
	}

	protected void synchronize(SpreadSheetCell cell, SpreadSheetCell referenceCell, MeasureFilter filter,
			MeasureFilter referenceFilter) {
		for (MeasureFilterItem item : filter.getItemListChangeHandler().getNewItems()) {
			String formula = item.getFormula();
			formula = RangeUtil.buildRefFormula(cell, referenceCell, formula);
			MeasureFilterItem reference = referenceFilter.getItemAtPosition(item.getPosition());
			if (reference != null) {
				reference.synchronize(item, formula);
				referenceFilter.updateItem(reference);
			} else {
				reference = new MeasureFilterItem();
				reference.synchronize(item, formula);
				referenceFilter.addItem(reference);
			}
		}
		for (MeasureFilterItem item : filter.getItemListChangeHandler().getUpdatedItems()) {
			String formula = item.getFormula();
			formula = RangeUtil.buildRefFormula(cell, referenceCell, formula);
			MeasureFilterItem reference = referenceFilter.getItemAtPosition(item.getPosition());
			if (reference != null) {
				reference.synchronize(item, formula);
				referenceFilter.updateItem(reference);
			} else {
				reference = new MeasureFilterItem();
				reference.synchronize(item, formula);
				referenceFilter.addItem(reference);
			}
		}
		for (MeasureFilterItem item : filter.getItemListChangeHandler().getDeletedItems()) {
			MeasureFilterItem reference = referenceFilter.getItemAtPosition(item.getPosition());
			if (reference != null) {
				referenceFilter.deleteOrForgetItem(reference);
			}
		}
	}

	protected void synchronize(SpreadSheetCell cell, SpreadSheetCell referenceCell, PeriodFilter filter,
			PeriodFilter referenceFilter) {
		for (PeriodFilterItem item : filter.getItemListChangeHandler().getNewItems()) {
			String formula = item.getFormula();
			formula = RangeUtil.buildRefFormula(cell, referenceCell, formula);
			PeriodFilterItem reference = referenceFilter.getItemAtPosition(item.getPosition());
			if (reference != null) {
				reference.synchronize(item, formula);
				referenceFilter.updateItem(reference);
			} else {
				reference = new PeriodFilterItem();
				reference.synchronize(item, formula);
				referenceFilter.addItem(reference);
			}
		}
		for (PeriodFilterItem item : filter.getItemListChangeHandler().getUpdatedItems()) {
			String formula = item.getFormula();
			formula = RangeUtil.buildRefFormula(cell, referenceCell, formula);
			PeriodFilterItem reference = referenceFilter.getItemAtPosition(item.getPosition());
			if (reference != null) {
				reference.synchronize(item, formula);
				referenceFilter.updateItem(reference);
			} else {
				reference = new PeriodFilterItem();
				reference.synchronize(item, formula);
				referenceFilter.addItem(reference);
			}
		}
		for (PeriodFilterItem item : filter.getItemListChangeHandler().getDeletedItems()) {
			PeriodFilterItem reference = referenceFilter.getItemAtPosition(item.getPosition());
			if (reference != null) {
				referenceFilter.deleteOrForgetItem(reference);
			}
		}
	}

	protected void synchronize(SpreadSheetCell cell, SpreadSheetCell referenceCell, SpotFilter filter,
			SpotFilter referenceFilter) {
		for (SpotFilterItem item : filter.getItemListChangeHandler().getNewItems()) {
			String formula = item.getFormula();
			formula = RangeUtil.buildRefFormula(cell, referenceCell, formula);
			SpotFilterItem reference = referenceFilter.getItemAtPosition(item.getPosition());
			if (reference != null) {
				reference.synchronize(item, formula);
				referenceFilter.updateItem(reference);
			} else {
				reference = new SpotFilterItem();
				reference.synchronize(item, formula);
				referenceFilter.addItem(reference);
			}
		}
		for (SpotFilterItem item : filter.getItemListChangeHandler().getUpdatedItems()) {
			String formula = item.getFormula();
			formula = RangeUtil.buildRefFormula(cell, referenceCell, formula);
			SpotFilterItem reference = referenceFilter.getItemAtPosition(item.getPosition());
			if (reference != null) {
				reference.synchronize(item, formula);
				referenceFilter.updateItem(reference);
			} else {
				reference = new SpotFilterItem();
				reference.synchronize(item, formula);
				referenceFilter.addItem(reference);
			}
		}
		for (SpotFilterItem item : filter.getItemListChangeHandler().getDeletedItems()) {
			SpotFilterItem reference = referenceFilter.getItemAtPosition(item.getPosition());
			if (reference != null) {
				referenceFilter.deleteOrForgetItem(reference);
			}
		}
	}

}

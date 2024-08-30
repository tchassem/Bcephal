package com.moriset.bcephal.reconciliation.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.UnionGridColumn;

import lombok.Data;

@Data
public class ReconciliationModelColumns {

	private List<GrilleColumn> leftColumns;
	private List<GrilleColumn> rightColumns;
	
	public ReconciliationModelColumns() {
		this.leftColumns = new ArrayList<>();
		this.rightColumns = new ArrayList<>();
	}

	public ReconciliationModelColumns(ReconciliationModel model) {
		this();
		if(model.getLeftGrid() != null) {
			for(GrilleColumn column : model.getLeftGrid().getColumnListChangeHandler().getItems()) {
				this.leftColumns.add(new GrilleColumn(column.getId(), column.getName(), column.getType(), column.getDimensionId(), column.getDimensionName()));
			}
		}
		if(model.getRigthGrid() != null) {
			for(GrilleColumn column : model.getRigthGrid().getColumnListChangeHandler().getItems()) {
				this.rightColumns.add(new GrilleColumn(column.getId(), column.getName(), column.getType(), column.getDimensionId(), column.getDimensionName()));
			}
		}
	}
	
	public ReconciliationModelColumns(ReconciliationUnionModel model) {
		this();
		if(model.getLeftGrid() != null) {
			if(model.getLeftGrid().isUnion() && model.getLeftGrid().getGrid() != null) {
				for(UnionGridColumn column : model.getLeftGrid().getGrid().getColumnListChangeHandler().getItems()) {
					this.leftColumns.add(new GrilleColumn(column.getId(), column.getName(), column.getType(), null, null));
				}
			}
			else if(model.getLeftGrid().isUniverse() && model.getLeftGrid().getGrille() != null) {
				for(GrilleColumn column : model.getLeftGrid().getGrille().getColumnListChangeHandler().getItems()) {
					this.leftColumns.add(new GrilleColumn(column.getId(), column.getName(), column.getType(), column.getDimensionId(), column.getDimensionName()));
				}
			}
		}
		if(model.getRigthGrid() != null) {
			if(model.getRigthGrid().isUnion() && model.getRigthGrid().getGrid() != null) {
				for(UnionGridColumn column : model.getRigthGrid().getGrid().getColumnListChangeHandler().getItems()) {
					this.rightColumns.add(new GrilleColumn(column.getId(), column.getName(), column.getType(), null, null));
				}
			}
			else if(model.getRigthGrid().isUniverse() && model.getRigthGrid().getGrille() != null) {
				for(GrilleColumn column : model.getRigthGrid().getGrille().getColumnListChangeHandler().getItems()) {
					this.rightColumns.add(new GrilleColumn(column.getId(), column.getName(), column.getType(), column.getDimensionId(), column.getDimensionName()));
				}
			}
		}		
	}
	

}

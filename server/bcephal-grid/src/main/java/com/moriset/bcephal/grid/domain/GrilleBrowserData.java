package com.moriset.bcephal.grid.domain;

import com.moriset.bcephal.domain.BrowserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class GrilleBrowserData extends BrowserData {

	private String status;
	private boolean editable;
	private Boolean published;
	
	public GrilleBrowserData(Grille grid) {
		super(grid);
		status = grid.getStatus().name();
		editable = grid.isEditable();
		published = grid.getPublished();
	}
	
	public GrilleBrowserData(MaterializedGrid grid) {
		super(grid);
		editable = grid.isEditable();
		published = grid.isPublished();
	}
}

package com.moriset.bcephal.dashboard.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.grid.domain.AbstractSmartGrid;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DashboardEditorData extends EditorData<Dashboard> {
	
	private List<AbstractSmartGrid<?>> grids;
	
	public DashboardEditorData() {
		super();
		grids = new ArrayList<>(0);
	}
	
	public DashboardEditorData(EditorData<Dashboard> data) {
		super(data);
		grids = new ArrayList<>(0);
	}
}

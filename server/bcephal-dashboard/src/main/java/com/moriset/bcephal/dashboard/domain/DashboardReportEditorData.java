package com.moriset.bcephal.dashboard.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DashboardReportEditorData extends EditorData<DashboardReport> {

	private List<Nameable> matGrids;
	
	public DashboardReportEditorData() {
		super();
		matGrids = new ArrayList<>(0);
	}
	
	public DashboardReportEditorData(EditorData<DashboardReport> data) {
		super(data);
		matGrids = new ArrayList<>(0);
	}
}

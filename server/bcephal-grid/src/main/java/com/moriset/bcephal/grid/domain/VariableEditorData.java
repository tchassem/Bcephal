package com.moriset.bcephal.grid.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.Variable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class VariableEditorData extends EditorData<Variable> {

	public List<Nameable> grids;
	
	private List<AbstractSmartGrid<?>> smartGrids;
	
	// private List<String> dateFormats;
	
	public VariableEditorData() {
		super();
		smartGrids = new ArrayList<>(0);
	}
	
	public VariableEditorData(EditorData<Variable> data) {
		super(data);
		smartGrids = new ArrayList<>(0);
	}

}

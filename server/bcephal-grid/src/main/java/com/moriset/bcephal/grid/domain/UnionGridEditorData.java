package com.moriset.bcephal.grid.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UnionGridEditorData extends EditorData<UnionGrid> {

	private List<AbstractSmartGrid<?>> grids;
	
	public UnionGridEditorData() {
		super();
		grids = new ArrayList<>(0);
	}
	
	public UnionGridEditorData(EditorData<UnionGrid> data) {
		super(data);
		grids = new ArrayList<>(0);
	}
	
}

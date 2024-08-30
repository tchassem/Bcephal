package com.moriset.bcephal.domain.dimension;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class CalculatedMeasureEditorData extends EditorData<CalculatedMeasure> {

	private List<Nameable> matGrids;
	
	public CalculatedMeasureEditorData() {
		super();
		matGrids = new ArrayList<>(0);
	}
	
	public CalculatedMeasureEditorData(EditorData<CalculatedMeasure> data) {
		super(data);
		matGrids = new ArrayList<>(0);
	}
	
}

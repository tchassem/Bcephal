/**
 * 
 */
package com.moriset.bcephal.planification.domain.routine;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.grid.domain.AbstractSmartGrid;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class TransformationRoutineEditorData extends EditorData<TransformationRoutine> {

	public List<Nameable> grids;
	
	private List<AbstractSmartGrid<?>> smartGrids;
	
	private List<String> dateFormats;
	
	public TransformationRoutineEditorData() {
		super();
		smartGrids = new ArrayList<>(0);
	}
	
	public TransformationRoutineEditorData(EditorData<TransformationRoutine> data) {
		super(data);
		smartGrids = new ArrayList<>(0);
	}
	
}

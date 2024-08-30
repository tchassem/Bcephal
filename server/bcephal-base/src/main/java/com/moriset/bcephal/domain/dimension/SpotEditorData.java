/**
 * 
 */
package com.moriset.bcephal.domain.dimension;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class SpotEditorData extends EditorData<Spot>{

	public List<Nameable> grids;
	private List<Nameable> matGrids;
	
	public SpotEditorData() {
		super();
		matGrids = new ArrayList<>(0);
	}
	
	public SpotEditorData(EditorData<Spot> data) {
		super(data);
		matGrids = new ArrayList<>(0);
	}
}

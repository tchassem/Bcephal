/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class JoinEditorData extends EditorData<Join> {

	private List<Nameable> routines;
	
	private List<Nameable> sequences;
	
	private List<AbstractSmartGrid<?>> grids;
	
	public JoinEditorData() {
		super();
		routines = new ArrayList<>(0);
		grids = new ArrayList<>(0);
	}
	
	public JoinEditorData(EditorData<Join> data) {
		super(data);
		routines = new ArrayList<>(0);
		sequences = new ArrayList<>(0);
		grids = new ArrayList<>(0);
	}
	
}

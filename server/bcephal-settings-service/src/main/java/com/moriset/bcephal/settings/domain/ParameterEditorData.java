/**
 * 
 */
package com.moriset.bcephal.settings.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.parameter.Parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ParameterEditorData extends EditorData<Parameter> {

	private List<Nameable> grids;

	private List<Nameable> sequences;

	private List<Nameable> billtemplates;
	
	private List<ParameterGroup> parameterGroups;
	
	private List<Nameable> matGrids;

	public ParameterEditorData() {
		grids = new ArrayList<>(0);
		matGrids = new ArrayList<>(0);
		sequences = new ArrayList<>(0);
		billtemplates = new ArrayList<>(0);
	}

}

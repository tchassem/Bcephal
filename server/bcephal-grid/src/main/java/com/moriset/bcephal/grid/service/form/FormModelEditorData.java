/**
 * 
 */
package com.moriset.bcephal.grid.service.form;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.grid.domain.AbstractSmartGrid;
import com.moriset.bcephal.grid.domain.form.FormModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FormModelEditorData extends EditorData<FormModel> {

	private List<AbstractSmartGrid<?>> grids;
	
	public List<Nameable> sequences;

	public FormModelEditorData() {
		super();
		grids = new ArrayList<>(0);
	}
	
}

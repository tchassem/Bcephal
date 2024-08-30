package com.moriset.bcephal.grid.domain.form;

import com.moriset.bcephal.domain.EditorDataFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class FormEditorDataFilter extends EditorDataFilter {

	private Object[] Datas;

}

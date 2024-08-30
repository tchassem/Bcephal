/**
 * 
 */
package com.moriset.bcephal.grid.service.form;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.grid.domain.form.Form;
import com.moriset.bcephal.grid.domain.form.FormModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FormEditorData extends EditorData<Form> {

	private FormModel  model;

}

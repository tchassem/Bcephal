/**
 * 
 */
package com.moriset.bcephal.grid.domain.form;

import com.moriset.bcephal.domain.EditorData;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FormDataEditorData extends EditorData<FormData> {

	private FormModel formModel;

}

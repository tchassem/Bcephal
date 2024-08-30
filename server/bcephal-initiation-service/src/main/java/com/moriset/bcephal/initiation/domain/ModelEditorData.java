/**
 * 
 */
package com.moriset.bcephal.initiation.domain;

import com.moriset.bcephal.domain.ListChangeHandler;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class ModelEditorData {

	private Model item;

	private ListChangeHandler<Model> allModels = new ListChangeHandler<>();

}

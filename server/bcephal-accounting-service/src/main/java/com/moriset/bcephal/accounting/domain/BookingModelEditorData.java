/**
 * 
 */
package com.moriset.bcephal.accounting.domain;

import java.util.List;

import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.utils.SlimObject;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class BookingModelEditorData extends EditorData<BookingModel> {

	private List<SlimObject> repositoryColumns;

}

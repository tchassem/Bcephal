/**
 * 
 */
package com.moriset.bcephal.grid.domain.form;

import java.util.HashMap;
import java.util.Map;

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.MainObject;
import com.moriset.bcephal.domain.Persistent;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FormData extends MainObject {

	private static final long serialVersionUID = -322354323554458862L;

	private Long id;
	private Long modelOid;
	private Long formModelId;
	private Long gridId;
	private int position;
	private boolean validated;

	private Map<Long, FormDataValue> datas;

	private Map<Long, ListChangeHandler<FormData>> subGridDatas;

	public FormData() {
		datas = new HashMap<>();
		subGridDatas = new HashMap<>();
	}

	@Override
	public Persistent copy() {
		return null;
	}

}

/**
 * 
 */
package com.moriset.bcephal.security.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ClientEditorData extends EditorData<Client> {

	private List<String> functionalities;

	public ClientEditorData() {
		functionalities = new ArrayList<>(0);
	}
	
}
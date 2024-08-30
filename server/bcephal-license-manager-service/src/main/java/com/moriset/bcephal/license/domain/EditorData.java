/**
 * 
 */
package com.moriset.bcephal.license.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class EditorData<P> {

	private P item;
	
	private List<String> functionalities;

	public EditorData() {
		functionalities = new ArrayList<>(0);
	}
	
	public EditorData(EditorData<P> data) {		
		item = data.getItem();
		functionalities = new ArrayList<>(0);
	}

}

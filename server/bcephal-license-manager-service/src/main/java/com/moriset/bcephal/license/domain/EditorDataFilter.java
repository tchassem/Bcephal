/**
 * 
 */
package com.moriset.bcephal.license.domain;

import lombok.Data;

@Data
public class EditorDataFilter {
	
	private Long dataSourceId;
	private Long id;
	private Long secondId;
	private boolean newData;
	
	public EditorDataFilter() {
		this.newData = false;
	}
	
	public EditorDataFilter(Long id) {
		this.id = id;
		this.newData = false;
	}

}

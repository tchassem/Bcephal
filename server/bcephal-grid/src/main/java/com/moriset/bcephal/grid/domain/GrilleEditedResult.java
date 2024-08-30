/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import lombok.Data;

/**
 * @author Moriset
 *
 */
@Data
public class GrilleEditedResult {

	private boolean isError;

	private String error;

	private Object[] datas;
	
}

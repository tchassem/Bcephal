/**
 * 
 */
package com.moriset.bcephal.grid.domain.form;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * @author Moriset
 *
 */
@Data
public class FormButtonActionData {

	private Form form;
	
	private Long modelId;
	
	private Long buttonId;
	
	@JsonIgnore
	private FormModel model;
	
	@JsonIgnore
	private FormModelButton button;
	
	
}

package com.moriset.bcephal.settings.domain;


import lombok.Data;

/**
 * @author MORISET-6
 *
 */
@Data
public class ParameterGroupItem {

	private String code;

	private String type;

	private String parentCode;
	
	
	public ParameterGroupItem() {
		// TODO Auto-generated constructor stub
	}
	
	public ParameterGroupItem(String code, String type, String parentCode){
		this();
		this.code = code;
	    this.type = type;
	    this.parentCode = parentCode;
	 }
	
}

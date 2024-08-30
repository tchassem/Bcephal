/**
 * 
 */
package com.moriset.bcephal.settings.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author MORISET-6
 *
 */
@Data
public class ParameterGroup {

	private String code;

	private String name;
	
	private Boolean canBeCreateAutomatically;
	
	private List<ParameterGroupItem> ParameterGroupItems;
	
	public List<ParameterGroup> subGroups ;
	
	public ParameterGroup() {
		subGroups = new ArrayList<ParameterGroup>(0);
		ParameterGroupItems = new ArrayList<ParameterGroupItem>(0);
	}
	
	public ParameterGroup(String code) {
		this();
		this.code = code;
	}
	
	public ParameterGroup(String code, String name) {
		this();
		this.code = code;
		this.name = name;
	}
	
	public ParameterGroup(String code, String name, Boolean canBeCreateAutomatically) {
		this();
		this.code = code;
		this.name = name;
		this.canBeCreateAutomatically = canBeCreateAutomatically;
	}

}

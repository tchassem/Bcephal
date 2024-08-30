/**
 * 
 */
package com.moriset.bcephal.accounting.domain;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class Account {

	private String number;
	private String name;
	

	public Account(String id, String name) {
		super();
		this.number = id;
		this.name = name;
	}
	
	
	
}

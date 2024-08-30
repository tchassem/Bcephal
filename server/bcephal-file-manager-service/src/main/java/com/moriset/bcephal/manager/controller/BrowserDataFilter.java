/**
 * 
 */
package com.moriset.bcephal.manager.controller;

import lombok.Data;

/**
 * @author MORISET-004
 *
 */
@Data
public class BrowserDataFilter {
	private long pageSize;
	private long page;
	private String category;
	private String criteria;
	public BrowserDataFilter(){
		pageSize = 25;
	}

}

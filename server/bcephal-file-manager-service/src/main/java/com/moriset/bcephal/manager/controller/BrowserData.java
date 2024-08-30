/**
 * 
 */
package com.moriset.bcephal.manager.controller;

/**
 * @author MORISET-004
 *
 */
public class BrowserData {

	public long id;
	public String name;
	public String path;
	
	public BrowserData(long id, String name, String path) {
		this.name = name;
		this.path = path;
		this.id = id;
	}
}

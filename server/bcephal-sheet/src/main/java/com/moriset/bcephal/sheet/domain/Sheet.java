/**
 * 
 */
package com.moriset.bcephal.sheet.domain;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class Sheet {

	private int index;

	private String name;

	private Sheet() {
	}

	private Sheet(int index, String name) {
		this();
		this.index = index;
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

}

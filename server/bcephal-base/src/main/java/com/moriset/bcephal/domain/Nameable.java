/**
 * 
 */
package com.moriset.bcephal.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Joseph Wambo
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Nameable extends Persistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3852317240732187363L;

	private Long id;

	private String name;
	
	private String code;
	
	public Nameable (Long id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public Persistent copy() {
		return null;
	}
}

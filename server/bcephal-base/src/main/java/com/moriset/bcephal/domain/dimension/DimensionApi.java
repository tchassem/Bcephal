/**
 * 
 */
package com.moriset.bcephal.domain.dimension;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Moriset
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DimensionApi {

	private String name;

	private Long parent;

	private Long entity;

	@JsonIgnore
	private DimensionType type;

	@Override
	public String toString() {
		return name;
	}

}

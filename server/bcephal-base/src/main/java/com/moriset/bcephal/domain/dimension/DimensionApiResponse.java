/**
 * 
 */
package com.moriset.bcephal.domain.dimension;

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
public class DimensionApiResponse {

	private Long id;

	private String name;

	public Dimension AsDimension(DimensionType type) {
		if (type.isMeasure()) {
			return new Measure(id, name);
		}
		if (type.isPeriod()) {
			return new Period(id, name);
		}
		if (type.isAttribute()) {
			return new Attribute(id, name);
		}
		return null;
	}
}

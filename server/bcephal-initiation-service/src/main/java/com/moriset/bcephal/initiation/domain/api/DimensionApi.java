package com.moriset.bcephal.initiation.domain.api;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DimensionApi {

	private String name;

	private Long parent;

	@Override
	public String toString() {
		return name;
	}
}

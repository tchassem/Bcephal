package com.moriset.bcephal.grid.domain;

import com.moriset.bcephal.domain.dimension.DimensionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnionGridCreateColumnData {

	private Long gridId;
	private String columnName;
	private DimensionType dimensionType;
	
	
}

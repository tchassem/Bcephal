package com.moriset.bcephal.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
@Embeddable
public class TransformationRoutineRanking {
	
	private boolean ascending;
	
	private Long dimensionId;
	
	@Enumerated(EnumType.STRING)
	private DimensionType dimensionType;
	
	public TransformationRoutineRanking() {
		dimensionType = DimensionType.ATTRIBUTE;
	}
	
	public DimensionType getDimensionType(){
		if(dimensionType == null) {
			dimensionType = DimensionType.ATTRIBUTE;
		}
		return dimensionType;
	}
	
	@JsonIgnore
	public String getUniverseTableColumnName(DataSourceType dataSourceType) {
		if(dimensionType.isMeasure()) {
			return new Measure(dimensionId, null, dataSourceType, null).getUniverseTableColumnName();
		}
		if(dimensionType.isPeriod()) {
			return new Period(dimensionId, null, dataSourceType, null).getUniverseTableColumnName();
		}
		return new Attribute(dimensionId, null, dataSourceType, null).getUniverseTableColumnName();
	}

}

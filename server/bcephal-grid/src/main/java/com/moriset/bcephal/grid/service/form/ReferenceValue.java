package com.moriset.bcephal.grid.service.form;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.dimension.DimensionType;

import lombok.Data;

@Data
public class ReferenceValue {
	
	private DimensionType dimensionType;
	private String stringValue;
	private BigDecimal decimalValue;
    
	private Date dateValue;
    
    
    @JsonIgnore
    public boolean isAttribute() {
    	return dimensionType == DimensionType.ATTRIBUTE;
    }
    
    @JsonIgnore
    public boolean isMeasure() {
    	return dimensionType == DimensionType.MEASURE;
    }
    
    @JsonIgnore
    public boolean isPeriod() {
    	return dimensionType == DimensionType.PERIOD;
    }

	public void setValue(Object item) {
		if(isAttribute()) {
			stringValue = (String)item;
		}
		else if(isMeasure()) {
			decimalValue = new BigDecimal(((Number)item).doubleValue());
		}
		else if(isPeriod()) {
			dateValue = (Date)item;
		}
	}

}

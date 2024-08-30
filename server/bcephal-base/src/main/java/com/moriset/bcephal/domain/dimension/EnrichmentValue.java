/**
 * 
 */
package com.moriset.bcephal.domain.dimension;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.filters.PeriodValue;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class EnrichmentValue {

	private DimensionType dimensionType;

	private Long dimensionId;

	private String side;

	private String stringValue;

	private BigDecimal decimalValue;

	private PeriodValue dateValue;

	public Object getValue() throws Exception {
		if (isAttribute()) {
			return stringValue;
		} else if (isMeasure()) {
			return decimalValue != null ? decimalValue : BigDecimal.ZERO;
		} else if (isPeriod()) {
			return dateValue != null ? dateValue.buildDynamicDate() : null;
		}
		return null;
	}

	@JsonIgnore
	public boolean isAttribute() {
		return getDimensionType() != null && getDimensionType().isAttribute();
	}

	@JsonIgnore
	public boolean isMeasure() {
		return getDimensionType() != null && getDimensionType().isMeasure();
	}

	@JsonIgnore
	public boolean isPeriod() {
		return getDimensionType() != null && getDimensionType().isPeriod();
	}

	@JsonIgnore
	public String getUniverseTableColumnName() {
		if (isAttribute()) {
			return new Attribute(getDimensionId()).getUniverseTableColumnName();
		} else if (isMeasure()) {
			return new Measure(getDimensionId()).getUniverseTableColumnName();
		} else if (isPeriod()) {
			return new Period(getDimensionId()).getUniverseTableColumnName();
		}
		return null;
	}
	
	@JsonIgnore
	public boolean isValid() {
		return getDimensionType() != null && getDimensionId() != null;
	}
	
	@JsonIgnore
	public boolean hasNullValue() {
		if(getDimensionType() == null) {
			dimensionType = DimensionType.ATTRIBUTE;
		}
		if(isAttribute()) {
			return stringValue == null;
		}
		else if(isMeasure()) {
			return false;
		}
		else if(isPeriod()) {
			return dateValue.buildDynamicDate() == null;
		}
		return false;
	}

	
}

/**
 * 
 */
package com.moriset.bcephal.domain.expression;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.PeriodValue;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class ExpressionValue {

	private ExpressionValueType type;

	private BigDecimal decimalValue;

	private PeriodValue periodValue;

	private String stringValue;

	private Long idValue;

	private Long dimensionId;

	private DimensionType dimensionType;

	@JsonIgnore
	public boolean isColumn() {
		return type == ExpressionValueType.COLUMN;
	}

	@JsonIgnore
	public boolean isLoop() {
		return type == ExpressionValueType.LOOP;
	}

	@JsonIgnore
	public boolean isSpot() {
		return type == ExpressionValueType.SPOT;
	}

	@JsonIgnore
	public boolean isNoCondition() {
		return type == ExpressionValueType.NO;
	}

	@JsonIgnore
	public boolean isNull() {
		return type == ExpressionValueType.NULL;
	}

	@JsonIgnore
	public Object getValue() {
		if (isNoCondition()) {
			return null;
		}
		if (isNull()) {
			return null;
		}
		if (dimensionType != null) {
			if (dimensionType.isAttribute()) {
				return stringValue;
			}
			if (dimensionType.isMeasure()) {
				return decimalValue;
			}
			if (dimensionType.isPeriod()) {
				return periodValue.buildDynamicDate();
			}
		}
		return null;
	}

}

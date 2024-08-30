/**
 * 
 */
package com.moriset.bcephal.grid.domain.form;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.utils.JsonDateDeserializer;
import com.moriset.bcephal.utils.JsonDateSerializer;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class FormDataValue {

	private BigDecimal decimalValue;

	private String stringValue;

	private String stringLinkedValue;

	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateValue;

	public Object getValue(FormModelField field) {
		if (field.isAttribute()) {
			return stringValue;
		}
		if (field.isMeasure()) {
			return decimalValue;
		}
		if (field.isPeriod()) {
			return dateValue;
		}
		return null;
	}

	public Object setValue(FormModelField field, Object value) {
		if (field.isAttribute()) {
			stringValue = value != null ? value.toString() : null;
		} else if (field.isMeasure()) {
			decimalValue = (BigDecimal) value;
		} else if (field.isPeriod()) {
			dateValue = (Date) value;
		}
		return null;
	}

}

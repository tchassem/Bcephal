package com.moriset.bcephal.license.domain;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.utils.JsonDateDeserializer;
import com.moriset.bcephal.utils.JsonDateSerializer;

import lombok.Data;

@Data
public class LicenseParameterValue {
	
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	private BigDecimal numericValue;
	
	private String stringValue;
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateValue;
	
	
	public void setValue(String value, ParameterType type) throws Exception {
		if(type == ParameterType.DATE) {
			SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
			dateValue = StringUtils.hasText(value) ? format.parse(value) : null;
		}
		else if(type == ParameterType.NUMERIC) {
			numericValue = StringUtils.hasText(value) ? new BigDecimal(value) : null;
		}
		else if(type == ParameterType.STRING) {
			stringValue = value;
		}
	}
	
	public Object getValue(ParameterType type) {
		if(type == ParameterType.DATE) {
			return dateValue;
		}
		else if(type == ParameterType.NUMERIC) {
			return numericValue;
		}
		else if(type == ParameterType.STRING) {
			return stringValue;
		}
		return null;
	}
	
	public String getValueAsString(ParameterType type) {
		if(type == ParameterType.DATE) {
			SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
			return dateValue != null ? format.format(dateValue) : null;
		}
		else if(type == ParameterType.NUMERIC) {
			return numericValue != null ? numericValue.toPlainString() : null;
		}
		else if(type == ParameterType.STRING) {
			return stringValue;
		}
		return null;
	}
	
}

package com.moriset.bcephal.api.domain;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;

@Data
public class ApiFilter {
	
	@Schema(description = "Dimension type", defaultValue = "ATTRIBUTE", allowableValues = {"ATTRIBUTE","MEASURE","PERIOD"}, requiredMode = RequiredMode.REQUIRED)
	String dimensionType;
	
	@Schema(description = "Dimension name", defaultValue = "", requiredMode = RequiredMode.REQUIRED)
	String dimensionName;
	
	@Schema(description = "Operator", defaultValue = "=", requiredMode = RequiredMode.REQUIRED)
	String operator;
	
	@Schema(description = "Attribute value", example = "", requiredMode = RequiredMode.NOT_REQUIRED, type = "String")
	String stringValue;
	
	@Schema(description = "Measure value", example = "100.05", requiredMode = RequiredMode.NOT_REQUIRED)
	BigDecimal decimalValue;
	
	@Schema(description = "Period value", example = "2024-05-20", requiredMode = RequiredMode.NOT_REQUIRED)	
	Date dateValue;
}

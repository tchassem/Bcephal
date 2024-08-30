package com.moriset.bcephal.license.domain;

import java.util.Date;

import lombok.Data;

@Data
public class ParameterValue {

	private Date dateValue;
	
	private Number numericValue;
	
	private String stringValue;
	
}

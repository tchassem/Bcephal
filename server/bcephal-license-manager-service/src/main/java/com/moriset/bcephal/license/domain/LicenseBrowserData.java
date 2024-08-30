package com.moriset.bcephal.license.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LicenseBrowserData extends BrowserData {

	private String code;
	private String productName;
	private int days;
	private LicenseValidityType validityType;
	
	public LicenseBrowserData(License license) {
		super(license);
		setCode(license.getCode());
		setProductName(license.getProductName());
		setDays(license.getDays());
		setValidityType(license.getValidityType());
	}
	
}

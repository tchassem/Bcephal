package com.moriset.bcephal.billing.domain;

import com.moriset.bcephal.domain.BrowserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class BillTemplateBrowserData extends BrowserData {

	private String code;
		
	private String repository;
	
	
	public BillTemplateBrowserData(BillTemplate template) {
		super(template);
		this.code = template.getCode();
		this.repository = template.getRepository();
	}
}

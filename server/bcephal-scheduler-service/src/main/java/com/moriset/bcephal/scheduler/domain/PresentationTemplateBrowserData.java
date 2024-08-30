package com.moriset.bcephal.scheduler.domain;

import com.moriset.bcephal.domain.BrowserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class PresentationTemplateBrowserData extends BrowserData {

	private String code;
		
	private String repository;	

	private boolean hasHeader;
	
	private boolean hasFooter;
	
	public PresentationTemplateBrowserData(PresentationTemplate template) {
		super(template);
		this.code = template.getCode();
		this.repository = template.getRepository();
		this.hasHeader = template.isHasHeader();
		this.hasFooter = template.isHasFooter();
	}

}

package com.moriset.bcephal.scheduler.domain;

import com.moriset.bcephal.domain.BrowserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class PresentationBrowserData  extends BrowserData {

	private String code;
		
	private String repository;	

	private String operationCode;
	
	public PresentationBrowserData(Presentation presentation) {
		super(presentation);
		this.code = presentation.getCode();
		this.repository = presentation.getRepository();
		this.operationCode = presentation.getOperationCode();
	}
}

package com.moriset.bcephal.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DocumentBrowserData extends BrowserData {

	private String subjectType;
	
	private String subjectName;
	
	private String code;
	
	private String category;
	
	private String extension;
	
	private String username;
	
	public DocumentBrowserData(Document document) {
		super(document);
		this.subjectType = document.getSubjectType();
		this.subjectName = document.getSubjectName();
		this.code = document.getCode();
		this.category = document.getCategory();
		this.extension = document.getExtension();
		this.username = document.getUsername();
	}
	
	
	
}

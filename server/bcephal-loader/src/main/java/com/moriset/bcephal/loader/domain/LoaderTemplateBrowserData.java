/**
 * 3 juin 2024 - LoaderTemplateBrowserData.java
 *
 */
package com.moriset.bcephal.loader.domain;

import com.moriset.bcephal.domain.BrowserData;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Emmanuel Emmeni
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class LoaderTemplateBrowserData extends BrowserData {

	private String code;
		
	private String repository;
	
	
	public LoaderTemplateBrowserData(LoaderTemplate template) {
		super(template);
		this.code = template.getCode();
		this.repository = template.getRepository();
	}

}

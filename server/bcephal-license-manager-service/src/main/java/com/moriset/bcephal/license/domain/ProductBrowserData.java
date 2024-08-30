package com.moriset.bcephal.license.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProductBrowserData extends BrowserData {

	private String code;
	
	private String version;
	
	public ProductBrowserData(Product product) {
		super(product);
		setCode(product.getCode());
		setVersion(product.getVersion());
	}
	
}

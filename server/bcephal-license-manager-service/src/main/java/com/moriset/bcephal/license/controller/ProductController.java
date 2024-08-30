package com.moriset.bcephal.license.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.license.domain.Product;
import com.moriset.bcephal.license.domain.ProductBrowserData;
import com.moriset.bcephal.license.service.ProductService;

@RestController
@RequestMapping("/license-manager/product")
public class ProductController extends BaseController<Product, ProductBrowserData> {

	@Autowired
	ProductService productService;

	@Override
	protected ProductService getService() {
		return productService;
	}
}

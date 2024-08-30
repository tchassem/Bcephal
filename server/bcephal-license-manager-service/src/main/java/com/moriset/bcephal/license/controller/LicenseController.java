package com.moriset.bcephal.license.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.license.domain.License;
import com.moriset.bcephal.license.domain.LicenseBrowserData;
import com.moriset.bcephal.license.service.LicenseService;

@RestController
@RequestMapping("/license-manager/license")
public class LicenseController extends BaseController<License, LicenseBrowserData> {

	@Autowired
	LicenseService licenseService;

	@Override
	protected LicenseService getService() {
		return licenseService;
	}
}

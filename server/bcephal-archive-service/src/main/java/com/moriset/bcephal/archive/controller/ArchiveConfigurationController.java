package com.moriset.bcephal.archive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.archive.domain.ArchiveConfiguration;
import com.moriset.bcephal.archive.service.ArchiveConfigurationService;
import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.BrowserData;

@RestController
@RequestMapping("/archive/configuration")
public class ArchiveConfigurationController extends BaseController<ArchiveConfiguration, BrowserData> {

	@Autowired
	ArchiveConfigurationService archiveConfigurationService;

	@Override
	protected ArchiveConfigurationService getService() {
		return archiveConfigurationService;
	}
}

package com.moriset.bcephal.sourcing.grid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.BGroup;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.service.BGroupService;

@RestController
@RequestMapping("/b-group")
public class BGroupController extends BaseController<BGroup, BrowserData> {

	@Autowired
	BGroupService bGroupService;

	@Override
	protected BGroupService getService() {
		return bGroupService;
	}

}

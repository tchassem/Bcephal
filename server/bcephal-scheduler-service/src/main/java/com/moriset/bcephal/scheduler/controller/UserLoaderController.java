package com.moriset.bcephal.scheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.loader.domain.UserLoader;
import com.moriset.bcephal.loader.domain.UserLoaderBrowserData;
import com.moriset.bcephal.scheduler.service.UserLoaderService;

@RestController
@RequestMapping("/scheduler/user-loader")

public class UserLoaderController extends BaseController<UserLoader, UserLoaderBrowserData>{
	
	@Autowired
	UserLoaderService userLoaderService;
	

	@Override
	protected UserLoaderService getService() {
		return userLoaderService;
	}

	
}

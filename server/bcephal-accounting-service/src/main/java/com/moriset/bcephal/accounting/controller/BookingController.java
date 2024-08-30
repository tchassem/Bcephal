package com.moriset.bcephal.accounting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.accounting.domain.Posting;
import com.moriset.bcephal.accounting.domain.PostingBrowserData;
import com.moriset.bcephal.accounting.service.PostingService;
import com.moriset.bcephal.controller.BaseController;

@RestController
@RequestMapping("/accounting/booking")
public class BookingController extends BaseController<Posting, PostingBrowserData> {

	@Autowired
	PostingService postingService;
	
	@Override
	protected PostingService getService() {
		return postingService;
	}
}

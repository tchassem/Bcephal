package com.moriset.bcephal.accounting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.accounting.domain.BookingModel;
import com.moriset.bcephal.accounting.service.BookingModelService;
import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.BrowserData;

@RestController
@RequestMapping("/accounting/booking-model")
public class BookingModelController extends BaseController<BookingModel, BrowserData> {

	@Autowired
	BookingModelService bookingModelService;
	
	@Override
	protected BookingModelService getService() {
		return bookingModelService;
	}
}

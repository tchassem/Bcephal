/**
 * 
 */
package com.moriset.bcephal.billing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.billing.domain.BillingModelLog;
import com.moriset.bcephal.billing.service.BillingModelLogService;
import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.BrowserData;

/**
 * @author MORISET-6
 
 */

@RestController
@RequestMapping("/billing/current_run_status")
public class BillingModelLogController extends BaseController<BillingModelLog, BrowserData> {

	@Autowired
	BillingModelLogService billingModelLogService;

	@Override
	protected BillingModelLogService getService() {
		// TODO Auto-generated method stub
		return billingModelLogService;
	}
	

}

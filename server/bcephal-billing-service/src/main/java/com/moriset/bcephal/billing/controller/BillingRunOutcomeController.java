/**
 * 
 */
package com.moriset.bcephal.billing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.billing.domain.BillingRunOutcome;
import com.moriset.bcephal.billing.domain.BillingRunOutcomeBrowserData;
import com.moriset.bcephal.billing.service.BillingRunOutcomeService;
import com.moriset.bcephal.controller.BaseController;

/**
 * @author MORISET-6
 *
 */
@RestController
@RequestMapping("/billing/run-outcome")
public class BillingRunOutcomeController extends BaseController<BillingRunOutcome, BillingRunOutcomeBrowserData> {

	@Autowired
	BillingRunOutcomeService billRunOutcomeService;
	
	@Override
	protected BillingRunOutcomeService getService() {
		// TODO Auto-generated method stub
		return billRunOutcomeService;
	}

}

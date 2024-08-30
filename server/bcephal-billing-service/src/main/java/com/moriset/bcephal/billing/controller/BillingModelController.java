package com.moriset.bcephal.billing.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.billing.domain.BillingModel;
import com.moriset.bcephal.billing.domain.BillingTemplateLabel;
import com.moriset.bcephal.billing.service.BillTemplateService;
import com.moriset.bcephal.billing.service.BillingModelService;
import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/billing/model")
@Slf4j
public class BillingModelController extends BaseController<BillingModel, BrowserData> {
	
	@Autowired
	BillingModelService billingModelService;
	
	@Autowired
	BillTemplateService billTemplateService;

	@Override
	protected BillingModelService getService() {
		return billingModelService;
	}
	
	@GetMapping("/template/{templateCode}")
	public ResponseEntity<?> getTemplateLabels(@PathVariable("templateCode") String templateCode) {
		
		log.debug("Call of get template model");
		try {
			List<BillingTemplateLabel> labels = new ArrayList<>();// billTemplateService.getTemplateLabels(templateCode);
			log.debug("Found : {}", labels.size());
			return ResponseEntity.status(HttpStatus.OK).body(labels);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving model labels ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
}

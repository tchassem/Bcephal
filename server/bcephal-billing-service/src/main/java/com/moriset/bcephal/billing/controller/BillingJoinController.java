/**
 * 
 */
package com.moriset.bcephal.billing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.billing.service.BillingJoinService;
import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.grid.domain.GridItem;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinBrowserData;
import com.moriset.bcephal.grid.service.JoinFilter;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@RestController
@RequestMapping("/billing/join")
@Slf4j
public class BillingJoinController extends BaseController<Join, JoinBrowserData> {

	@Autowired
	BillingJoinService billingJoinService;
	
	
	@Override
	protected BillingJoinService getService() {
		return billingJoinService;
	}
	
	@PostMapping("/rows2")
	public ResponseEntity<?> searchRows(@RequestBody JoinFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of searchRows");
		try {
			BrowserDataPage<GridItem> page = getService().searchRows2(filter, locale);
			log.debug("Found : {}", page.getCurrentPage());
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while search rows by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.search.rows.by.filter", new Object[] { filter },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
}

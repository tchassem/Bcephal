/**
 * 
 */
package com.moriset.bcephal.billing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.billing.domain.Invoice;
import com.moriset.bcephal.billing.domain.InvoiceBrowserData;
import com.moriset.bcephal.billing.service.InvoiceModificationManager;
import com.moriset.bcephal.billing.service.InvoiceService;
import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.MailSendingStatus;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-6
 *
 */

@Slf4j
@RestController
@RequestMapping("/billing/invoice")
public class InvoiceController extends BaseController<Invoice, InvoiceBrowserData> {

	@Autowired
	InvoiceService invoiceService;
	
	@Autowired
	InvoiceModificationManager modificationManager;
	
	@Override
	protected InvoiceService getService() {
		return invoiceService;
	}
	
	@GetMapping("file-id/{id}")
	public ResponseEntity<?> getById(@PathVariable("id") Long id,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of get invoice file id : {}", id);
		try {
			Invoice item = getService().getById(id);
			if(item != null)
			log.debug("Found : {}", item != null ? item.getFile() : "");
			return ResponseEntity.status(HttpStatus.OK).body(item != null ? item.getFile() : null);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving invoice file id : {}", id, e);
			String message = messageSource.getMessage("unable.to.retieve.invoice.file.id", new Object[] { id }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("/save")
	@Override
	public ResponseEntity<?> save(@RequestBody Invoice invoice, 
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale,
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId,
			JwtAuthenticationToken principal, HttpSession session,
			@RequestHeader HttpHeaders headers) {
		log.debug("Call of save invoice : {}", invoice);
		try {
			boolean isNew = invoice.getId() == null;
			modificationManager.modify(invoice, headers, locale);
			invoice = getService().getById((Long) invoice.getId());
			String rightLevel = isNew ? "CREATE" : "EDIT";
			getService().saveUserSessionLog(principal.getName(), client, projectCode, session.getId(), invoice.getId(),getFunctionalityCode(invoice.getId()) , rightLevel,profileId);
			log.trace("Invoice : {}", invoice);
			return ResponseEntity.status(HttpStatus.OK).body(invoice);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while save invoice : {}", invoice, e);
			String message = messageSource.getMessage("unable.to.save.invoice", new Object[] { invoice }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@PostMapping("change-send-status/{id}")
	public ResponseEntity<?> changeSendStatus(@PathVariable("id") Long id, 
			@RequestBody MailSendingStatus status,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale,
			@RequestHeader(RequestParams.BC_CLIENT) Long client,
			@RequestHeader(RequestParams.BC_PROJECT) String projectCode,
			@RequestHeader(name= RequestParams.BC_PROFILE,required = false) Long profileId,
			JwtAuthenticationToken principal, HttpSession session,
			@RequestHeader HttpHeaders headers) {
		log.debug("Call of change send status : {}", id);
		try {
			Invoice invoice = getService().getById(id);
			if(invoice == null) {
				throw new BcephalException("Invoice with id '" + id + "' not found!");
			}
			modificationManager.changeSendStatus(invoice, status, headers, locale);
			invoice = getService().getById((Long) invoice.getId());
			String rightLevel = "EDIT";
			getService().saveUserSessionLog(principal.getName(), client, projectCode, session.getId(), invoice.getId(),getFunctionalityCode(invoice.getId()) , rightLevel,profileId);
			log.trace("Invoice : {}", invoice);
			return ResponseEntity.status(HttpStatus.OK).body(invoice);			
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while changing send status id : {}", id, e);
			String message = messageSource.getMessage("Unable to change send mail status!", new Object[] { id }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
}

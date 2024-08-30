package com.moriset.bcephal.billing.service.action;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.billing.domain.BillingRunOutcome;
import com.moriset.bcephal.billing.domain.Invoice;
import com.moriset.bcephal.billing.service.BillingRunOutcomeService;
import com.moriset.bcephal.billing.service.InvoiceLogService;
import com.moriset.bcephal.billing.service.InvoiceRepositoryService;
import com.moriset.bcephal.billing.service.InvoiceService;
import com.moriset.bcephal.billing.service.batch.BillingContext;
import com.moriset.bcephal.billing.websocket.BillingRequest;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public abstract class BillingActionRunner {

	BillingRequest billingRequest;
	RunModes mode;
	TaskProgressListener listener;	
	String sessionId;
	String username = "B-CEPHAL";
	HttpHeaders httpHeaders;
	String projectCode;
	Long profileId;
	String language;
	Long client;	
	
	Locale locale = Locale.ENGLISH;
	boolean stop;
	
	BillingContext context;
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	InvoiceService invoiceService;
	
	@Autowired
	InvoiceRepositoryService invoiceRepositoryService;
	
	@Autowired
	InvoiceLogService invoiceLogService;
	
	@Autowired
	BillingRunOutcomeService billingRunOutcomeService;
	
	@Autowired
	ParameterRepository parameterRepository;
	
	@Transactional
	public int run() {
		try {			
			if(billingRequest == null) {
				throw new BcephalException("Billing model request is NULL!");
			}
			int count = billingRequest.getIds().size();
			if(count == 0) {
//				throw new BcephalException("Item list is empty!");
			}
			
			if(listener != null) {
				listener.createInfo(null, "");
				listener.start(count + 1);
			}
			context = buildContext();
			count = run(billingRequest.getIds());
//			for(Long id : billingRequest.getIds()) {
//				log.debug("Try to run item : {}", id);
//				if(listener != null) {
//					listener.nextStep(1);
//				}
//				if(listener != null) {
//					listener.createSubInfo(id, "");
//					listener.startSubInfo(10);
//				}
//				
//				if(billingRequest.isInvoice()) {
//					//Invoice invoice = invoiceService.getById(id);
//					Invoice invoice = getInvoice(id, billingRequest.isFromRepository());
//					run(invoice);
//				}
//				else if(billingRequest.isRunOutcome()) {
//					BillingRunOutcome outcome = billingRunOutcomeService.getById(id);
//					run(outcome);
//				}				
//			}	
			return count;
		}
		catch (Exception e) {
			log.error("", e);
			if(listener != null) {
				listener.error(e.getMessage(), false);
			}
		}	
		finally {
			if(listener != null) {
				listener.end();
			}
		}	
		return 0;
	}
	
	protected Invoice getInvoice(Long id, boolean fromRepository) {
		if(fromRepository) {
			Long invoiceId = invoiceRepositoryService.getInvoiceObjectId(id, Locale.ENGLISH);
			return invoiceService.getById(invoiceId);
		}
		else {
			return invoiceService.getById(id);
		}
	}
	
	protected int run(List<Long> ids) throws Exception {
		int count = 0;
		for(Long id : ids) {
			log.debug("Try to run item : {}", id);
			if(listener != null) {
				listener.nextStep(1);
			}
			if(listener != null) {
				listener.createSubInfo(id, "");
				listener.startSubInfo(10);
			}
			
			if(billingRequest.isInvoice()) {
				//Invoice invoice = invoiceService.getById(id);
				Invoice invoice = getInvoice(id, billingRequest.isFromRepository());
				count += run(invoice);
			}
			else if(billingRequest.isRunOutcome()) {
				BillingRunOutcome outcome = billingRunOutcomeService.getById(id);
				count = run(outcome);
			}				
		}	
		return count;
	}
	
	protected abstract int run(Invoice invoice) throws Exception;
	
	protected abstract int run(BillingRunOutcome outcome) throws Exception;
	
	protected abstract BillingContext buildContext() throws Exception;
	
	protected void checkSopped() throws BcephalException {
		if(stop) {
			throw new BcephalException("Stoppped by user!");
		}
	}
	
	public void stop() {
		stop = true;
	}
	
	
	protected Long getParameterLongValue(String code, ParameterType type) {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(code, type);
		return parameter != null ? parameter.getLongValue() : null;
	}
	
	protected String getParameterStringValue(String code, ParameterType type) {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(code, type);
		return parameter != null ? parameter.getStringValue() : null;
	}
}



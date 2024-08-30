/**
 * 
 */
package com.moriset.bcephal.billing.service.action;

import java.io.File;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.billing.domain.BillTemplate;
import com.moriset.bcephal.billing.domain.BillingRunOutcome;
import com.moriset.bcephal.billing.domain.Invoice;
import com.moriset.bcephal.billing.domain.InvoiceStatus;
import com.moriset.bcephal.billing.repository.BillTemplateRepository;
import com.moriset.bcephal.billing.service.BillTemplateService;
import com.moriset.bcephal.billing.service.BillingFileUploaderService;
import com.moriset.bcephal.billing.service.batch.BillingContext;
import com.moriset.bcephal.billing.service.batch.InvoicePrinter;
import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.parameter.IncrementalNumber;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.repository.filters.IncrementalNumberRepository;

import jakarta.persistence.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Service
@Data
@Slf4j
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@EqualsAndHashCode(callSuper=false)
public class BillingValidationRunner extends BillingActionRunner {
	
	@Autowired
	IncrementalNumberRepository sequenceRepository;
	
	@Autowired
	BillingFileUploaderService billlingFileUploaderService;
	
	@Autowired
	BillTemplateRepository billTemplateRepository;
	
	@Autowired
	BillTemplateService billTemplateService;
	
	@Value("${bcephal.language}")
	String defaultLanguage;
	
	
	
	@Override
	protected int run(Invoice invoice) throws Exception {
		log.debug("Try to validate invoice : {}", invoice.getReference());
		
		String oldReference = invoice.getReference();
		invoice.setStatus(InvoiceStatus.VALIDATED);
		invoice.setValidationDate(new Date());
		
		if(invoice.isUseValidationDateAsInvoiceDate()) {
			invoice.setBillingDate(invoice.getValidationDate());
		}
		
		if(!StringUtils.hasText(invoice.getValidationReference())) {
			IncrementalNumber sequence = invoice.getInvoiceValidationSequenceId() != null ? sequenceRepository.getReferenceById(invoice.getInvoiceValidationSequenceId()) : null;
			if(sequence != null) {
				invoice.setValidationReference(sequence.buildNextValue());
				sequence = sequenceRepository.save(sequence);
			}
			else {
				invoice.setValidationReference(invoice.getReference());
			}		
			invoice.setReference(invoice.getValidationReference());
		}
		
		String sql = context.getValidateInvoiceSQL();
		Query query = getEntityManager().createNativeQuery(sql);
		int index = 1;
		query.setParameter(index++, invoice.getReference());
		query.setParameter(index++, invoice.getStatus().toString());
		query.setParameter(index++, invoice.getReference());
		if(context.invoiceValidationDateId != null) {
			query.setParameter(index++, invoice.getValidationDate());
		}
		if(context.invoiceDateId != null) {
			query.setParameter(index++, invoice.getBillingDate());
		}
		query.setParameter(index++, invoice.getId());
		query.setParameter(index++, oldReference);
		query.setParameter(index++, invoice.isCreditNote() ? UniverseSourceType.CREDIT_NOTE.toString() : UniverseSourceType.INVOICE.toString());		
		query.executeUpdate();
		
		sql = context.getValidateInvoiceEventsSQL();
		query = getEntityManager().createNativeQuery(sql);
		index = 1;
		query.setParameter(index++, invoice.getReference());
		query.setParameter(index++, oldReference);
		query.setParameter(index++, invoice.getRunNumber());
		query.executeUpdate();
				
		if(StringUtils.hasText(invoice.getFileNameDescription())) {
			String fileName = invoice.buildFileName();
			invoice.setFileName(fileName);
			invoice.setName(fileName);
		}	
		
		if(/*getBillingRequest().isReprint() &&*/ invoice.isReprintPdfAtValidation()) {
			String fileName = invoice.buildFileName();
			if(!StringUtils.hasText(fileName)) {
				fileName = invoice.getName();
			}
			invoice.setFileName(fileName);
			invoice.setName(fileName);
			
			invoice.buildDescription();
			
			String path = printBill(invoice);	
			log.trace("Invoice printed: {}", path);
			try {
				String fileID = billlingFileUploaderService.uploadFileToFileManager(invoice, path, httpHeaders, locale);
				invoice.setFile(fileID);
				log.trace("PDF stored: {}", fileID);
			}
			catch (Exception e) {
				log.error("", e);
			}		
			if(invoice.getFile() != null) {
				try {
					new File(path).delete();
				}
				catch (Exception e) {
					log.debug("Unable to delete temp file : {}", path, e);
				}	
			}
		}
		
		getInvoiceService().save(invoice, locale);
		
		if(listener != null) {
			listener.endSubInfo();
		}
		return 0;
	}

	@Override
	protected int run(BillingRunOutcome outcome) throws Exception {
		log.debug("Try to validate billing run outcome : {}", outcome.getRunNumber());
		if(listener != null) {
			listener.endSubInfo();
		}
		return 0;
	}
	
	@Override
	protected BillingContext buildContext() throws Exception {
		BillingContext context = new BillingContext();		
		context.invoiceRefId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE);
        context.invoiceStatusId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE);        
		context.invoiceValidationDateId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_VALIDATION_DATE_PERIOD, ParameterType.PERIOD);	
		context.billingRunId = getParameterLongValue(BillingParameterCodes.BILLING_RUN_ATTRIBUTE, ParameterType.ATTRIBUTE);            
		return context;
	}
	
	private String printBill(Invoice invoice) throws Exception {
		String code = invoice.getBillTemplateCode();
		InvoicePrinter printer = null;
		if(printer == null) {
			BillTemplate template = billTemplateRepository.findByCode(code);
			printer = new InvoicePrinter(template, "");
			printer.setBillingModelLabels(template.getLabelListChangeHandler().getItems());
			printer.setDefaultLang(defaultLanguage);
		}		
		String path = printer.print(invoice);
		return path;
	}

}

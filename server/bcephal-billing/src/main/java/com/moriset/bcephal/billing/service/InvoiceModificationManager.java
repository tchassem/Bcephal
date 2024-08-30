/**
 * 
 */
package com.moriset.bcephal.billing.service;

import java.io.File;
import java.util.List;
import java.util.Locale;

import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.billing.domain.BillTemplate;
import com.moriset.bcephal.billing.domain.BillingTemplateLabel;
import com.moriset.bcephal.billing.domain.Invoice;
import com.moriset.bcephal.billing.domain.InvoiceStatus;
import com.moriset.bcephal.billing.repository.BillTemplateRepository;
import com.moriset.bcephal.billing.service.batch.BillingContext;
import com.moriset.bcephal.billing.service.batch.InvoicePrinter;
import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.MailSendingStatus;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InvoiceModificationManager  {
		
	@Autowired
	BillingFileUploaderService billlingFileUploaderService;
	
	@Autowired
	BillTemplateRepository billTemplateRepository;
	
	@Autowired
	BillTemplateService billTemplateService;
	
	@Autowired
	ParameterRepository parameterRepository;
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	InvoiceService invoiceService;
	
	@Value("${bcephal.language}")
	String defaultLanguage;
	
	
	@Transactional
	public Invoice changeSendStatus(Invoice invoice, MailSendingStatus status, HttpHeaders httpHeaders, Locale locale) throws Exception {
		log.debug("Try to change send status for invoice : {}", invoice.getReference());
				
		BillingContext context = buildContextForSendStatus();
		
		invoice.setMailStatus(status);
		invoice.setVersion(invoice.getVersion() + 1);	
		invoice.setManuallyModified(true);
				
		String sql = context.getSendingInvoiceSQL();
		
		Query query = entityManager.createNativeQuery(sql);
		int index = 1;
		query.setParameter(index++, status == MailSendingStatus.SENT ? context.invoiceMailStatusSentValue : context.invoiceMailStatusNotSendValue);
		query.setParameter(index++, invoice.getReference());
		query.setParameter(index++, invoice.isCreditNote() ? UniverseSourceType.CREDIT_NOTE.toString() : UniverseSourceType.INVOICE.toString());	
		query.setParameter(index++, invoice.getReference());
		query.executeUpdate();			
		invoice = invoiceService.save(invoice, locale);		
		return invoice;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional
	public Invoice modify(Invoice invoice, HttpHeaders httpHeaders, Locale locale) throws Exception {
		log.debug("Try to modify invoice : {}", invoice.getReference());
		
		if(invoice.getStatus() == InvoiceStatus.VALIDATED) {
			throw new BcephalException("You can't modify a validated invoice!");
		}
		BillingContext context = buildContext();
				
		String sql = context.getUpdateInvoiceSQL();
		
		invoice.buildVatItems();
		
		int i = 1;
		Query query = entityManager.createNativeQuery(sql);
				
		query.setParameter(i++, new TypedParameterValue(StandardBasicTypes.STRING, invoice.getClientNumber()));
		query.setParameter(i++, invoice.getAmountWithoutVat());
		query.setParameter(i++, new TypedParameterValue(StandardBasicTypes.BIG_DECIMAL, invoice.getVatAmount()));
		query.setParameter(i++, invoice.getTotalAmount());
		query.setParameter(i++, new TypedParameterValue(StandardBasicTypes.DATE, invoice.getDueDate()));
		if(context.invoiceCommunicationMessageId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getCommunicationMessage()) ? invoice.getCommunicationMessage() : "");
		}
		if(context.invoiceDescriptionId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getDescription()) ? invoice.getDescription() : "");
		}
		
		if(context.clientNameId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getClientName()) ? invoice.getClientName() : "");
		}
		if(context.clientDepartmentNumberId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getDepartmentNumber()) ? invoice.getDepartmentNumber() : "");	
		}
		if(context.clientLanguageId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getClientLanguage()) ? invoice.getClientLanguage() : "");	
		}					
		if(context.clientAdressStreetId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getClientAdressStreet()) ? invoice.getClientAdressStreet() : "");	
		}		
		if(context.clientAdressPostalCodeId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getClientAdressPostalCode()) ? invoice.getClientAdressPostalCode() : "");	
		}		
		if(context.clientAdressCityId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getClientAdressCity()) ? invoice.getClientAdressCity() : "");	
		}		
		if(context.clientAdressCountryId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getClientAdressCountry()) ? invoice.getClientAdressCountry() : "");
		}		
		if(context.clientVatNumberId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getClientVatNumber()) ? invoice.getClientVatNumber() : "");
		}		
		if(context.clientLegalFormId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getClientLegalForm()) ? invoice.getClientLegalForm() : "");
		}		
		if(context.clientPhoneId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getClientPhone()) ? invoice.getClientPhone() : "");
		}		
		if(context.clientEmailId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getClientEmail()) ? invoice.getClientEmail() : "");	
		}	
		if(context.clientEmailCcId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getClientEmailCc()) ? invoice.getClientEmailCc() : "");	
		}	
		if(context.clientInternalNumberId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getClientInternalNumber()) ? invoice.getClientInternalNumber() : "");	
		}		
		if(context.clientDoingBusinessAsId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getClientDoingBusinessAs()) ? invoice.getClientDoingBusinessAs() : "");	
		}
		
		if(context.clientContactTitleId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getContactTitle()) ? invoice.getContactTitle() : "");
		}
		if(context.clientContactFirstnameId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getContactFirstname()) ? invoice.getContactFirstname() : "");
		}
		if(context.clientContactLastnameId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getContactLastname()) ? invoice.getContactLastname() : "");
		}
		
			
		query.setParameter(i++, invoice.getId());
		query.setParameter(i++, invoice.getReference());
		query.setParameter(i++, invoice.isCreditNote() ? UniverseSourceType.CREDIT_NOTE.toString() : UniverseSourceType.INVOICE.toString());
		query.setParameter(i++, invoice.getReference());
		if(context.billingRunId != null) {
			query.setParameter(i++, StringUtils.hasText(invoice.getRunNumber()) ? invoice.getRunNumber() : "");
		}	
		query.executeUpdate();		
		
		
				
		if(StringUtils.hasText(invoice.getFileNameDescription())) {
			String fileName = invoice.buildFileName();
			invoice.setFileName(fileName);
			invoice.setName(fileName);
		}	
		invoice.setVersion(invoice.getVersion() + 1);	
		invoice.setManuallyModified(true);
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
		
		invoice = invoiceService.save(invoice, locale);		
		return invoice;
	}
	
	protected BillingContext buildContext() throws Exception {
		BillingContext context = new BillingContext();		
		context.invoiceRefId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.invoiceTypeId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_CREDIT_NOTE_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.invoiceValue = getParameterStringValue(BillingParameterCodes.BILLING_INVOICE_CREDIT_NOTE_INVOICE_VALUE, ParameterType.ATTRIBUTE_VALUE);
        context.creditNoteValue = getParameterStringValue(BillingParameterCodes.BILLING_INVOICE_CREDIT_NOTE_CN_VALUE, ParameterType.ATTRIBUTE_VALUE);
        context.invoiceStatusId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE);        
		context.invoiceDueDateId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_DUE_DATE_PERIOD, ParameterType.PERIOD);
		context.invoiceCommunicationMessageId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_COMMUNICATION_MESSAGE_ATTRIBUTE, ParameterType.ATTRIBUTE);        
        context.invoiceDescriptionId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_DESCRIPTION_ATTRIBUTE, ParameterType.ATTRIBUTE);
        
        context.invoiceAmountWithoutVatId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_AMOUNT_WITHOUT_VAT_MEASURE, ParameterType.MEASURE);
		context.invoiceVatAmountId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_VAT_AMOUNT_MEASURE, ParameterType.MEASURE);		
		context.invoiceTotalAmountId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_TOTAL_AMOUNT_MEASURE, ParameterType.MEASURE);
        
		context.billingRunId = getParameterLongValue(BillingParameterCodes.BILLING_RUN_ATTRIBUTE, ParameterType.ATTRIBUTE); 
		context.billingRunTypeId = getParameterLongValue(BillingParameterCodes.BILLING_RUN_TYPE_ATTRIBUTE, ParameterType.ATTRIBUTE);
		
		context.clientIdId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_ID_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientNameId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientDepartmentNumberId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_DEPARTMENT_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientDepartmentNameId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_DEPARTMENT_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientLanguageId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_DEFAULT_LANGUAGE_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientAdressStreetId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_ADRESS_STREET_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientAdressPostalCodeId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_ADRESS_POSTAL_CODE_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientAdressCityId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_ADRESS_CITY_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientAdressCountryId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_ADRESS_COUNTRY_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientVatNumberId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_VAT_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientLegalFormId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_LEGAL_FORM_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientPhoneId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_PHONE_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientEmailId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_EMAIL_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientEmailCcId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_EMAIL_CC_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientInternalNumberId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_INTERNAL_ID_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientDoingBusinessAsId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_DOING_BUSINESS_AS_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientDepartmentInternalNumberId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_DEPARTMENT_INTERNAL_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE);
		
		context.clientContactLastnameId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_CONTACT_LASTNAME_ATTRIBUTE, ParameterType.ATTRIBUTE);		
		context.clientContactFirstnameId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_CONTACT_FIRSTNAME_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientContactTitleId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_CONTACT_TITLE_ATTRIBUTE, ParameterType.ATTRIBUTE);				
		
		return context;
	}
	
	protected BillingContext buildContextForSendStatus() throws Exception {
		BillingContext context = new BillingContext();		
		context.invoiceRefId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.invoiceMailStatusId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE);		
		context.invoiceMailStatusSentValue = getParameterStringValue(BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_SENT_VALUE, ParameterType.ATTRIBUTE_VALUE);
        context.invoiceMailStatusNotSendValue = getParameterStringValue(BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_NOT_YET_SENT_VALUE, ParameterType.ATTRIBUTE_VALUE);        
		return context;
	}
	
	private String printBill(Invoice invoice) throws Exception {
		String code = invoice.getBillTemplateCode();	
		//code = "default_invoice_template";
		if(!StringUtils.hasText(code)) {
			throw new BcephalException("Unable to pring invoice : '{}'. The bill template is not defined!", invoice.getReference());
		}
		InvoicePrinter printer = null;
		if(printer == null) {
			BillTemplate template = billTemplateRepository.findByCode(code);
			if(template == null) {
				throw new BcephalException("Unable to pring invoice : '{}'. The bill template with code '{}' is not found!", invoice.getReference(), code);
			}
        	List<BillingTemplateLabel> labels = billTemplateService.getTemplateLabels(template);
			printer = new InvoicePrinter(template, "");
			printer.setBillingModelLabels(labels);
			printer.setDefaultLang(defaultLanguage);
		}		
		String path = printer.print(invoice);
		return path;
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

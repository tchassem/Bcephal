/**
 * 
 */
package com.moriset.bcephal.billing.service.action;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.alarm.domain.Alarm;
import com.moriset.bcephal.alarm.domain.AlarmCategory;
import com.moriset.bcephal.alarm.repository.AlarmRepository;
import com.moriset.bcephal.billing.domain.BillingRunOutcome;
import com.moriset.bcephal.billing.domain.Invoice;
import com.moriset.bcephal.billing.service.batch.BillingContext;
import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.MailSendingStatus;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.utils.BcephalException;

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
public class BillingSendMailRunner extends BillingActionRunner {
	
	@Autowired
	BillingAlarmRunner alarmRunner;
	
	@Autowired
	AlarmRepository alarmRepository;
	
	@Override
	protected int run(List<Long> ids) throws Exception {
		if(billingRequest.getModelId() == null) {
			throw new BcephalException("You have to choose an automatic mail model!");
		}
		Optional<Alarm> response = alarmRepository.findById(billingRequest.getModelId());
		if(response.isEmpty()) {
			throw new BcephalException("Automatic mail model with ID '" + billingRequest.getModelId() + "' is not found!");
		}
		Alarm alarm = response.get();
		if(alarm.getCategory() != AlarmCategory.INVOICE) {
			throw new BcephalException("Automatic mail model '" + alarm.getName() + "' is not for invoice!");
		}
		
		int count = alarmRunner.sendAlarm(alarm, username, mode, projectCode, client, billingRequest.getIds(), billingRequest.isRunOutcome(), billingRequest.isFromRepository(), context);
		return count;
	}
	
	@Override
	protected int run(Invoice invoice) throws Exception {
		log.debug("Try to send mail for invoice : {}", invoice.getReference());
		
		invoice.setMailStatus(MailSendingStatus.SENT);
		String sql = context.getSendingInvoiceSQL();
		Query query = getEntityManager().createNativeQuery(sql);
		int index = 1;
		query.setParameter(index++, context.invoiceMailStatusSentValue);
		query.setParameter(index++, invoice.getReference());
		query.setParameter(index++, invoice.isCreditNote() ? UniverseSourceType.CREDIT_NOTE.toString() : UniverseSourceType.INVOICE.toString());	
		query.setParameter(index++, invoice.getReference());
		query.executeUpdate();
		
		getInvoiceService().save(invoice, locale);
		
		if(listener != null) {
			listener.endSubInfo();
		}
		return 0;
	}

	@Override
	protected int run(BillingRunOutcome outcome) throws Exception {
		log.debug("Try to send mails billing run outcome : {}", outcome.getRunNumber());
		if(listener != null) {
			listener.endSubInfo();
		}
		return 0;
	}
	
	@Override
	protected BillingContext buildContext() throws Exception {
		BillingContext context = new BillingContext();		
		context.invoiceRefId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientIdId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_ID_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.clientNameId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.invoiceAmountWithoutVatId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_AMOUNT_WITHOUT_VAT_MEASURE, ParameterType.MEASURE);
		context.invoiceVatAmountId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_VAT_AMOUNT_MEASURE, ParameterType.MEASURE);
		context.invoiceTotalAmountId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_TOTAL_AMOUNT_MEASURE, ParameterType.MEASURE);
		context.invoiceStatusId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.invoiceMailStatusId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.billingRunId = getParameterLongValue(BillingParameterCodes.BILLING_RUN_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.billingRunDateId = getParameterLongValue(BillingParameterCodes.BILLING_RUN_DATE_PERIOD, ParameterType.PERIOD);
		
		context.invoiceStatusDraftValue =  getParameterStringValue(BillingParameterCodes.BILLING_INVOICE_STATUS_DRAFT_VALUE, ParameterType.ATTRIBUTE_VALUE);
        context.invoiceStatusValidatedValue =  getParameterStringValue(BillingParameterCodes.BILLING_INVOICE_STATUS_VALIDATED_VALUE, ParameterType.ATTRIBUTE_VALUE);
		
		context.invoiceMailStatusSentValue = getParameterStringValue(BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_SENT_VALUE, ParameterType.ATTRIBUTE_VALUE);
        context.invoiceMailStatusNotSendValue = getParameterStringValue(BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_NOT_YET_SENT_VALUE, ParameterType.ATTRIBUTE_VALUE);
        
        context.invoiceDescriptionId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_DESCRIPTION_ATTRIBUTE, ParameterType.ATTRIBUTE);
        
		return context;
	}

}

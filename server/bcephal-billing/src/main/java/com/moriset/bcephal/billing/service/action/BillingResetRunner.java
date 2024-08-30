/**
 * 
 */
package com.moriset.bcephal.billing.service.action;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.billing.domain.BillingRunOutcome;
import com.moriset.bcephal.billing.domain.Invoice;
import com.moriset.bcephal.billing.domain.InvoiceStatus;
import com.moriset.bcephal.billing.service.batch.BillingContext;
import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@Slf4j
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@EqualsAndHashCode(callSuper=false)
public class BillingResetRunner extends BillingActionRunner {
	
	@Override
	protected int run(Invoice invoice) throws Exception {
		if(invoice != null) {
			log.debug("Try to reset invoice : {}", invoice.getReference());
			if(invoice.getStatus() == InvoiceStatus.VALIDATED) {
				log.debug("Invoice : {} is already validated!", invoice.getReference());
				throw new BcephalException("Unable to reset validated invoice : " + invoice.getReference());
			}
			String sql = context.getResetInvoiceSQL();
			Query query = getEntityManager().createNativeQuery(sql);
			query.setParameter(1, context.billingEventStatusDraftValue);
			query.setParameter(2, invoice.getReference());
			query.setParameter(3, invoice.getRunNumber());
			query.executeUpdate();
			
			sql = context.getDeleteInvoiceSQL();
			query = getEntityManager().createNativeQuery(sql);
			query.setParameter(1, invoice.getId());
			query.setParameter(2, invoice.getReference());
			query.setParameter(3, invoice.isCreditNote() ? UniverseSourceType.CREDIT_NOTE.toString() : UniverseSourceType.INVOICE.toString());
			query.executeUpdate();
			
			getInvoiceService().delete(invoice);
		}
		if(listener != null) {
			listener.endSubInfo();
		}
		return 0;
	}

	@Override
	protected int run(BillingRunOutcome outcome) throws Exception {
		log.debug("Try to reset billing run outcome : {}", outcome.getRunNumber());
		
		if(listener != null) {
			listener.endSubInfo();
		}
		return 0;
	}
		
	@Override
	protected BillingContext buildContext() throws Exception {
		BillingContext context = new BillingContext();			
		context.invoiceRefId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.billingEventStatusId = getParameterLongValue(BillingParameterCodes.BILLING_EVENT_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE);		
		context.billingEventStatusBilledValue = getParameterStringValue(BillingParameterCodes.BILLING_EVENT_STATUS_BILLED_VALUE, ParameterType.ATTRIBUTE_VALUE);
		context.billingEventStatusDraftValue = getParameterStringValue(BillingParameterCodes.BILLING_EVENT_STATUS_DRAFT_VALUE, ParameterType.ATTRIBUTE_VALUE);
		context.billingRunId = getParameterLongValue(BillingParameterCodes.BILLING_RUN_ATTRIBUTE, ParameterType.ATTRIBUTE);        
		return context;
	}
	

}

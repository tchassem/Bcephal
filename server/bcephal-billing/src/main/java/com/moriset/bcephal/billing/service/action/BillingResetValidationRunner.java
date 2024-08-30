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
public class BillingResetValidationRunner extends BillingActionRunner {
	
	@Override
	protected int run(Invoice invoice) throws Exception {
		log.debug("Try to reset validation of invoice : {}", invoice.getReference());
		
		invoice.setStatus(InvoiceStatus.DRAFT);
		invoice.setValidationDate(null);				
		String sql = context.getResetInvoiceValidationSQL();
		Query query = getEntityManager().createNativeQuery(sql);
		int index = 1;
		query.setParameter(index++, invoice.getStatus().toString());
		query.setParameter(index++, invoice.getId());
		query.setParameter(index++, invoice.getReference());
		query.setParameter(index++, invoice.isCreditNote() ? UniverseSourceType.CREDIT_NOTE.toString() : UniverseSourceType.INVOICE.toString());		
		query.executeUpdate();
					
		getInvoiceService().save(invoice, locale);
		getInvoiceLogService().deleteByInvoice(invoice.getId());
		
		if(listener != null) {
			listener.endSubInfo();
		}
		return 1;
	}

	@Override
	protected int run(BillingRunOutcome outcome) throws Exception {
		log.debug("Try to reset validation of billing run outcome : {}", outcome.getRunNumber());
		if(listener != null) {
			listener.endSubInfo();
		}
		return 0;
	}
	
	@Override
	protected BillingContext buildContext() throws Exception {
		BillingContext context = new BillingContext();		
		context.invoiceStatusId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE);        
		context.invoiceValidationDateId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_VALIDATION_DATE_PERIOD, ParameterType.PERIOD);	
		return context;
	}

}

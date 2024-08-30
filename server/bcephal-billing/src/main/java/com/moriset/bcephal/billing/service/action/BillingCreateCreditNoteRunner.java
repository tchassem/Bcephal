/**
 * 
 */
package com.moriset.bcephal.billing.service.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.billing.domain.BillingRunOutcome;
import com.moriset.bcephal.billing.domain.Invoice;
import com.moriset.bcephal.billing.domain.InvoiceStatus;
import com.moriset.bcephal.billing.service.batch.BillingContext;
import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.service.UniverseGenerator;
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
public class BillingCreateCreditNoteRunner extends BillingActionRunner {
	
	@Autowired
	UniverseGenerator universeGenerator;
	
	@Override
	protected int run(Invoice invoice) throws Exception {
		if(invoice != null) {
			log.debug("Try to cretate credit note from invoice : {}", invoice.getReference());
			if(invoice.getStatus() == InvoiceStatus.DRAFT) {
				log.debug("Invoice : {} is no yet validated!", invoice.getReference());
				throw new BcephalException("Unable to cretate credit note from DRAFT invoice : " + invoice.getReference());
			}
			
			if(invoice.getAmountWithoutVat() == BigDecimal.ZERO) {
				log.debug("Invoice : {} Amount is = 0", invoice.getReference());
				throw new BcephalException("Unable to cretate credit note from invoice with amont equals to zero : " + invoice.getReference());
			}
			
			String universe = UniverseParameters.SCHEMA_NAME.concat(UniverseParameters.UNIVERSE_TABLE_NAME);
			String table = "TEMP_CN_" + invoice.getId() + System.currentTimeMillis();
			
			String statusCol = new Attribute(context.billingEventStatusId, "").getUniverseTableColumnName();
			String invoiceNbrCol = new Attribute(context.invoiceRefId, "").getUniverseTableColumnName();
			String subInvoiceNbrCol = new Attribute(context.subInvoiceRefId, "").getUniverseTableColumnName();
			String eventTypeCol = new Attribute(context.billingEventTypeId, "").getUniverseTableColumnName();				
			String runCol = new Attribute(context.billingRunId, "").getUniverseTableColumnName();
			String dateCol = new Period(context.billingEventDateId).getUniverseTableColumnName();
			
			
			String existsql = "SELECT count(1) from " + universe 
					+ " WHERE " + subInvoiceNbrCol + " = :reference"
					+ " AND " + eventTypeCol + " = :eventType";
			Query existquery = getEntityManager().createNativeQuery(existsql);
			existquery.setParameter("reference", invoice.getReference());
			existquery.setParameter("eventType", context.billingEventTypeCreditNoteValue);
			Number count = (Number)existquery.getSingleResult();	
			if(count.longValue() > 0) {
				throw new BcephalException("Unable to create credit note. A credit note already exists for this invoice : " + invoice.getReference());
			}			
						
			
			String sql = "CREATE TEMPORARY TABLE " + table 
					+ " AS SELECT * from " + universe 
					+ " WHERE " + invoiceNbrCol + " = :reference"
					+ " AND " + eventTypeCol + " = :eventType"
					+ " AND " + statusCol + " = :status";
			Query query = getEntityManager().createNativeQuery(sql);
			query.setParameter("reference", invoice.getReference());
			query.setParameter("eventType", context.billingEventTypeInvoiceValue);
			query.setParameter("status", context.billingEventStatusBilledValue);
			query.executeUpdate();	
			
			
			sql = "ALTER TABLE " + table  + " DROP " + UniverseParameters.ID;
			query = getEntityManager().createNativeQuery(sql);
			query.executeUpdate();
							
			sql = "UPDATE " + table 
					+ " SET " + eventTypeCol + " = :eventType" 
					+ ", " + invoiceNbrCol + " = :reference"
					+ ", " + subInvoiceNbrCol + " = :subreference"
					+ ", " + statusCol + " = :status"
					+ ", " + runCol + " = :run"
					+ ", " + dateCol + " = :date"
					+ ", " + UniverseParameters.SOURCE_TYPE + " = :sourceType"
					+ ", " + UniverseParameters.SOURCE_ID + " = null"
					+ ", " + UniverseParameters.USERNAME + " = :username";
			query = getEntityManager().createNativeQuery(sql);
			query.setParameter("eventType", context.billingEventTypeCreditNoteValue);
			query.setParameter("reference", null);
			query.setParameter("subreference", invoice.getReference());
			query.setParameter("status", context.billingEventStatusDraftValue);
			query.setParameter("run", null);
			query.setParameter("date", new Date());
			query.setParameter("sourceType", UniverseSourceType.INPUT_GRID.toString());
			//query.setParameter("sourceOid", null);
			query.setParameter("username", username);
			query.executeUpdate();
			
			BigDecimal delta = invoice.getBillingAmountWithoutVat().subtract(invoice.getAmountWithoutVat());
			if(BigDecimal.ZERO.compareTo(delta) != 0) {
				String descriptionCol = new Attribute(context.descriptionId, "").getUniverseTableColumnName();
				String amountCol = new Measure(context.billingAmountId, "").getUniverseTableColumnName();
				
				sql = "INSERT INTO " + table + "(" 
						+ eventTypeCol
						+ ", " + subInvoiceNbrCol
						+ ", " + statusCol
						+ ", " + descriptionCol	
						+ ", " + amountCol
						+ ", " + dateCol
						//+ ", " + eventNumberCol
						+ ", " + UniverseParameters.SOURCE_TYPE
						+ ", " + UniverseParameters.SOURCE_ID
						+ ", " + UniverseParameters.USERNAME
						+ ") VALUES(:eventType, :subreference, :status, :description, :amount, :date, :sourceType, null, :username)";
				query = getEntityManager().createNativeQuery(sql);
				query.setParameter("eventType", context.billingEventTypeCreditNoteValue);
				query.setParameter("description", "Credit note corresponding to manual adjustement in original invoice");
				query.setParameter("amount", delta);
				query.setParameter("date", new Date());
				//query.setParameter("eventNumber", "000000000");
				query.setParameter("subreference", invoice.getReference());
				query.setParameter("status", context.billingEventStatusDraftValue);
				query.setParameter("sourceType", UniverseSourceType.INPUT_GRID.toString());
				//query.setParameter("sourceOid", null);
				query.setParameter("username", username);
				query.executeUpdate();
			}
			
			List<String> cols = universeGenerator.getTableColumns(UniverseParameters.SCHEMA_NAME, UniverseParameters.UNIVERSE_TABLE_NAME);
			cols.remove(UniverseParameters.ID);
			String columns = "";
			String coma = "";
			for(String col : cols) {
				columns += coma + col;
				coma = ",";
			}
			sql = "INSERT INTO " + universe + "(" + columns + ")"
					+ " SELECT tmp.* FROM " + table + " tmp";
			query = getEntityManager().createNativeQuery(sql);
			query.executeUpdate();
			
			sql = "DROP TABLE " + table;
			query = getEntityManager().createNativeQuery(sql);
			query.executeUpdate();
			
		}
		if(listener != null) {
			listener.endSubInfo();
		}
		return 0;
	}
	
	

	@Override
	protected int run(BillingRunOutcome outcome) throws Exception {
		log.debug("Try to cretate credit note from billing run outcome : {}", outcome.getRunNumber());
		
		if(listener != null) {
			listener.endSubInfo();
		}
		return 0;
	}
		
	@Override
	protected BillingContext buildContext() throws Exception {
		BillingContext context = new BillingContext();		
		context.invoiceRefId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE);
		
		context.descriptionId = getParameterLongValue(BillingParameterCodes.BILLING_EVENT_DESCRIPTION_ATTRIBUTE, ParameterType.ATTRIBUTE);	
		context.billingAmountId = getParameterLongValue(BillingParameterCodes.BILLING_EVENT_BILLING_AMOUNT_MEASURE, ParameterType.MEASURE);
		
		context.billingEventStatusId = getParameterLongValue(BillingParameterCodes.BILLING_EVENT_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE);		
		context.billingEventStatusBilledValue = getParameterStringValue(BillingParameterCodes.BILLING_EVENT_STATUS_BILLED_VALUE, ParameterType.ATTRIBUTE_VALUE);
		context.billingEventStatusDraftValue = getParameterStringValue(BillingParameterCodes.BILLING_EVENT_STATUS_DRAFT_VALUE, ParameterType.ATTRIBUTE_VALUE);
		
		context.billingRunId = getParameterLongValue(BillingParameterCodes.BILLING_RUN_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.billingEventDateId = getParameterLongValue(BillingParameterCodes.BILLING_EVENT_DATE_PERIOD, ParameterType.PERIOD);
		
		context.subInvoiceRefId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_SUB_INVOICE_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE);	
		context.billingEventTypeId = getParameterLongValue(BillingParameterCodes.BILLING_EVENT_TYPE_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.billingEventTypeInvoiceValue =  getParameterStringValue(BillingParameterCodes.BILLING_EVENT_TYPE_INVOICE_VALUE, ParameterType.ATTRIBUTE_VALUE);
		context.billingEventTypeCreditNoteValue =  getParameterStringValue(BillingParameterCodes.BILLING_EVENT_TYPE_CREDIT_NOTE_VALUE, ParameterType.ATTRIBUTE_VALUE);		
		        
		return context;
	}
	

}

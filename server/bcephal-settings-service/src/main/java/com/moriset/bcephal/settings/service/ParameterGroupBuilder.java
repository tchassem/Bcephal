package com.moriset.bcephal.settings.service;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.AccountingParameterCodes;
import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.InitiationParameterCodes;
import com.moriset.bcephal.domain.ReconciliationParameterCodes;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.settings.domain.ParameterGroup;
import com.moriset.bcephal.settings.domain.ParameterGroupItem;

public class ParameterGroupBuilder {

	public ParameterGroupBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	public List<ParameterGroup> buildParameterGroup(){
		List<ParameterGroup> groups  = new ArrayList<ParameterGroup>();
		groups.add(buildInitiationGroup());
		groups.add(buildReconciliationGroup());
		groups.add(buildBillingGroup());
		groups.add(buildAccountingGroup());
		groups.add(buildLogsGroup());
		
		return groups;
	}

	private ParameterGroup buildInitiationGroup() {
		
		ParameterGroup group = new ParameterGroup(InitiationParameterCodes.INITIATION);
		group.setCanBeCreateAutomatically(true);
		group.getParameterGroupItems().add(new ParameterGroupItem(InitiationParameterCodes.INITIATION_DEFAULT_MODEL, ParameterType.MODEL.name(), null));
		group.getParameterGroupItems().add(new ParameterGroupItem(InitiationParameterCodes.INITIATION_DEFAULT_ENTITY, ParameterType.ENTITY.name(), null));
		group.getParameterGroupItems().add(new ParameterGroupItem(InitiationParameterCodes.INITIATION_FILE_LOADER_FILE_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
		group.getParameterGroupItems().add(new ParameterGroupItem(InitiationParameterCodes.INITIATION_FILE_LOADER_LOAD_NBR_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
		group.getParameterGroupItems().add(new ParameterGroupItem(InitiationParameterCodes.INITIATION_FILE_LOADER_LOAD_NBR_SEQUENCE, ParameterType.INCREMENTAL_NUMBER.name(), null));
		group.getParameterGroupItems().add(new ParameterGroupItem(InitiationParameterCodes.OPERATION_CODE_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
		return group;
	}

	private ParameterGroup buildReconciliationGroup() {

		ParameterGroup group = new ParameterGroup(ReconciliationParameterCodes.RECONCILIATION);
		group.setCanBeCreateAutomatically(true);
		
		group.getParameterGroupItems().add(new ParameterGroupItem(ReconciliationParameterCodes.RECONCILIATION_DC_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
		group.getParameterGroupItems().add(new ParameterGroupItem(ReconciliationParameterCodes.RECONCILIATION_CREDIT_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), ReconciliationParameterCodes.RECONCILIATION_DC_ATTRIBUTE));
		group.getParameterGroupItems().add(new ParameterGroupItem(ReconciliationParameterCodes.RECONCILIATION_DEBIT_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), ReconciliationParameterCodes.RECONCILIATION_DC_ATTRIBUTE));		
		group.getParameterGroupItems().add(new ParameterGroupItem(ReconciliationParameterCodes.RECONCILIATION_AUTO_MANUAL_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
		group.getParameterGroupItems().add(new ParameterGroupItem(ReconciliationParameterCodes.RECONCILIATION_AUTOMATIC_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), ReconciliationParameterCodes.RECONCILIATION_AUTO_MANUAL_ATTRIBUTE));		
		group.getParameterGroupItems().add(new ParameterGroupItem(ReconciliationParameterCodes.RECONCILIATION_MANUAL_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), ReconciliationParameterCodes.RECONCILIATION_AUTO_MANUAL_ATTRIBUTE));		
		group.getParameterGroupItems().add(new ParameterGroupItem(ReconciliationParameterCodes.RECONCILIATION_USER_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
		return group;
	}

	private ParameterGroup buildBillingGroup() {
		ParameterGroup group = new ParameterGroup(BillingParameterCodes.BILLING);		
		group.setCanBeCreateAutomatically(false);
		
		ParameterGroup roleGroup = new ParameterGroup(BillingParameterCodes.BILLING_ROLE);		
		roleGroup.setCanBeCreateAutomatically(true);
		group.getSubGroups().add(roleGroup);
		roleGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_LEGAL_FORM_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        roleGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_PHONE_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        roleGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_EMAIL_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        roleGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_EMAIL_CC_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        roleGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_ADRESS_STREET_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        roleGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_ADRESS_POSTAL_CODE_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        roleGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_ADRESS_CITY_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        roleGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_ADRESS_COUNTRY_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        roleGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_VAT_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        roleGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_DEFAULT_LANGUAGE_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        roleGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        roleGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_STATUS_ACTIVE_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), BillingParameterCodes.BILLING_ROLE_STATUS_ATTRIBUTE));
        roleGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_STATUS_INACTIVE_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), BillingParameterCodes.BILLING_ROLE_STATUS_ATTRIBUTE));
		
		ParameterGroup clientGroup = new ParameterGroup(BillingParameterCodes.BILLING_ROLE_CLIENT);		
		clientGroup.setCanBeCreateAutomatically(false);
		roleGroup.getSubGroups().add(clientGroup);
		clientGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_CLIENT_ID_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        clientGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_CLIENT_INTERNAL_ID_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        clientGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_CLIENT_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        clientGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_CLIENT_DOING_BUSINESS_AS_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        clientGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_CLIENT_CONTACT_TITLE_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        clientGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_CLIENT_CONTACT_FIRSTNAME_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        clientGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_CLIENT_CONTACT_LASTNAME_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        clientGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_CLIENT_DEPARTMENT_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        clientGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_CLIENT_DEPARTMENT_INTERNAL_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), "Department internal number"));
        clientGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_CLIENT_DEPARTMENT_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
				
		ParameterGroup companyGroup = new ParameterGroup(BillingParameterCodes.BILLING_ROLE_BILLING_COMPANY);		
		companyGroup.setCanBeCreateAutomatically(false);
		roleGroup.getSubGroups().add(companyGroup);
		companyGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_BILLING_COMPANY_ID_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
		companyGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_ROLE_BILLING_COMPANY_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
		
		
		ParameterGroup invoiceGroup = new ParameterGroup(BillingParameterCodes.BILLING_INVOICE);		
		invoiceGroup.setCanBeCreateAutomatically(true);
		group.getSubGroups().add(invoiceGroup);
		invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_NUMBER_SEQUENCE, ParameterType.INCREMENTAL_NUMBER.name(), null));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_SUB_INVOICE_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_STATUS_DRAFT_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), BillingParameterCodes.BILLING_INVOICE_STATUS_ATTRIBUTE));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_STATUS_TO_CHECK_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), BillingParameterCodes.BILLING_INVOICE_STATUS_ATTRIBUTE));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_STATUS_VALIDATED_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), BillingParameterCodes.BILLING_INVOICE_STATUS_ATTRIBUTE));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_AMOUNT_WITHOUT_VAT_MEASURE, ParameterType.MEASURE.name(), null));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_VAT_AMOUNT_MEASURE, ParameterType.MEASURE.name(), null));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_TOTAL_AMOUNT_MEASURE, ParameterType.MEASURE.name(), null));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_DATE_PERIOD, ParameterType.PERIOD.name(), null));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_DUE_DATE_PERIOD, ParameterType.PERIOD.name(), null));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_DUE_DATE_CALCULATION_MEASURE, ParameterType.MEASURE.name(), null));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_VALIDATION_DATE_PERIOD, ParameterType.PERIOD.name(), null));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_COMMUNICATION_MESSAGE_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_DESCRIPTION_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_CREDIT_NOTE_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_CREDIT_NOTE_NUMBER_SEQUENCE, ParameterType.INCREMENTAL_NUMBER.name(), null));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_DEFAULT_TEMPLATE, ParameterType.BILL_TEMPLATE.name(), null));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_SENT_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_ATTRIBUTE));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_NOT_YET_SENT_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_ATTRIBUTE));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_CREDIT_NOTE_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_CREDIT_NOTE_INVOICE_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_ATTRIBUTE));
        invoiceGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_INVOICE_CREDIT_NOTE_CN_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_ATTRIBUTE));

        
        ParameterGroup eventGroup = new ParameterGroup(BillingParameterCodes.BILLING_EVENT);		
    	eventGroup.setCanBeCreateAutomatically(true);
		group.getSubGroups().add(eventGroup);
		eventGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_EVENT_DESCRIPTION_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));            
        eventGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_EVENT_DATE_PERIOD, ParameterType.PERIOD.name(), null));
        eventGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_EVENT_BILLING_DRIVER_MEASURE, ParameterType.MEASURE.name(), null));
        eventGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_EVENT_UNIT_COST_MEASURE, ParameterType.MEASURE.name(), null));
        eventGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_EVENT_BILLING_AMOUNT_MEASURE, ParameterType.MEASURE.name(), null));
        eventGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_EVENT_VAT_RATE_MEASURE, ParameterType.MEASURE.name(), null));
        eventGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_EVENT_TYPE_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        eventGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_EVENT_TYPE_INVOICE_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), BillingParameterCodes.BILLING_EVENT_TYPE_ATTRIBUTE));
        eventGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_EVENT_TYPE_CREDIT_NOTE_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), BillingParameterCodes.BILLING_EVENT_TYPE_ATTRIBUTE));
        eventGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_EVENT_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), "Status"));
        eventGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_EVENT_STATUS_DRAFT_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), BillingParameterCodes.BILLING_EVENT_STATUS_ATTRIBUTE));
        eventGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_EVENT_STATUS_FROZEN_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), BillingParameterCodes.BILLING_EVENT_STATUS_ATTRIBUTE));
        eventGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_EVENT_STATUS_BILLED_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), BillingParameterCodes.BILLING_EVENT_STATUS_ATTRIBUTE));
        eventGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_EVENT_AM_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        eventGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_EVENT_AM_AUTO_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), BillingParameterCodes.BILLING_EVENT_AM_ATTRIBUTE));
        eventGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_EVENT_AM_MANUAL_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), BillingParameterCodes.BILLING_EVENT_AM_ATTRIBUTE));
        eventGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_RUN_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));                            
        eventGroup.getParameterGroupItems().add(new ParameterGroupItem(BillingParameterCodes.BILLING_RUN_DATE_PERIOD, ParameterType.PERIOD.name(), null));
				
		return group;
	}

	private ParameterGroup buildAccountingGroup() {
		ParameterGroup group = new ParameterGroup(AccountingParameterCodes.ACCOUNTING);
		group.setCanBeCreateAutomatically(true);
		
		group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_POSTING_ID_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_POSTING_TYPE_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_POSTING_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_POSTING_STATUS_DRAFT_VALUE, ParameterType.ATTRIBUTE_VALUE.name(), AccountingParameterCodes.ACCOUNTING_POSTING_STATUS_ATTRIBUTE));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_POSTING_STATUS_VALIDATED_VALUE,  ParameterType.ATTRIBUTE_VALUE.name(), AccountingParameterCodes.ACCOUNTING_POSTING_STATUS_ATTRIBUTE));

        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_POSTING_ACCOUNT_ID_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_POSTING_ACCOUNT_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_POSTING_SIGN_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_POSTING_SIGN_DEBIT_VALUE,  ParameterType.ATTRIBUTE_VALUE.name(), AccountingParameterCodes.ACCOUNTING_POSTING_SIGN_ATTRIBUTE));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_POSTING_SIGN_CREDIT_VALUE,  ParameterType.ATTRIBUTE_VALUE.name(), AccountingParameterCodes.ACCOUNTING_POSTING_SIGN_ATTRIBUTE));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_POSTING_USER_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_POSTING_COMMENT_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));

        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_POSTING_AMOUNT_MEASURE, ParameterType.MEASURE.name(), null));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_POSTING_ENTRY_DATE_PERIOD, ParameterType.PERIOD.name(), null));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_POSTING_VALUE_DATE_PERIOD, ParameterType.PERIOD.name(), null));

        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_POSTING_NUMBER_SEQUENCE, ParameterType.INCREMENTAL_NUMBER.name(), null));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_POSTING_ACCOUNT_GRID, ParameterType.GRID.name(), null));

        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_BOOKING_ID_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_BOOKING_AMOUNT_MEASURE, ParameterType.MEASURE.name(), null));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_BOOKING_DATE_PERIOD, ParameterType.PERIOD.name(), null));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_BOOKING_MA_ATTRIBUTE, ParameterType.ATTRIBUTE.name(), null));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_BOOKING_MA_AUTOMATIC_VALUE,  ParameterType.ATTRIBUTE_VALUE.name(), AccountingParameterCodes.ACCOUNTING_BOOKING_MA_ATTRIBUTE));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_BOOKING_MA_MANUAL_VALUE,  ParameterType.ATTRIBUTE_VALUE.name(), AccountingParameterCodes.ACCOUNTING_BOOKING_MA_ATTRIBUTE));
        group.getParameterGroupItems().add(new ParameterGroupItem(AccountingParameterCodes.ACCOUNTING_BOOKING_NUMBER_SEQUENCE, ParameterType.INCREMENTAL_NUMBER.name(), null));
	
		return group;
	}
	
	private ParameterGroup buildLogsGroup() {
		ParameterGroup group = new ParameterGroup(InitiationParameterCodes.LOGS);
		group.setCanBeCreateAutomatically(true);
		
		group.getParameterGroupItems().add(new ParameterGroupItem(InitiationParameterCodes.LOGS_ARCHIVE_MAT_GRID, ParameterType.MAT_GRID.name(), null));
        group.getParameterGroupItems().add(new ParameterGroupItem(InitiationParameterCodes.LOGS_JOIN_MAT_GRID, ParameterType.MAT_GRID.name(), null));
        group.getParameterGroupItems().add(new ParameterGroupItem(InitiationParameterCodes.LOGS_FILE_LOADER_MAT_GRID, ParameterType.MAT_GRID.name(), null));
        group.getParameterGroupItems().add(new ParameterGroupItem(InitiationParameterCodes.LOGS_RECO_MAT_GRID, ParameterType.MAT_GRID.name(), null));
        group.getParameterGroupItems().add(new ParameterGroupItem(InitiationParameterCodes.LOGS_SCHEDULER_MAT_GRID, ParameterType.MAT_GRID.name(), null));
	
		return group;
	}

}

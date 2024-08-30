/**
 * 
 */
package com.moriset.bcephal.domain;

/**
 * @author Joseph Wambo
 *
 */
public class BillingParameterCodes {
	
	public static String VARIABLE_CLIENT_ID = "Client ID";       
	public static String VARIABLE_INVOICE_NBR  = "Invoice number";       
	

	public static String BILLING = "billing";

	
	public static String BILLING_ROLE = BILLING + ".role";       
    public static String BILLING_ROLE_GRID = BILLING_ROLE + ".grid";
    
    public static String BILLING_CLIENT_REPOSITORY_GRID = BILLING_ROLE + ".client.repository.grid";
    public static String BILLING_COMPANY_REPOSITORY_GRID = BILLING_ROLE + ".company.repository.mat.grid";
    public static String BILLING_CREDIT_NOTE_GRID = BILLING + ".credit.note.grid";
    public static String BILLING_JOIN = BILLING + ".join";

    public static String BILLING_ROLE_CLIENT = BILLING_ROLE + ".client";
    public static String BILLING_ROLE_CLIENT_ID_ATTRIBUTE = BILLING_ROLE_CLIENT + ".id.attribute";
    public static String BILLING_ROLE_CLIENT_INTERNAL_ID_ATTRIBUTE = BILLING_ROLE_CLIENT + ".internal.id.attribute";
    public static String BILLING_ROLE_CLIENT_NAME_ATTRIBUTE = BILLING_ROLE_CLIENT + ".name.attribute";
    public static String BILLING_ROLE_CLIENT_DOING_BUSINESS_AS_ATTRIBUTE = BILLING_ROLE_CLIENT + ".doing.business.as.attribute";
    public static String BILLING_ROLE_CLIENT_CONTACT_LASTNAME_ATTRIBUTE = BILLING_ROLE_CLIENT + ".contact.attribute";
    public static String BILLING_ROLE_CLIENT_CONTACT_FIRSTNAME_ATTRIBUTE = BILLING_ROLE_CLIENT + ".contact.firstname.attribute";
    public static String BILLING_ROLE_CLIENT_CONTACT_TITLE_ATTRIBUTE = BILLING_ROLE_CLIENT + ".contact.title.attribute";

    public static String BILLING_ROLE_CLIENT_DEPARTMENT_NUMBER_ATTRIBUTE = BILLING_ROLE_CLIENT + ".department.number.attribute";
    public static String BILLING_ROLE_CLIENT_DEPARTMENT_INTERNAL_NUMBER_ATTRIBUTE = BILLING_ROLE_CLIENT + ".department.internal.number.attribute";
    public static String BILLING_ROLE_CLIENT_DEPARTMENT_NAME_ATTRIBUTE = BILLING_ROLE_CLIENT + ".department.name.attribute";

    public static String BILLING_ROLE_BILLING_COMPANY = BILLING_ROLE + ".billing.company";
    public static String BILLING_ROLE_BILLING_COMPANY_ID_ATTRIBUTE = BILLING_ROLE_BILLING_COMPANY + ".id.attribute";
    public static String BILLING_ROLE_BILLING_COMPANY_NAME_ATTRIBUTE = BILLING_ROLE_BILLING_COMPANY + ".name.attribute";


    public static String BILLING_ROLE_LEGAL_FORM_ATTRIBUTE = BILLING_ROLE + ".legal.form.attribute";

    public static String BILLING_ROLE_ADRESS_STREET_ATTRIBUTE = BILLING_ROLE + ".adress.street.attribute";
    public static String BILLING_ROLE_ADRESS_POSTAL_CODE_ATTRIBUTE = BILLING_ROLE + ".adress.postalcode.attribute";
    public static String BILLING_ROLE_ADRESS_CITY_ATTRIBUTE = BILLING_ROLE + ".adress.city.attribute";
    public static String BILLING_ROLE_ADRESS_COUNTRY_ATTRIBUTE = BILLING_ROLE + ".adress.country.attribute";

    public static String BILLING_ROLE_PHONE_ATTRIBUTE = BILLING_ROLE + ".phone.attribute";
    public static String BILLING_ROLE_EMAIL_ATTRIBUTE = BILLING_ROLE + ".email.attribute";
    public static String BILLING_ROLE_EMAIL_CC_ATTRIBUTE = BILLING_ROLE + ".email.cc.attribute";

    public static String BILLING_ROLE_VAT_NUMBER_ATTRIBUTE = BILLING_ROLE + ".vat.number.attribute";

    public static String BILLING_ROLE_DEFAULT_LANGUAGE_ATTRIBUTE = BILLING_ROLE + ".default.language.attribute";

    public static String BILLING_ROLE_STATUS_ATTRIBUTE = BILLING_ROLE + ".status.attribute";
    public static String BILLING_ROLE_STATUS_ACTIVE_VALUE = BILLING_ROLE + ".status.active.value";
    public static String BILLING_ROLE_STATUS_INACTIVE_VALUE = BILLING_ROLE + ".status.inactive.value";
    
    
    
    public static String BILLING_EVENT = BILLING + ".event";
    public static String BILLING_EVENT_NAME_ATTRIBUTE = BILLING_EVENT + ".name.attribute";
    public static String BILLING_EVENT_CATEGORY_ATTRIBUTE = BILLING_EVENT + ".category.attribute";
    public static String BILLING_EVENT_DESCRIPTION_ATTRIBUTE = BILLING_EVENT + ".description.attribute";

    public static String BILLING_EVENT_STATUS_ATTRIBUTE = BILLING_EVENT + ".status.attribute";
    public static String BILLING_EVENT_STATUS_DRAFT_VALUE = BILLING_EVENT + ".status.draft.value";
    public static String BILLING_EVENT_STATUS_FROZEN_VALUE = BILLING_EVENT + ".status.frozen.value";
    public static String BILLING_EVENT_STATUS_BILLED_VALUE = BILLING_EVENT + ".status.billed.value";

    public static String BILLING_EVENT_AM_ATTRIBUTE = BILLING_EVENT + ".am.attribute";
    public static String BILLING_EVENT_AM_AUTO_VALUE = BILLING_EVENT + ".am.auto.value";
    public static String BILLING_EVENT_AM_MANUAL_VALUE = BILLING_EVENT + ".am.manual.value";
    public static String BILLING_EVENT_DRIVER_NAME_ATTRIBUTE = BILLING_EVENT + ".driver.name.attribute";

    public static String BILLING_EVENT_TYPE_ATTRIBUTE = BILLING_EVENT + ".type.attribute";
    public static String BILLING_EVENT_TYPE_INVOICE_VALUE = BILLING_EVENT + ".type.invopice.value";
    public static String BILLING_EVENT_TYPE_CREDIT_NOTE_VALUE = BILLING_EVENT + ".type.credit.note.value";

    public static String BILLING_RUN_ATTRIBUTE = BILLING_EVENT + ".run.attribute";
    public static String BILLING_RUN_TYPE_ATTRIBUTE = BILLING_EVENT + ".run.type.attribute";

    public static String BILLING_EVENT_BILLING_DRIVER_MEASURE = BILLING_EVENT + ".billing.driver.measure";
    public static String BILLING_EVENT_UNIT_COST_MEASURE = BILLING_EVENT + ".unit.cost.measure";
    public static String BILLING_EVENT_BILLING_AMOUNT_MEASURE = BILLING_EVENT + ".billing_amount.measure";
    public static String BILLING_EVENT_VAT_RATE_MEASURE = BILLING_EVENT + ".vat.rate.measure";

    public static String BILLING_EVENT_DATE_PERIOD = BILLING_EVENT + ".date.period";
    public static String BILLING_RUN_DATE_PERIOD = BILLING_EVENT + ".run.date.period";

    public static String BILLING_EVENT_REPOSITORY_GRID = BILLING_EVENT + ".repository.grid";
    public static String BILLING_EVENT_GRID = BILLING_EVENT + ".grid";
    public static String BILLING_EVENT_MANUEL_BILLING_EVENT_DYNAMIC_FORM = BILLING_EVENT + ".manual.billing.event.dinamic.form";




    public static String BILLING_INVOICE = BILLING + ".invoice";
    public static String BILLING_INVOICE_NUMBER_ATTRIBUTE = BILLING_INVOICE + ".number.attribute";
    public static String BILLING_INVOICE_SUB_INVOICE_NUMBER_ATTRIBUTE = BILLING_INVOICE + "sub.invoice.number.attribute";
    public static String BILLING_INVOICE_NUMBER_SEQUENCE = BILLING_INVOICE + ".number.sequence";
    public static String BILLING_INVOICE_STATUS_ATTRIBUTE = BILLING_INVOICE + ".status.attribute";
    public static String BILLING_INVOICE_STATUS_DRAFT_VALUE = BILLING_INVOICE + ".status.draft.value";
    public static String BILLING_INVOICE_STATUS_VALIDATED_VALUE = BILLING_INVOICE + ".status.validated.value";
    public static String BILLING_INVOICE_STATUS_TO_CHECK_VALUE = BILLING_INVOICE + ".status.tocheck.value";
    public static String BILLING_INVOICE_COMMUNICATION_MESSAGE_ATTRIBUTE = BILLING_INVOICE + ".communication.message.attribute";
    public static String BILLING_INVOICE_DESCRIPTION_ATTRIBUTE = BILLING_INVOICE + ".description.attribute";

    public static String BILLING_INVOICE_SENDING_STATUS_ATTRIBUTE = BILLING_INVOICE + ".sending.status.attribute";
    public static String BILLING_INVOICE_SENDING_STATUS_SENT_VALUE = BILLING_INVOICE + ".sending.status.sent.value";
    public static String BILLING_INVOICE_SENDING_STATUS_NOT_YET_SENT_VALUE = BILLING_INVOICE + ".sending.status.notyetsent.value";

    public static String BILLING_INVOICE_AMOUNT_WITHOUT_VAT_MEASURE = BILLING_INVOICE + ".billed.amount.without.vat.measure";
    public static String BILLING_INVOICE_VAT_AMOUNT_MEASURE = BILLING_INVOICE + ".vat.amount.measure";
    public static String BILLING_INVOICE_TOTAL_AMOUNT_MEASURE = BILLING_INVOICE + ".total.amount.measure";

    public static String BILLING_INVOICE_DATE_PERIOD = BILLING_INVOICE + ".date.period";
    public static String BILLING_INVOICE_DUE_DATE_PERIOD = BILLING_INVOICE + ".due.date.period";
    public static String BILLING_INVOICE_DUE_DATE_CALCULATION_MEASURE = BILLING_INVOICE + ".due.date.calculation.measure";
    public static String BILLING_INVOICE_VALIDATION_DATE_PERIOD = BILLING_INVOICE + ".validation.date.period";


    public static String BILLING_CREDIT_NOTE = BILLING + ".credit.note";
    public static String BILLING_CREDIT_NOTE_NUMBER_ATTRIBUTE = BILLING_CREDIT_NOTE + ".number.attribute";
    public static String BILLING_CREDIT_NOTE_NUMBER_SEQUENCE = BILLING_CREDIT_NOTE + ".number.sequence";
    
    public static String BILLING_INVOICE_REPOSITORY_GRID = BILLING_INVOICE + ".repository.grid";
    public static String BILLING_CREDIT_NOTE_REPOSITORY_GRID = BILLING_CREDIT_NOTE + ".repository.grid";

    public static String BILLING_INVOICE_DEFAULT_TEMPLATE = BILLING_INVOICE + ".default.template";
    public static String BILLING_INVOICE_CREDIT_NOTE_DEFAULT_TEMPLATE = BILLING_INVOICE + ".invoice.cn.default.template";


    public static String BILLING_INVOICE_CREDIT_NOTE_ATTRIBUTE = BILLING_INVOICE + ".invoice.cn.attribute";
    public static String BILLING_INVOICE_CREDIT_NOTE_INVOICE_VALUE = BILLING_INVOICE + ".invoice.cn.invoice.value";
    public static String BILLING_INVOICE_CREDIT_NOTE_CN_VALUE = BILLING_INVOICE + ".invoice.cn.cn.value";
	
	

}

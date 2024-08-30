/**
 * 
 */
package com.moriset.bcephal.settings.service;

import java.util.HashMap;
import java.util.Locale;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.domain.AccountingParameterCodes;
import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.domain.InitiationParameterCodes;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.Model;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class AccountingParameterBuilder extends ParameterBuilder {

	protected String MODEL_NAME = "Default Model";
	protected String ENTITY_NAME = "Accounting";
	protected String POSTING_ENTRY_REPOSITOPRY_GRID_NAME = "Posting entry repository";
	protected String BOOKING_REPOSITOPRY_GRID_NAME = "Booking repository";
	
	
	protected Model model;

	@Transactional
	public void buildParameters(HttpSession session, Locale locale) {
		HashMap<Parameter, IPersistent> parameters = new HashMap<Parameter, IPersistent>(0);
		try {
			buildReconciliationParameters(parameters, session, locale);
			saveParameters(parameters, locale);
		} catch (Exception ex) {
			log.error("Unable to save accounting role parameters ", ex);
		}
	}

	private void buildReconciliationParameters(HashMap<Parameter, IPersistent> parametersMap, HttpSession session, Locale locale) throws Exception {
		this.model = (Model) buildParameterIfNotExist(MODEL_NAME, InitiationParameterCodes.INITIATION_DEFAULT_MODEL,
				ParameterType.MODEL, parametersMap, null, null, null, session, locale);
		
		Attribute attribute = (Attribute) buildParameterIfNotExist("Posting Number Generator",
				AccountingParameterCodes.ACCOUNTING_POSTING_NUMBER_SEQUENCE, ParameterType.ATTRIBUTE, parametersMap, null,
				ENTITY_NAME, this.model, session, locale);
		buildParameterIfNotExist("Posting Number Generator", AccountingParameterCodes.ACCOUNTING_POSTING_NUMBER_SEQUENCE,
				ParameterType.ATTRIBUTE, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
		buildParameterIfNotExist("Booking Number Generator", AccountingParameterCodes.ACCOUNTING_BOOKING_NUMBER_SEQUENCE,
				ParameterType.ATTRIBUTE, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
		buildParameterIfNotExist("Posting ID", AccountingParameterCodes.ACCOUNTING_POSTING_ID_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
		buildParameterIfNotExist("Posting Type", AccountingParameterCodes.ACCOUNTING_POSTING_TYPE_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
		buildParameterIfNotExist("Posting Status", AccountingParameterCodes.ACCOUNTING_POSTING_STATUS_ATTRIBUTE,
				ParameterType.ATTRIBUTE, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
		buildParameterIfNotExist("DRAFT", AccountingParameterCodes.ACCOUNTING_POSTING_STATUS_DRAFT_VALUE,
				ParameterType.ATTRIBUTE, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
		attribute = (Attribute) buildParameterIfNotExist("VALIDATED", AccountingParameterCodes.ACCOUNTING_POSTING_STATUS_VALIDATED_VALUE,
				ParameterType.ATTRIBUTE, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
		if(attribute.getId() == null) {
			attribute.setDeclared(true);
		}
		
		buildParameterIfNotExist("Account ID", AccountingParameterCodes.ACCOUNTING_POSTING_ACCOUNT_ID_ATTRIBUTE, ParameterType.ATTRIBUTE, parametersMap,  attribute, ENTITY_NAME, this.model, session, locale);
        buildParameterIfNotExist("Account name", AccountingParameterCodes.ACCOUNTING_POSTING_ACCOUNT_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE, parametersMap,  attribute, ENTITY_NAME, this.model, session, locale);
        
        attribute = (Attribute)buildParameterIfNotExist("Sign", AccountingParameterCodes.ACCOUNTING_POSTING_SIGN_ATTRIBUTE, ParameterType.ATTRIBUTE, parametersMap,  attribute, ENTITY_NAME, this.model, session, locale);
        buildParameterIfNotExist("D", AccountingParameterCodes.ACCOUNTING_POSTING_SIGN_DEBIT_VALUE, ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
        buildParameterIfNotExist("C", AccountingParameterCodes.ACCOUNTING_POSTING_SIGN_CREDIT_VALUE, ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
        if(attribute.getId() == null) {
			attribute.setDeclared(true);
		}        
        
        buildParameterIfNotExist("User", AccountingParameterCodes.ACCOUNTING_POSTING_USER_ATTRIBUTE, ParameterType.ATTRIBUTE, parametersMap,  attribute, ENTITY_NAME, this.model, session, locale);
        buildParameterIfNotExist("Comment", AccountingParameterCodes.ACCOUNTING_POSTING_COMMENT_ATTRIBUTE, ParameterType.ATTRIBUTE, parametersMap,  attribute, ENTITY_NAME, this.model, session, locale);

        buildParameterIfNotExist("Posting amount", AccountingParameterCodes.ACCOUNTING_POSTING_AMOUNT_MEASURE, ParameterType.MEASURE, parametersMap,  attribute, ENTITY_NAME, this.model, session, locale);
        buildParameterIfNotExist("Entry date", AccountingParameterCodes.ACCOUNTING_POSTING_ENTRY_DATE_PERIOD, ParameterType.PERIOD, parametersMap,  attribute, ENTITY_NAME, this.model, session, locale);
        buildParameterIfNotExist("Value date", AccountingParameterCodes.ACCOUNTING_POSTING_VALUE_DATE_PERIOD, ParameterType.PERIOD, parametersMap,  attribute, ENTITY_NAME, this.model, session, locale);
			
        
        
        buildParameterIfNotExist("Booking ID", AccountingParameterCodes.ACCOUNTING_BOOKING_ID_ATTRIBUTE, ParameterType.ATTRIBUTE, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
		
		buildParameterIfNotExist("Booking amount", AccountingParameterCodes.ACCOUNTING_BOOKING_AMOUNT_MEASURE, ParameterType.MEASURE, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
        buildParameterIfNotExist("Booking date", AccountingParameterCodes.ACCOUNTING_BOOKING_DATE_PERIOD, ParameterType.PERIOD, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
        		
		attribute = (Attribute)buildParameterIfNotExist("M/A", AccountingParameterCodes.ACCOUNTING_BOOKING_MA_ATTRIBUTE, ParameterType.ATTRIBUTE, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
		buildParameterIfNotExist("A", AccountingParameterCodes.ACCOUNTING_BOOKING_MA_AUTOMATIC_VALUE, ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
        buildParameterIfNotExist("M", AccountingParameterCodes.ACCOUNTING_BOOKING_MA_MANUAL_VALUE, ParameterType.ATTRIBUTE_VALUE, parametersMap, attribute, ENTITY_NAME, this.model, session, locale);
        if(attribute.getId() == null) {
			attribute.setDeclared(true);
		}
	}

}

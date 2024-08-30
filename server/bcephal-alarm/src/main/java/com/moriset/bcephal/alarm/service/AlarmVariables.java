/**
 * 
 */
package com.moriset.bcephal.alarm.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.moriset.bcephal.alarm.domain.AlarmCategory;

/**
 * @author Moriset
 *
 */
public class AlarmVariables {

	public String CURRENT_DATE 		= "$CURRENT DATE$";
	public String CURRENT_TIME 		= "$CURRENT TIME$";
	public String CURRENT_DATE_TIME = "$CURRENT DATE TIME$";
	public String USER = "$USER$";
	
	public String INVOICE_DUE_DATE 	= "$INVOICE DUE DATE$";
	public String INVOICE_DATE 		= "$INVOICE DATE$";
	public String INVOICE_PERIOD_FROM 	= "$INVOICE PERIOD FROM$";
	public String INVOICE_PERIOD_TO = "$INVOICE PERIOD TO$";
	public String INVOICE_DAY 		= "$INVOICE DAY$";
	public String INVOICE_MONTH 	= "$INVOICE MONTH$";
	public String INVOICE_MONTH_NAME = "$INVOICE MONTH NAME$";
	public String INVOICE_YEAR 		= "$INVOICE YEAR$";
	public String INVOICE_REFERENCE = "$INVOICE REF$";	
	public String INVOICE_DESCRIPTION 		= "$INVOICE DESCRIPTION$";
	public String CLIENT_ID 		= "$INVOICE CLIENT ID$";
	public String CLIENT_NAME 		= "$INVOICE CLIENT NAME$";
	
	public String CONTACT_TITLE 		= "$CONTACT TITLE$";
	public String CONTACT_FIRSTNAME 	= "$CONTACT FIRSTNAME$";
	public String CONTACT_LASTNAME 		= "$CONTACT LASTNAME$";
	
	public List<String> getAll(){
		List<String> variables = new ArrayList<String>();
		Collections.addAll(variables, CURRENT_TIME, CURRENT_DATE, CURRENT_DATE_TIME, USER, 
				INVOICE_REFERENCE, INVOICE_DATE, CLIENT_ID, CLIENT_NAME);	
		return variables;
	}
	
	public Map<String, List<String>> getAllGroupByCategory(){
		Map<String, List<String>> datas = new HashMap<>();
		
		List<String> variables = new ArrayList<String>();
		Collections.addAll(variables, CURRENT_TIME, CURRENT_DATE, CURRENT_DATE_TIME, USER);	
		datas.put(AlarmCategory.FREE.name(), variables);
		
		variables = new ArrayList<String>();
		Collections.addAll(variables, 
				INVOICE_REFERENCE, INVOICE_DESCRIPTION, INVOICE_DATE, INVOICE_DUE_DATE, INVOICE_PERIOD_FROM, INVOICE_PERIOD_TO, INVOICE_DAY, INVOICE_MONTH, INVOICE_MONTH_NAME, INVOICE_YEAR,
				CLIENT_ID, CLIENT_NAME, 
				CONTACT_TITLE, CONTACT_FIRSTNAME, CONTACT_LASTNAME, 
				CURRENT_TIME, CURRENT_DATE, CURRENT_DATE_TIME, USER
				);			
		datas.put(AlarmCategory.INVOICE.name(), variables);
		
		return datas;
	}
	
}

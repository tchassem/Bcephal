/**
 * 
 */
package com.moriset.bcephal.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Moriset
 *
 */
public class AlarmVariables {

	public String CURRENT_DATE 		= "$CURRENT DATE$";
	public String CURRENT_TIME 		= "$CURRENT TIME$";
	public String CURRENT_DATE_TIME = "$CURRENT DATE TIME$";
	public String USER = "$USER$";
	
	public String INVOICE_DATE 		= "$INVOICE DATE$";
	public String INVOICE_REFERENCE = "$INVOICE REF$";	
	public String CLIENT_ID 		= "$INVOICE CLIENT ID$";
	public String CLIENT_NAME 		= "$INVOICE CLIENT NAME$";
	
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
		Collections.addAll(variables, CURRENT_TIME, CURRENT_DATE, CURRENT_DATE_TIME, USER, 
				INVOICE_REFERENCE, INVOICE_DATE, CLIENT_ID, CLIENT_NAME);			
		datas.put(AlarmCategory.INVOICE.name(), variables);
		
		return datas;
	}
	
}

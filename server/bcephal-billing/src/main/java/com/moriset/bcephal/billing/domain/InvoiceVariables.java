/**
 * 
 */
package com.moriset.bcephal.billing.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Moriset
 *
 */
public class InvoiceVariables {

	public String CURRENT_DATE 		= "$CURRENT DATE$";
	public String CURRENT_DATE_TIME = "$CURRENT DATE TIME$";
	public String INVOICE_DATE 		= "$INVOICE DATE$";
	public String INVOICE_YEAR 		= "$INVOICE YEAR$";
	public String INVOICE_MONTH 	= "$INVOICE MONTH$";
	public String INVOICE_MONTH_NAME = "$INVOICE MONTH NAME$";
	public String INVOICE_DAY 		= "$INVOICE DAY$";
	
	public String INVOICE_FROM 		= "$PERIOD FROM$";
	public String INVOICE_TO 		= "$PERIOD TO$";
	
	public String INVOICE_NBR 		= "$INVOICE NBR$";
	public String INVOICE_DESCRIPTION 		= "$INVOICE DESCRIPTION$";
	public String CLIENT_ID 		= "$CLIENT ID$";
	public String CLIENT_NAME 		= "$CLIENT NAME$";
	
	public List<String> getAll(){
		List<String> variables = new ArrayList<String>();
		Collections.addAll(variables, CURRENT_DATE, CURRENT_DATE_TIME, INVOICE_DATE, INVOICE_YEAR, INVOICE_MONTH, INVOICE_MONTH_NAME, INVOICE_DAY, INVOICE_FROM, INVOICE_TO, INVOICE_NBR, INVOICE_DESCRIPTION, CLIENT_ID, CLIENT_NAME);	
		return variables;
	}
	
}

/**
 * 
 */
package com.moriset.bcephal.scheduler.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Moriset
 *
 */
public class PresentationVariables {

	public String CURRENT_DATE 		= "$CURRENT DATE$";
	public String CURRENT_DATE_TIME = "$CURRENT DATE TIME$";
	public String YEAR 		= "$YEAR$";
	public String MONTH 	= "$MONTH$";
	public String MONTH_NAME = "$MONTH NAME$";
	public String DAY 		= "$DAY$";
	
	
	public List<String> getAll(){
		List<String> variables = new ArrayList<String>();
		Collections.addAll(variables, CURRENT_DATE, CURRENT_DATE_TIME, YEAR, MONTH, MONTH_NAME, DAY);	
		return variables;
	}
	
}

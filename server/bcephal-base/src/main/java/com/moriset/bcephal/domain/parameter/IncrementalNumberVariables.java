/**
 * 
 */
package com.moriset.bcephal.domain.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Moriset
 *
 */
public class IncrementalNumberVariables {

	public String YEAR 			= "$YEAR$";
	public String MONTH 		= "$MONTH$";
	public String DAY_OF_MONTH 	= "$DAY_OF_MONTH$";	
	
	public List<String> getAll(){
		List<String> variables = new ArrayList<String>();
		Collections.addAll(variables, YEAR, MONTH, DAY_OF_MONTH);	
		return variables;
	}
	
}

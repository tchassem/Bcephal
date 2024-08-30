/**
 * 
 */
package com.moriset.bcephal.utils;

/**
 * @author Joseph Wambo
 *
 */
public class SpecialCharUtils {

	public static String replaceAllSpecialChars(String name, String substitutionString ) {
		return name.replaceAll("[^a-zA-Z0-9]+",substitutionString);
	}
	
}

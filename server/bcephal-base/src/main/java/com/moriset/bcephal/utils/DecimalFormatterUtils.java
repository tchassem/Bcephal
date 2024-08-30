package com.moriset.bcephal.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import lombok.Data;

@Data
public class DecimalFormatterUtils {

	private DecimalFormat formatter;
		
	public DecimalFormatterUtils(char decimalSeparator) {
		formatter = new DecimalFormat();
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setDecimalSeparator(decimalSeparator);
		formatter.setDecimalFormatSymbols(symbols);
	}
	
	public DecimalFormatterUtils(char decimalSeparator, char groupingSeparator) {
		formatter = new DecimalFormat();
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setDecimalSeparator(decimalSeparator);
		symbols.setGroupingSeparator(groupingSeparator);
		formatter.setDecimalFormatSymbols(symbols);
	}
	
	public String format(Object number) {
		return formatter.format(number);
	}
	
	public Number parse(String number) throws ParseException {
		return formatter.parse(number);
	}
	
	public static DecimalFormatterUtils getInstance(char decimalSeparator) {
		return new DecimalFormatterUtils(decimalSeparator);
	}
	
	public static DecimalFormatterUtils getInstance(char decimalSeparator, char groupingSeparator) {
		return new DecimalFormatterUtils(decimalSeparator, groupingSeparator);
	}
	
}

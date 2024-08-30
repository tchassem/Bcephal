package com.moriset.bcephal.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ByteConverter {

	public static byte[] encode(String inputString) {
	    return encode(inputString, StandardCharsets.UTF_8);
	}
	
	public static byte[] encode(String inputString, Charset charset) {
	    if(inputString == null) {
	    	throw new IllegalArgumentException("Input string is NULL!");
	    }
	    return inputString.getBytes(charset);
	}
	
	public static String decode(byte[] byteArrray) {	    
	    return decode(byteArrray, StandardCharsets.UTF_8);
	}
	
	public static String decode(byte[] byteArrray, Charset charset) {	    
		if(byteArrray == null) {
	    	throw new IllegalArgumentException("Byte arrray is NULL!");
	    }	    
	    return new String(byteArrray, charset);
	}
	
}

/**
 * 
 */
package com.moriset.bcephal.manager.utils;

import org.slf4j.helpers.MessageFormatter;
import org.springframework.http.HttpStatus;

/**
 * @author Joseph Wambo
 *
 */
@SuppressWarnings("serial")
public class BcephalException extends RuntimeException {

	private int statusCode;

	public BcephalException() {
		this.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	public BcephalException(String message) {
		this(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
	}

	public BcephalException(String message, Object... args) {
		this(HttpStatus.INTERNAL_SERVER_ERROR.value(), MessageFormatter.arrayFormat(message, args).getMessage());
	}

	public BcephalException(int statusCode, String message) {
		super(message);
		this.setStatusCode(statusCode);
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

}

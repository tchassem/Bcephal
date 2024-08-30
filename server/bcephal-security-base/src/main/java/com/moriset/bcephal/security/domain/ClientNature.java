/**
 * 
 */
package com.moriset.bcephal.security.domain;

/**
 * A client can be a Company or physical person
 * 
 * @author Joseph Wambo
 *
 */
public enum ClientNature {
	
	COMPANY,
	PERSONAL;
	
	public boolean isCompany() {
		return this == COMPANY;
	}
	
	public boolean isPersonal() {
		return this == PERSONAL;
	}
	
}

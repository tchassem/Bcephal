/**
 * 
 */
package com.moriset.bcephal.security.domain;

/**
 * @author Joseph Wambo
 *
 */
public enum ProfileType {

	ADMINISTRATOR,
	SUPERUSER,
	USER,
	GUEST;
	
	public boolean isAdministrator() {
		return this == ADMINISTRATOR;
	}
	
	public boolean isSuperUser() {
		return this == SUPERUSER;
	}
	
	public boolean isUser() {
		return this == USER;
	}
	
	public boolean isGuest() {
		return this == GUEST;
	}
	
	public boolean isAdministratorOrSuperUser() {
		return isAdministrator() || isSuperUser();
	}
		
}

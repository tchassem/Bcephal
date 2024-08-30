package com.moriset.bcephal.security.domain;

public enum ClientStatus {

	ACTIVE,
    TRIAL,
    SUSPENDED,
    END_OF_TRIAL,
    CLOSED;
	
	
	public boolean isEnabled() {
		return this == ACTIVE || this == TRIAL;
	}
	
}

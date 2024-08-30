package com.moriset.bcephal.license.domain;

public enum LicenseValidityType {

	DEFINITIVE,
	TEMPORARY;
	
	public boolean IsDefinitive() {
		return this == DEFINITIVE;
	}
	
	public boolean IsTemporary() {
		return this == TEMPORARY;
	}
		
}

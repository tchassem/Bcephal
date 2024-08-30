package com.moriset.bcephal.grid.domain.form;

public enum FormModelFieldCategory {

	DETAILS,
	MAIN;
	
	public boolean isDetails() {
		return this == DETAILS;
	}
	
	public boolean isMain() {
		return this == MAIN;
	}
	
}

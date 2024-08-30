package com.moriset.bcephal.grid.domain.form;

public enum FormModelFieldType {
	
	EDITION,
	LABEL,
	REFERENCE,
	SELECTION,
	SELECTION_EDITION,
	CALCULATION,
	CONCATENATION,
	SPOT,
	SEQUENCE,
	CHECK;
	
	public boolean isEdition() {
		return this == EDITION;
	}
	
	public boolean isLabel() {
		return this == LABEL;
	}
	
	public boolean isReference() {
		return this == REFERENCE;
	}
	
	public boolean isSelection() {
		return this == SELECTION;
	}
	
	public boolean isSelectionEdition() {
		return this == SELECTION_EDITION;
	}
	
	public boolean isCalculate() {
		return this == CALCULATION;
	}
	
	public boolean isConcatenation() {
		return this == CONCATENATION;
	}
	
	public boolean isSpot() {
		return this == SPOT;
	}
	
	public boolean isSequence() {
		return this == SEQUENCE;
	}
	
	public boolean isCheck() {
		return this == CHECK;
	}
}

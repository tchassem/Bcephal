package com.moriset.bcephal.archive.domain;

public enum ArchiveGridCreationType {

	NEW,
	APPEND;
	
	public boolean isNew() {
		return this == NEW;
	}
	
	public boolean isAppend() {
		return this == APPEND;
	}
}

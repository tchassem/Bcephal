package com.moriset.bcephal.utils;

public enum FileType {

	 EXCEL,
     CSV,
     XML;
	
	
	public boolean isExcel() {
		return this == EXCEL;
	}
	
	public boolean isCsv() {
		return this == CSV;
	}
	
	public boolean isXml() {
		return this == XML;
	}
}

package com.moriset.bcephal.utils.socket;

public enum DataType {
	EXCEL,
	CSV,
	JSON;
	
	public String getExtension() {
		return this == CSV ? ".csv" : this == JSON ? ".json" : ".xlsx";
	}
}

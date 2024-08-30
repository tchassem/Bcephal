package com.moriset.bcephal.messenger.send.sms;

public enum SmsSupplierType {
	BULKSMS("bulksms"), BULKSMSONLINE("bulksmsonline");

	private String code;

	private SmsSupplierType(String code) {
		this.code = code;
	}

	public boolean isBulksms() {
		return this == SmsSupplierType.BULKSMS;
	}

	public boolean isBulkSmsOnline() {
		return this == SmsSupplierType.BULKSMSONLINE;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return this.code;
	}
}

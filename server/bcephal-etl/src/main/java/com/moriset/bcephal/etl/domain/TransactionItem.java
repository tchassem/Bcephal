package com.moriset.bcephal.etl.domain;

import lombok.Data;

@Data
public class TransactionItem {

	String client;
	String project;
	String user;
	String runId;
	String fileName;
	String runDate;
	
	String messageType;
	String reference;
	String sender;
	String receiver;
	String date;
	String amount;
	String currency;
	String dc;
	String entryDate;
	String valueDate;
	String transactionType;
	String identification;
	String referenceAccOwner;
	String description;
	
	public TransactionItem() {
		
	}
	
}

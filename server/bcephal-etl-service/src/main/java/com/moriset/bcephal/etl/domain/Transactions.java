package com.moriset.bcephal.etl.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Transactions {
	
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
	
	List<TransactionItem> items;
	
	public Transactions(){
		items = new ArrayList<>();
	}
	
	public TransactionItem buildNewItem() {
		TransactionItem item = new TransactionItem();
		item.setClient(client);
		item.setProject(project);
		item.setUser(user);
		item.setRunId(runId);
		item.setRunDate(runDate);
		item.setFileName(fileName);		
		
		item.setMessageType(getMessageType());
		item.setReceiver(getReceiver());
		item.setSender(getSender());
		item.setReference(getReference());
		return item;
	}
	
}

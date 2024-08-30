package com.moriset.bcephal.messenger.model;

import jakarta.jms.Message;

public interface BuildMessage {
	
	void work(Message message) throws Exception;
	
}
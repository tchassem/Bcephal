package com.moriset.bcephal.messenger.send.mail;

import java.io.IOException;

import com.moriset.bcephal.messenger.send.mail.AbstractMailService.ProviderName;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;



public interface IMailService {
	
	public static String iMailService_= "iMailService_";
	
	public void sendMail(Email emailmessage) throws AddressException, MessagingException, IOException;
	
	default public boolean isLocalSystem() {
		return ProviderName.system.equals(getProvider());
	}
	
	public ProviderName getProvider();
	
	default public boolean isMicrosoft() {
		return ProviderName.microsoft.equals(getProvider());
	}
	
	default public boolean isGridApi() {
		return ProviderName.grid_api.equals(getProvider());
	}
}

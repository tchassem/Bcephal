package com.moriset.bcephal.messenger.properties;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import jakarta.mail.Address;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

public class User {

	private String name;
	private String password;
	

	private String Key;
	private List<String> replyTo; 

	public String getName() {
		return name;
	}

	public String getEmail() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String name) {
		this.name = name;
	}

	public void setFrom(String name) {
		this.name = name;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(List<String> replyTo) {
		this.replyTo = replyTo;
	}

	public Address[] getReplayToAddress() {

		List<Address> adresses = new ArrayList<>();
		if (replyTo != null && replyTo.size() > 0) {
			replyTo.forEach(item ->{
				try {
					adresses.add(new InternetAddress(item));
				} catch (AddressException e) {
					e.printStackTrace();
				}
			});
		}
		return  adresses.toArray(new Address[adresses.size()]);
	}

	public String getKey() {
		return Key;
	}

	public void setKey(String key) {
		Key = key;
	}
	
	public void setUserName(String key) {
		Key = key;
	}

	public String getUserName() {
		String UserName = getEmail();
		if(!StringUtils.isBlank(getKey())) {
			UserName = getKey();
		}
		return UserName;
	}
}

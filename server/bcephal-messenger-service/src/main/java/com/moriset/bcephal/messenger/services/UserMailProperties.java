/**
 * 
 */
package com.moriset.bcephal.messenger.services;

import java.util.ArrayList;
import java.util.List;

import jakarta.mail.Address;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.Data;

/**
 * @author Moriset
 *
 */
//@Component
//@ConfigurationProperties(prefix = "bcephal.notification.userMail")
@Data
public class UserMailProperties {

	private String from;
	private String displayName;
	private String userName;
	private String password;	
	private List<String> replyTo; 
		
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
	
}

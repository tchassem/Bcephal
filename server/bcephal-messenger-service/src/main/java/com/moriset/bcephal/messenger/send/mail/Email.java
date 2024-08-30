package com.moriset.bcephal.messenger.send.mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.moriset.bcephal.messenger.services.UserMailProperties;

import jakarta.mail.Address;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.Data;

@Data
public class Email {

	private UserMailProperties userMail;
	private Address[] adresseReceipt;
	private String subject;
	private String body;
	private List<String> filesId;

	public Email() {
		super();
	}

	public Email(UserMailProperties userMail, List<String> adresseReceipt, String subject, String body, List<String> filesId) {
		super();
		this.userMail = userMail;
		setAdresseReceipt(adresseReceipt);
		this.subject = subject;
		this.body = body;
		this.filesId = filesId;
	}

	public void setAdresseReceipt(List<String> adresseReceipt) {
		List<Address> adresses = new ArrayList<>();
		if (adresseReceipt != null) {
			adresseReceipt.forEach(addresse -> {
				try {
					adresses.add(new InternetAddress(addresse));
				} catch (AddressException e) {
					e.printStackTrace();
				}
			});
		}
		this.adresseReceipt = adresses.toArray(new Address[adresses.size()]);
	}

	@Override
	public String toString() {
		return "Email [userMail=" + userMail + ", adresseReceipt=" + Arrays.toString(adresseReceipt) + ", subject="
				+ subject + ", body=" + body + ", file=" + (filesId != null && filesId.size() > 0 ? filesId.get(0) : "") + "]";
	}

}

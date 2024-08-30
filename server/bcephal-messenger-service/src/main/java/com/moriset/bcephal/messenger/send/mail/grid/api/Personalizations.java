package com.moriset.bcephal.messenger.send.mail.grid.api;

import java.util.ArrayList;
import java.util.List;

public class Personalizations {
	public List<EmailInfos> to;
	public EmailInfos from;
	public List<EmailInfos> reply_to;
	public Content content;
	
	public Personalizations() {
		content = new Content();
		from = new EmailInfos();
		to = new ArrayList<>();
		reply_to = new ArrayList<>();
	}
}

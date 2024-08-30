package com.moriset.bcephal.messenger.send.sms;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.messenger.properties.User;

public class Sms {

	private User userSms;
	private Date sendDateTime = new Date();
	private List<String> to;
	private String content;
	private String from;
	private String type;

	private String lienToken;
	private String lienSms;

	private SmsEncoding encoding;

	public Sms() {
		super();
	}

	public Sms(User userSms, String from, List<String> to, String type, String content) {
		super();
		this.setUserSms(userSms);
		this.to = to;
		this.content = content;
		this.from = from;
		this.type = type;
	}

	public String getLienToken() {
		return lienToken;
	}

	public void setLienToken(String lienToken) {
		this.lienToken = lienToken;
	}

	public String getLienSms() {
		return lienSms;
	}

	public void setLienSms(String lienSms) {
		this.lienSms = lienSms;
	}

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getSendDateTime() {
		return sendDateTime;
	}

	@JsonIgnore
	public User getUserSms() {
		return userSms;
	}

	public void setUserSms(User userSms) {
		this.userSms = userSms;
	}

	public void setSendDateTime(Date sendDateTime) {
		this.sendDateTime = sendDateTime;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Sms [userSms=" + userSms + ", from=" + from + ", type=" + type + ", sendDateTime=" + sendDateTime
				+ ", to=" + to + ", content=" + content + "]";
	}

	@JsonIgnore
	public String getContentSmsForBulksmsonline() {
		return "{\"from\":\"" + getFrom() + "\",\"to\":[" + getToString() + "],\"type\":\"" + getType()
				+ "\",\"content\":\"" + getContent() + "\",\"sendDateTime\": \"" + getSendDateTime() + "\"}";
	}

	@JsonIgnore
	public String getContentSmsForBulksms() {
		String form = "";
		if (getFrom() != null && !getFrom().trim().isEmpty()) {
			form = "\"" + getFrom() + "\"";
		}
		if (!form.isEmpty()) {
			return "{\"from\":" + form + ",\"to\":[" + getToString() + "],\"body\":\"" + getContent()
					+ "\",\"encoding\":\"" + getEncoding() + "\"}";

		} else {
			return "{\"to\":[" + getToString() + "],\"body\":\"" + getContent() + "\",\"encoding\":\"" + getEncoding()
					+ "\"}";
		}
	}

	String tot = "";

	private String getToString() {
		tot = "";
		if (getTo() != null) {

			getTo().forEach(x -> {
				if (tot.isEmpty()) {
					tot = "\"" + x + "\"";
				} else {
					tot += ",\"" + x + "\"";
				}
			});
		}
		return tot;
	}

	@JsonIgnore
	public String getAuthorization() {
		if (userSms == null) {
			return null;
		}
		String authStr = userSms.getName() + ":" + userSms.getPassword();
		String authEncoded = Base64.getEncoder().encodeToString(authStr.getBytes());
		return "Basic " + authEncoded;
	}

	public SmsEncoding getEncoding() {
		if (encoding == null) {
			return SmsEncoding.TEXT;
		}
		return encoding;
	}

	public void setEncoding(SmsEncoding encoding) {
		this.encoding = encoding;
	}

	public Sms getCopy(List<String> to, String sontent) {
		Sms sms = new Sms(getUserSms(), getFrom(), to, getType(), sontent);
		sms.setEncoding(getEncoding());
		sms.setLienSms(getLienSms());
		sms.setLienToken(getLienToken());
		sms.setSendDateTime(new Date());
		return sms;
	}

}

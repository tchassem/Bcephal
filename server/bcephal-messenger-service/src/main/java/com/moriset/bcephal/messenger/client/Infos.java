package com.moriset.bcephal.messenger.client;

public class Infos {

	public static final String MAIL = "MAIL";
	public static final String SMS = "SMS";

	private Object message;
	private String destination;

	public Infos() {
	}

	public Infos(Object message) {
		this.message = message;
	}

	public Infos(Object message, String destination) {
		this(message);
		this.destination = destination;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

}

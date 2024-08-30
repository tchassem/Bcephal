package com.moriset.bcephal.etl.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class EmailBrowserData extends BrowserData {

	private String userName;
	private String host;
	private String port;
	
	
	private String password;

	public EmailBrowserData(EmailAccount item) {
		super(item.getId(), item.getUserName());
		setUserName(item.getUserName());
		setHost(item.getServe_host());
		setPort(item.getServer_port());
		
		
	}
	
}

package com.moriset.bcephal.manager.service;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.springframework.http.HttpHeaders;

public interface AddressProperties extends FileManagerProperties {

	public String getHost();

	public void setHost(String host);

	public String getPort();

	public void setPort(String port);

	public String getUserName();

	public void setUserName(String userName);

	public String getUserpwd();

	public void setUserpwd(String userpwd);

	default public String getAuth() {
		if (getUserName() == null || getUserpwd() == null) {
			return null;
		}
		String encoding;
		try {
			encoding = Base64.getEncoder()
					.encodeToString(getUserName().concat(":").concat(getUserpwd()).getBytes("UTF-8"));
			return "Basic " + encoding;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	default public String getBaseUrl() {
		return String.format("http://%s:%s/", getHost(), getPort());
	}

	default public HttpHeaders getHttpHeaders() {
		String encoding = getAuth();
		HttpHeaders headers = new HttpHeaders();
		if (encoding != null) {
			headers.set("authorization", getAuth());
		}
		return headers;
	}

}

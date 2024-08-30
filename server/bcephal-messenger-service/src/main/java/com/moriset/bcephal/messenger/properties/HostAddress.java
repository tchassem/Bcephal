package com.moriset.bcephal.messenger.properties;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(prefix = "activemq.address")
public class HostAddress {
	private String ip = "localhost";
	private String tcpPort = "61626";
	private String stompPort = "61627";
	private String url;
	private Boolean secure;

	public String getIp() {
		return replaceSpecialValues(ip);
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getTcpPort() {
		if (StringUtils.hasText(tcpPort)) {
			return Integer.valueOf(tcpPort);
		}
		return 61626;
	}

	public void setTcpPort(String tcpPort) {
		this.tcpPort = tcpPort;
	}
	
	public int getStompPort() {
		if (StringUtils.hasText(stompPort)) {
			return Integer.valueOf(stompPort);
		}
		return 61627;
	}

	public void setStompPort(String stompPort) {
		this.stompPort = stompPort;
	}

	public String getUrl() {
		if (StringUtils.hasText(url)) {
			return replaceSpecialValues(url.trim());
		} else if (StringUtils.hasText(ip) && StringUtils.hasText(tcpPort)) {
			return replaceSpecialValues(String.format("http://%s:%s", ip, tcpPort));
		} else if (StringUtils.hasText(ip) && !StringUtils.hasText(tcpPort)) {
			return replaceSpecialValues(String.format("http://%s:61626", ip));
		}
		return replaceSpecialValues("http://localhost:61626");
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private final String LOCALHOST = "localhost";

	public String replaceSpecialValues(String property) {
		if (StringUtils.hasText(property)) {
			if (property.contains(LOCALHOST)) {
				InetAddress address = getIpAddress();
				if (address != null) {
					property = StringUtils.replace(property, LOCALHOST, address.getHostAddress());
				}
			}
		}
		return property;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(String secure) {
		if (!org.apache.commons.lang3.StringUtils.isBlank(secure)) {
			this.secure = secure.trim().toLowerCase().equals("true");
		}
	}

	/**
	 * 
	 * @return InetAddress
	 */
	public static InetAddress getIpAddress() {
		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

}

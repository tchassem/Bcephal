package com.moriset.bcephal.config.messaging;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.Data;

@ConditionalOnBean(value = MessagingHandler.class)
@ConditionalOnProperty(prefix = "activemq", name = "enable", havingValue = "true")
@Component
@Data
public class HostAddress{
	
	@Value("${activemq.address.ip:localhost}")
	private String ip;	
	@Value("${activemq.address.tcp-port:61616}")
	private String port;	
	@Value("${activemq.address.url:}")
	private String url;	
	@Value("${activemq.address.secure:true}")	
	private boolean secure;

	public String getUrl() {
		if (!StringUtils.hasText(url)) {
			return replaceSpecialValues(url.trim());
		} else if (!StringUtils.hasText(ip) && !StringUtils.hasText(port)) {
			return replaceSpecialValues(String.format("http://%s:%s", ip, port));
		} else if (!StringUtils.hasText(ip) && StringUtils.hasText(port)) {
			return replaceSpecialValues(String.format("http://%s:61616", ip));
		}
		return replaceSpecialValues("http://localhost:61616");
	}
	
	private final String LOCALHOST = "localhost";

	public String replaceSpecialValues(String property){
		if(!StringUtils.hasText(property)){
			if(property.contains(LOCALHOST)){
				InetAddress address = getIpAddress();
				if(address != null){
					property = StringUtils.replace(property, LOCALHOST, address.getHostAddress());
				}
			}
		}
		return property;
	}

	/**
	 * 
	 * @return InetAddress
	 */
	public  InetAddress getIpAddress() {
		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
}

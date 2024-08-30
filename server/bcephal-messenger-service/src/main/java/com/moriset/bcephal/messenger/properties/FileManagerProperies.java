package com.moriset.bcephal.messenger.properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bcephal.file.manager")
public class FileManagerProperies {

	public static final String HAVING_VALUE = "1";
	public static final String _NAME = "bcephal.file.manager.active";
	private String active;
	private Boolean active_ = null;
	private String host;
	private Integer port;
	
	@Value("${gateway-host}")
	private String gatewayHost;
	@Value("${gateway-port}")
	private String gatewayPort;

	@Value("${gateway-protocol}")
	private String gatewayProtocol;

	public String gatewayUrl() {
		String proto = gatewayProtocol;
		if (StringUtils.isBlank(proto)) {
			proto = "http";
		}
		//return proto + "://" + gatewayHost + ":" + gatewayPort + "/file-manager/download/";
		return "lb://file-manager-service/file-manager/download/";
	}
	
	public Boolean getActive() {
		if(active_ == null && !StringUtils.isBlank(active)) {
			active_ = active.trim().equals("1") || active.trim().equalsIgnoreCase("true");
		}else {
			active_ = false;
		}
		return active_;
	}
	public void setActive(String active) {
		this.active = active;
	}
	private String getHost() {
		if(host == null) {
			host = "127.0.0.1";
		}
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	private Integer getPort() {
		if(port == null) {
			port = 9194;
		}
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	
	public String getUrl() {
		return "http://" + getHost() + ":" + getPort() + "/bcephal-file-manager/download/";
	}
}

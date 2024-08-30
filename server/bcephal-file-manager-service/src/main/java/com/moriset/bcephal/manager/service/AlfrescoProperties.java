package com.moriset.bcephal.manager.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

import lombok.Data;

@PropertySources(
		{
			@PropertySource("alfresco.properties")
		})
@ConfigurationProperties(prefix = "document.manager")
@Component
@Data
public class AlfrescoProperties implements AddressProperties {
	private String nodeId;
	private ProviderName provider;
	
	private String basePath;
	
	private String host;
	private String port;
	
	private String userName = "admin";
	private String userPwd = "admin";
	
	
	public AlfrescoProperties() {
		provider = ProviderName.alfresco;
	}	

	@Override
	public ProviderName getProvider() {
		return provider;
	}

	@Override
	public void setProvider(ProviderName provider) {
		this.provider = provider;
	}
	
	private String getBaseNode() {
		return String.format("%s%s%s",getBaseUrl(),getBasePath(),"nodes/");
	}
	
	private String getBaseSite() {
		return String.format("%s%s%s",getBaseUrl(),getBasePath(),"sites/");
	}
	
	public String getNodeUrl(String nodeId) {
		if(nodeId == null || nodeId.trim().isEmpty()) {
			nodeId = getNodeId();
		}
		return String.format("%s%s/children", getBaseNode(), nodeId);
	}
	
	public String getNodeIDUrl(String nodeId) {
		if(nodeId == null || nodeId.trim().isEmpty()) {
			nodeId = getNodeId();
		}
		return String.format("%s%s", getBaseNode(), nodeId);
	}
	
	public String getDeleteUrl(String nodeId) {
		if(nodeId == null || nodeId.trim().isEmpty()) {
			nodeId = getNodeId();
		}
		return String.format("%s%s", getBaseNode(), nodeId);
	}
	
	public String getSiteUrl(String nodeId) {
		if(nodeId == null || nodeId.trim().isEmpty()) {
			nodeId = getNodeId();
		}
		return String.format("%s%s/children", getBaseSite(), nodeId);
	}
	
	public String getContentUrl(String nodeId) {
		if(nodeId == null || nodeId.trim().isEmpty()) {
			nodeId = getNodeId();
		}
		return String.format("%s%s/content", getBaseNode(), nodeId);
	}

	public void init() {
		if (getBasePath() == null
				|| getBasePath().trim().isEmpty()) {
			setBasePath("alfresco/api/-default-/public/alfresco/versions/1/");
		}
	}

	@Override
	public String getHost() {
		return this.host;
	}

	@Override
	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public String getPort() {
		return this.port;
	}

	@Override
	public void setPort(String port) {
		this.port = port;
	}

	@Override
	public String getUserName() {
		return this.userName;
	}

	@Override
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String getUserpwd() {
		return this.userPwd;
	}

	@Override
	public void setUserpwd(String userpwd) {
		this.userPwd = userpwd;
	}
	
}

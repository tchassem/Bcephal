package com.moriset.bcephal.manager.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "document.manager")
public interface FileManagerProperties {

	public enum ProviderName {
		system, alfresco, git, microsoft
	}

	ProviderName getProvider();
    void  init();

	default public void setProvider(String provider) {
		if (provider != null && provider.trim().isEmpty()) {
			setProvider(ProviderName.valueOf(provider.trim()));
		}
	}

	public void setProvider(ProviderName provider);

	
	default public boolean isAlfresco() {
		return ProviderName.alfresco.equals(getProvider());
	}

	default public boolean isGit() {
		return ProviderName.git.equals(getProvider());
	}

	default public boolean isLocalSystem() {
		return ProviderName.system.equals(getProvider());
	}

	default public boolean isMicrosoft() {
		return ProviderName.microsoft.equals(getProvider());
	}

}

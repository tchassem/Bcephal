package com.moriset.bcephal.messenger.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "activemq.client.keystore")
@Component
public class KeystoreClient {
	private String trustPassword = "mok145oloKMDYBG@1*24}589658klUFdc32";
	private String keyStorePassword = "mok145oloWNMTGD@k*412578rbdg@QMBV45DFTS";

	public String getTrustPassword() {
		return trustPassword;
	}

	public void setTrustPassword(String trustPassword) {
		if (StringUtils.hasText(trustPassword)) {
			this.trustPassword = trustPassword;
		}
	}

	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		if (StringUtils.hasText(keyStorePassword)) {
			this.keyStorePassword = keyStorePassword;
		}
	}
}

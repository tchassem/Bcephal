package com.moriset.bcephal.messenger.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "activemq.server.keystore")
@Component
public class KeystoreServer {
	private String trustPassword = "mok145oloWNMTGD@k*412578rbdg@QMBV45DFTS";
	private String keyPassword = "mok145oloKMDYBG@1*24}589658klUFdc32";

	public String getTrustPassword() {
		return trustPassword;
	}

	public void setTrustPassword(String trustPassword) {
		if (StringUtils.hasText(trustPassword)) {
			this.trustPassword = trustPassword;
		}
	}

	public String getKeyPassword() {
		return keyPassword;
	}

	public void setKeyPassword(String keyPassword) {
		if (StringUtils.hasText(keyPassword)) {
			this.keyPassword = keyPassword;
		}
	}

}

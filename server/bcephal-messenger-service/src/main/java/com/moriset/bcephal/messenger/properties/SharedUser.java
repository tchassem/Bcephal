package com.moriset.bcephal.messenger.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "activemq.user")
@Component
public class SharedUser {

	private String name = "mksaf32ariouser45pok";
	private String password = "mk+/sD(f4782*SrTo?User45kWq";
	private String clientId = "MESSENGER___";
	private String group = "users,admins";
	private String guests = "users,admins,guests";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (StringUtils.hasText(name)) {
			this.name = name;
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if (StringUtils.hasText(password)) {
			this.password = password;
		}
	}

	public String getClientID() {
		return clientId;
	}

	public String getGroup() {
		return group;
	}
	
	public String getGuests() {
		return guests;
	}
}

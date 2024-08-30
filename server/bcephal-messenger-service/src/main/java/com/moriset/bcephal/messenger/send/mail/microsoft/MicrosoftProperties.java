package com.moriset.bcephal.messenger.send.mail.microsoft;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "bcephal.notification.user-mail")
@Component
public class MicrosoftProperties {	
	
	public final String graphDomain = "https://graph.microsoft.com";	
	
	private String basePath;
	private String password;
	private String email;
	
	@Autowired
	ApplicationClient applicationClient;

	@ConfigurationProperties(prefix = "bcephal.notification.client")
	@Component
	public class ApplicationClient{
		private String id;
		private String secret;
		private String domain;
		
		public String getSecret() {
			return secret;
		}
		public void setSecret(String secret) {
			this.secret = secret;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		
		/**
		 * @return the domain
		 */
		public String getDomain() {
			return domain;
		}

		/**
		 * @param domain the domain to set
		 */
		public void setDomain(String domain) {
			this.domain = domain;
		}
	}
	
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}


	
	public String getLoginTennatIdV2() {
		return String.format("https://login.microsoftonline.com/%s/oauth2/v2.0/token", applicationClient.getDomain());
	}
	
	
	public String getSenderMailUrl() {
		return String.format("%s/v1.0/users/%s/sendMail",graphDomain, getEmail());
	}
	

	

	public String getBasePath() {
		return basePath;
	}
	 
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public ApplicationClient getApplicationClient() {
		return applicationClient;
	}

	public void setApplicationClient(ApplicationClient applicationClient) {
		this.applicationClient = applicationClient;
	}

}

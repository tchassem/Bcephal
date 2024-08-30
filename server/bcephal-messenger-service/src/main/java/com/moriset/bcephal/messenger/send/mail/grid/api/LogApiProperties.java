package com.moriset.bcephal.messenger.send.mail.grid.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bcephal.log.api")
public class LogApiProperties {
	public static final String HAVING_VALUE = "1";
	public static final String _NAME = "bcephal.file.manager.active";
	
	private String key;
	private String url;
	private String path;
	private String logPath;
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		if(StringUtils.isBlank(url)) {
			return;
		}
		String uri = url.toLowerCase();
		if(uri.contains("https://")) {
			int pos = uri.indexOf("https://") + "https://".length();
			
			int pos2 = uri.substring(pos).indexOf("/") + 1;
			path = uri.substring(pos + pos2);
			this.url = url.substring(0, pos + pos2);
		}
		
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPath() {
		return path;
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}
	
}

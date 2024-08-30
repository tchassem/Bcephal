package com.moriset.bcephal.messenger.properties;

import java.nio.file.Paths;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "activemq.server")
@Component
public class ServerProperties {

	private String[] virtualDestination = { "bcephal_messenger_service.*" };
	private String dbPath;
	private String dbName = "bcephalJmsDb";
	private String brockerName = "bcephal-remote-server";
	private String brockerBindName = "remotehost-bcephal-remote-server";
	private boolean start = true;
	private boolean clearAllMessage = false;

	public ServerProperties() {
		setDbPath(getDefaultPath());
	}

	public String[] getVirtualDestination() {
		return virtualDestination;
	}

	public void setVirtualDestination(List<String> virtualDestination) {
		if (virtualDestination != null && virtualDestination.size() > 0) {
			this.virtualDestination = virtualDestination.toArray(new String[virtualDestination.size()]);
		}
	}

	public String getDbPath() {
		return dbPath;
	}

	public void setDbPath(String dbPath) {
		if (StringUtils.hasText(dbPath) && Paths.get(dbPath).toFile().exists()) {
			this.dbPath = dbPath;
		}
	}

	private String getDefaultPath() {
		return dbPath = Paths.get(System.getProperty("user.home"), ".bcephal","8", "activemq-kahaDB").toAbsolutePath()
				.toString();
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		if (StringUtils.hasText(dbName)) {
			this.dbName = dbName;
		}
	}

	public String getBrockerName() {
		return brockerName;
	}

	public void setBrockerName(String brockerName) {
		if (StringUtils.hasText(brockerName)) {
			this.brockerName = brockerName;
		}
	}

	public boolean isStart() {
		return start;
	}

	public void setStart(boolean start) {
		this.start = start;
	}

	public String getBrockerBindName() {
		return brockerBindName;
	}

	public void setBrockerBindName(String brockerBindName) {
		if (StringUtils.hasText(brockerBindName)) {
			this.brockerBindName = brockerBindName;
		}
	}

	public boolean isClearAllMessage() {
		return clearAllMessage;
	}

	public void setClearAllMessage(Boolean clearAllMessage) {
		this.clearAllMessage = clearAllMessage != null ? clearAllMessage : false;
	}

}

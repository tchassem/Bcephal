package com.moriset.bcephal.chat.domain;

import java.util.Objects;

public enum ChatStatus {

	OPENED("OPENED"),
	BLOCKED("BLOCKED"),
	CLOSED("CLOSED"),
	ARCHIVED("ARCHIVED");
	
	public boolean isOpened() {
		return this == OPENED;
	}
	
	public boolean isBlocked() {
		return this == BLOCKED;
	}
	
	public boolean isClosed() {
		return this == CLOSED;
	}
	
	public boolean isArchived() {
		return this == ARCHIVED;
	}

    private String value;

    ChatStatus(String value) {
        this.value = value;
    }

    public String getValue() { return value; }

    public void setValue(String value) { this.value = value; }

    public static ChatStatus forValue(String value) {
        for (ChatStatus chatStatus : values()) {
            if (Objects.equals(chatStatus.getValue(), value)) {
                return chatStatus;
            }
        }
        return null;
    }

}

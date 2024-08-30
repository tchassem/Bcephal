package com.moriset.bcephal.chat.domain;

import java.util.Objects;

public enum ChatItemType {

	MESSAGE("MESSAGE"),
	DOCUMENT("DOCUMENT");
	
	public boolean isMessage() {
		return this == MESSAGE;
	}
	
	public boolean isDocument() {
		return this == DOCUMENT;
	}

    private String value;

    ChatItemType(String value) {
        this.value = value;
    }

    public String getValue() { return value; }

    public void setValue(String value) { this.value = value; }

    public static ChatItemType forValue(String value) {
        for (ChatItemType chatItemType : values()) {
            if (Objects.equals(chatItemType.getValue(), value)) {
                return chatItemType;
            }
        }
        return null;
    }
}

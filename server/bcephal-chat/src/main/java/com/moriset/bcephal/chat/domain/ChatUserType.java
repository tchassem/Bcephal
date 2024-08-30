package com.moriset.bcephal.chat.domain;

import java.util.Objects;

public enum ChatUserType {

	USER("USER"),
	PROFILE("PROFILE");
	
	public boolean isUser() {
		return this == USER;
	}
	
	public boolean isProfile() {
		return this == PROFILE;
	}

    private String value;

    ChatUserType(String value) {
        this.value = value;
    }

    public String getValue() { return value; }

    public void setValue(String value) { this.value = value; }

    public static ChatUserType forValue(String value) {
        for (ChatUserType chatUserType : values()) {
            if (Objects.equals(chatUserType.getValue(), value)) {
                return chatUserType;
            }
        }
        return null;
    }

}

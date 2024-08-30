package com.moriset.bcephal.utils;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationRequest {
	
	public static final String YES = "YES";
	public static final String NO = "NO";

	private String question;
	private String response;
	private boolean applyToAll;
	
	@JsonIgnore
	public boolean isYes() {
		return StringUtils.hasText(response) && response.equalsIgnoreCase(YES);
	}
	
}

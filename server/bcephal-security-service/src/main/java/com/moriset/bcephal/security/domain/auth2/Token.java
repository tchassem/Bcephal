/**
 * 
 */
package com.moriset.bcephal.security.domain.auth2;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author MORISET-004
 *
 */
@Data
public class Token {
	@JsonProperty("access_token")
	private String token;
	@JsonProperty("expires_in")
	private long expiresIn;
	@JsonProperty("refreshExpiresIn")
	private long refresh_expires_in;
	@JsonProperty("refreshToken")
	private String refresh_token;
	@JsonProperty("tokenType")
	private String token_type;
	@JsonProperty("not-before-policy")
	private long notBeforePolicy;
	@JsonProperty("session_state")
	private String sessionState;
	@JsonProperty("scope")
	private String scope;
}

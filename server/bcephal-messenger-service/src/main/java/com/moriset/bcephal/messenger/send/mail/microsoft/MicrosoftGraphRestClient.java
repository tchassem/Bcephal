package com.moriset.bcephal.messenger.send.mail.microsoft;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class MicrosoftGraphRestClient {

	protected Logger logger;
	protected RestTemplate restTemplate;
	protected MicrosoftProperties manager;
	protected JSONObject tokenObject;

	private final String accessToken = "access_token";
	private final String expireToken = "expires_in";
	private Long lastTime;

	public MicrosoftGraphRestClient(MicrosoftProperties manager) {
		logger = LoggerFactory.getLogger(getClass());
		restTemplate = new RestTemplate();
		this.manager = manager;
	}

	private String getClientCredential() {
		String scope = "https://graph.microsoft.com/.default";
		String params_ = "grant_type=%s&scope=%s&client_id=%s&client_secret=%s";
		return String.format(params_, "client_credentials", scope, manager.getApplicationClient().getId(),
				manager.getApplicationClient().getSecret());
	}

	private String getAccessToken() throws Exception {
		if (tokenObject != null && tokenObject.has(accessToken) && tokenObject.has(expireToken)) {
			Long now = (System.currentTimeMillis() / 1000) - lastTime;
			if ((tokenObject.getLong(expireToken) - now) > (5 * 60)) {
				return tokenObject.getString(accessToken);
			}
		}
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Content-type", new String("application/x-www-form-urlencoded".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
		RequestEntity<String> requestEntity = new RequestEntity<>(getClientCredential(), headers, HttpMethod.POST,
				null);

		ResponseEntity<String> responseEntity = restTemplate.exchange(manager.getLoginTennatIdV2(), HttpMethod.POST,
				requestEntity, String.class);
		String securityToken = null;
		try {
			tokenObject = new JSONObject(responseEntity.getBody());
			if (tokenObject.has(accessToken)) {
				securityToken = tokenObject.getString(accessToken);
				lastTime = (System.currentTimeMillis() / 1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		if (StringUtils.isBlank(securityToken)) {
			throw new Exception("Unable to authenticate: empty token");
		}
		return securityToken;
	}

	private MultiValueMap<String, String> getAuthheaders() throws Exception {
		String securityToken = getAccessToken();
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Content-type", "application/json");
		headers.add("Authorization", "Bearer " + securityToken);

		return headers;

	}

	protected String post(String path, String body, boolean usingToken) throws Exception {
		MultiValueMap<String, String> headers = getAuthheaders();
		RequestEntity<String> requestEntity = new RequestEntity<>(new String(body.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8), headers, HttpMethod.POST, new URI(path));
		ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
		return responseEntity.getBody();
	}

	protected String get(String path, boolean usingToken) throws Exception {
		MultiValueMap<String, String> headers = getAuthheaders();
		RequestEntity<String> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI(path));
		ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
		return responseEntity.getBody();
	}
}

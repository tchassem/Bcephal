/**
 * 
 */
package com.moriset.bcephal.manager.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author moriset 
 *
 */
@Slf4j
@Data
public abstract class BaseController {
	

	@Autowired
	protected RestTemplate restTemplate;

	protected ResponseEntity<String> get(String url, String auth) {
		return execute(url, HttpMethod.GET, auth, null);
	}

	protected ResponseEntity<String> post(String url, String auth) {
		return execute(url, HttpMethod.POST, auth, null);
	}

	protected ResponseEntity<String> post(String url, String auth, String body) {
		return execute(url, HttpMethod.POST, auth, body);
	}

	protected ResponseEntity<String> execute(String url, HttpMethod method, String auth, String body) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("authorization", auth);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(body, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class);
		log.trace(response.getBody());
		return response;
	}

	protected ResponseEntity<String> post(String url, String auth, byte[] body) {
		return executeByte(url, HttpMethod.POST, auth, body);
	}

	protected ResponseEntity<String> executeByte(String url, HttpMethod method, String auth, byte[] body) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("authorization", auth);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		HttpEntity<byte[]> entity = new HttpEntity<byte[]>(body, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class);
		log.trace(response.getBody());
		return response;
	}

}

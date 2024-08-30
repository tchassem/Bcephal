/**
 * 
 */
package com.moriset.bcephal.manager.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.moriset.bcephal.manager.controller.BrowserDataFilter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-004
 *
 */
@Component
@Data
@Slf4j
public class AlfrescoService implements BaseService {

	protected final RestTemplate restTemplate;
	
	@Autowired
	public AlfrescoProperties manager;
	
	@Autowired
	protected  ResourceLoader resourceLoader;

	public AlfrescoService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	


	protected ResponseEntity<String> get(String url) {
		return execute(url, HttpMethod.GET, null);
	}

	protected ResponseEntity<String> post(String url) {
		return execute(url, HttpMethod.POST, null);
	}

	protected ResponseEntity<String> post(String url, String body) {
		return execute(url, HttpMethod.POST, body);
	}

	protected ResponseEntity<String> execute(String url, HttpMethod method, String body) {
		HttpHeaders headers = manager.getHttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(body, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class);
		return response;
	}

	protected ResponseEntity<String> post(String url, String auth, byte[] body) {
		return executeByte(url, HttpMethod.POST, auth, body);
	}

	protected ResponseEntity<String> executeByte(String url, HttpMethod method, String auth, byte[] body) {
		HttpHeaders headers = manager.getHttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		HttpEntity<byte[]> entity = new HttpEntity<byte[]>(body, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class);
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.moriset.misp.document.service.BaseService#getById(java.lang.String)
	 */
	@Override
	public ResponseEntity<String> getById(String documentId) {
		ResponseEntity<String> response = get(manager.getNodeIDUrl(documentId));
		if(response.getStatusCode().value() == 200) {
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(getJsonEntry(response.getBody()));
		}
		return ResponseEntity.status(response.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(getJsonEntry(response.getBody()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.moriset.misp.document.service.BaseService#delete(java.lang.String)
	 */
	@Override
	public ResponseEntity<String> delete(String documentId) {
		HttpHeaders headers = manager.getHttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<RequestData> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				manager.getDeleteUrl(documentId) + "?permanent=true", HttpMethod.DELETE, requestEntity,
				String.class);
		if(response.getStatusCode().value() == 204 ) {
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(getJsonEntry(response.getBody()));
		}
		return ResponseEntity.status(response.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(getJsonEntry(response.getBody()));
	}

	@Override
	public ResponseEntity<String> handleFileUpload(MultipartFile file, String bodyJson, String documentId) {
		// Normalize the path by suppressing sequences like "path/.." and inner simple
		// dots.
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			// we can add additional file validation to discard invalid files
			Path uploadDir = getUploadDirLocation().resolve(fileName);
			// copy the file to the upload directory,it will replace any file with same
			// name.
			Files.copy(file.getInputStream(), uploadDir, StandardCopyOption.REPLACE_EXISTING);
			Resource resource = resourceLoader.getResource("file:" + uploadDir.toFile().getCanonicalPath());
			HttpHeaders headers = manager.getHttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("filedata", resource);
			body.add("body", bodyJson);
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(manager.getNodeUrl(documentId),
					requestEntity, String.class);
			if(response.getStatusCode().value() == 201) {
				return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(getJsonEntry(response.getBody()));
			}
			return ResponseEntity.status(response.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(getJsonEntry(response.getBody()));
		} catch (IOException e) {
			log.error("unable to cpy file to the target location {}", e);
		}
		return null;
	}
	
	private String getJsonEntry(String body){
		if(!StringUtils.hasText(body)) {
			return "";
		}
		JSONObject jsonObject = new JSONObject(body);			
		if(jsonObject != null && jsonObject.has("entry")) {
			JSONObject entry = jsonObject.getJSONObject("entry");
			if(entry.has("id")) {
				return entry.getString("id");
			}
		}
		return "";
	}

	private Path getUploadDirLocation() throws IOException {
		return Files.createTempDirectory("download_invoice_" + System.currentTimeMillis()).toAbsolutePath().normalize();
	}

	@Override
	public ResponseEntity<Resource> handleFileDownload(String documentId) throws IOException {
		File file = File.createTempFile("download_invoice_" + System.currentTimeMillis(), ".pdf");
		RequestCallback requestCallback = request -> {
			request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
			request.getHeaders().set("authorization", manager.getAuth());
			request.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
			request.getHeaders().set("Range", String.format("bytes=%d-", file.length()));
		};
		ResponseExtractor<ClientHttpResponse> responseExtractor = response -> {
			StreamUtils.copy(response.getBody(), new FileOutputStream(file, true));
			return response;
		};
		restTemplate.execute(manager.getContentUrl(documentId) + "?attachment=true", HttpMethod.GET,
				requestCallback, responseExtractor);
		Resource resource = resourceLoader.getResource("file:" + file.getCanonicalPath());
		return ResponseEntity.ok().header("fileName", file.getName()).contentType(MediaType.APPLICATION_JSON).body(resource);
	}

	@Override
	public ResponseEntity<String> saveOrUpdate(String data, String nodeId) {
		ResponseEntity<String> response = post(manager.getNodeUrl(nodeId), data);
		if(response.getStatusCode().value() == 201) {
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(getJsonEntry(response.getBody()));
		}
		return ResponseEntity.status(response.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(getJsonEntry(response.getBody()));
	}
	
	@Override
	public ResponseEntity<?> viewDocument(BrowserDataFilter viewDocument) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public ResponseEntity<?> GetBrowserItems(BrowserDataFilter filter) {
		// TODO Auto-generated method stub
		return null;
	}

}

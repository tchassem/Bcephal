package com.moriset.bcephal.billing.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.moriset.bcephal.billing.domain.Invoice;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BillingFileUploaderService {
	
	@Autowired
	RestTemplate loadBalancedRestTemplate;
	
	private String fileManagerServiceUri = "lb://file-manager-service";

	public String uploadFileToFileManager(Invoice invoice, String path, HttpHeaders httpHeaders, Locale locale) {
		ByteArrayResource bytes = null;
		try {
			bytes = new ByteArrayResource(Files.readAllBytes(Path.of(path))){
			    @Override
			    public String getFilename(){
			        return FilenameUtils.getName(path);
			    }
			};
		} catch (IOException e) {
			log.error("Unable to read file : {}", path, e.getMessage());
			throw new BcephalException("Unable to tranfert bill PDF to file manager server.");
		}
		
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("filedata", bytes);
		body.add("body", StringUtils.hasLength(invoice.getFileName()) ? invoice.getFileName() : FilenameUtils.getName(path));
		HttpHeaders headers = getHttpHeaders(httpHeaders, locale);
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		ResponseEntity<String> response = loadBalancedRestTemplate.postForEntity(fileManagerServiceUri + "/file-manager/upload/{category}", requestEntity, String.class, "invoice");	
		log.trace("Upload file. Response : {}", response.getStatusCode());
		return response.getBody();
	}
	
	public void deleteFile(String fileId, HttpHeaders httpHeaders, Locale locale) {		
		HttpHeaders headers = getHttpHeaders(httpHeaders, locale);
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		loadBalancedRestTemplate.delete(fileManagerServiceUri + "/file-manager/delete/" + fileId);
	}
	
	
	private HttpHeaders getHttpHeaders(HttpHeaders httpHeaders, Locale locale){
		HttpHeaders requestHeaders = new HttpHeaders();
		List<String> items = Arrays.asList("bc-profile","bc-client","authorization","cookie");
		httpHeaders.forEach((key,value)->{
			if(items.contains(key)) {
				requestHeaders.add(key, value.get(0));
			}
		});
		requestHeaders.set(MultiTenantInterceptor.TENANT_HEADER_NAME, TenantContext.getCurrentTenant());
		requestHeaders.set(RequestParams.LANGUAGE, locale.getLanguage());
		return requestHeaders;
	}

}

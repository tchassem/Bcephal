/**
 * 15 janv. 2024 - HashMapHttpMessageConverter.java
 * 
 */
package com.moriset.bcephal.reporting.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.google.gson.Gson;

/**
 * @author Emmanuel Emmeni
 *
 */
public class HashMapHttpMessageConverter extends AbstractHttpMessageConverter<HashMap<String, Object>> {

    public HashMapHttpMessageConverter() {
        super(MediaType.APPLICATION_JSON);
    }

	@Override
	protected boolean supports(Class<?> clazz) {
		return HashMap.class.isAssignableFrom(clazz);
	}

	@Override
	protected HashMap<String, Object> readInternal(Class<? extends HashMap<String, Object>> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		return null;
	}

	@Override
	protected void writeInternal(HashMap<String, Object> map, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		HttpHeaders headers = outputMessage.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
        String json = convertHashMapToJson(map);
        outputMessage.getBody().write(json.getBytes(StandardCharsets.UTF_8));
	}

    private String convertHashMapToJson(HashMap<String, Object> map) {
        Gson gson = new Gson();
        return gson.toJson(map);
    }

}

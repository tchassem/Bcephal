package com.moriset.bcephal.api.controller;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class RestTemplateWithCookies extends RestTemplate {

    private final List<HttpCookie> cookies = new ArrayList<>();

    public RestTemplateWithCookies() {
    	this(new HttpComponentsClientHttpRequestFactory());
    }

    public RestTemplateWithCookies(ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
    }

    public synchronized List<HttpCookie> getCoookies() {
        return cookies;
    }

    public synchronized void resetCoookies() {
        cookies.clear();
    }

    private void processHeaders(HttpHeaders headers) {
        final List<String> cooks = headers.get("Set-Cookie");
        if (cooks != null && !cooks.isEmpty()) {
            cooks.stream().map((c) -> HttpCookie.parse(c)).forEachOrdered((cook) -> {
                cook.forEach((a) -> {
                    HttpCookie cookieExists = cookies.stream().filter(x -> a.getName().equals(x.getName())).findAny().orElse(null);
                    if (cookieExists != null) {
                        cookies.remove(cookieExists);
                    }
                    cookies.add(a);
                });
            });
        }
    }

	@Override
	protected <T> T doExecute(URI url, @Nullable String uriTemplate, @Nullable HttpMethod method, @Nullable RequestCallback requestCallback,
			@Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {
           final List<HttpCookie> cookies = getCoookies();

        return super.doExecute(url, uriTemplate,method, new RequestCallback() {
            @Override
            public void doWithRequest(ClientHttpRequest chr) throws IOException {
                if (cookies != null) {
                    StringBuilder sb = new StringBuilder();
                    for (HttpCookie cookie : cookies) {
                        sb.append(cookie.getName()).append(cookie.getValue()).append(";");
                    }
                    chr.getHeaders().add("Cookie", sb.toString());
                }
                requestCallback.doWithRequest(chr);
            }

        }, new ResponseExtractor<T>() {
            @Override
            public T extractData(ClientHttpResponse chr) throws IOException {
                processHeaders(chr.getHeaders());
                return responseExtractor.extractData(chr);
            }
        });
    }

}

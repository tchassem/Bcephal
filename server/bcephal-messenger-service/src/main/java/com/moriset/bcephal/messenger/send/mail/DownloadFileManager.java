package com.moriset.bcephal.messenger.send.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.google.common.io.Files;
import com.moriset.bcephal.messenger.properties.FileManagerProperies;

@Component
@ConditionalOnProperty(havingValue=FileManagerProperies.HAVING_VALUE,name=FileManagerProperies._NAME)
public class DownloadFileManager {
	
	public static final String RESTNAME = "DownloadFileManager_";

	@Autowired
	@Qualifier(RESTNAME)
	protected RestTemplate restTemplate;
	

	@Autowired
	ResourceLoader resourceLoader;

//	private String getAuth() throws UnsupportedEncodingException {
//					return "Basic " + Base64.getEncoder()
//							.encodeToString("sergeyAlfresco".concat(":").concat("sergeyAlfresco").getBytes("UTF-8"));
//	}
	
	String OrigineFileName = null;
	File file = null;
	
	public String downloadFile(String url) throws IOException {
		file = File.createTempFile("download-file-manager", ".pdf");
		OrigineFileName = null;
		RequestCallback requestCallback = request -> {
			request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
			//request.getHeaders().set("authorization", getAuth());
			request.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
			request.getHeaders().set("Range", String.format("bytes=%d-", file.length()));
		};
		ResponseExtractor<ClientHttpResponse> responseExtractor = response -> {
			FileOutputStream out = new FileOutputStream(file, true);
			StreamUtils.copy(response.getBody(), out);
			if(OrigineFileName == null) {
				OrigineFileName = response.getHeaders().getFirst("fileName");
			}
			out.close();
			return response;
		};
		restTemplate.execute(url, HttpMethod.POST, requestCallback, responseExtractor);
		if(OrigineFileName != null) {
			File OrigineFileName_ = Paths.get(file.getParent(),OrigineFileName).toFile();
			try {
				Files.move(file, OrigineFileName_);
				file = OrigineFileName_;
			}catch (Exception e) {
				e.printStackTrace();
			}			
		}
		return file.getCanonicalPath();
	}
}

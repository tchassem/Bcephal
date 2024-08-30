package com.moriset.bcephal.messenger.config;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.moriset.bcephal.messenger.client.Infos;
import com.moriset.bcephal.messenger.client.ReceiverClient;
import com.moriset.bcephal.messenger.client.ReceiverConfig;
import com.moriset.bcephal.messenger.client.ReceiverConfig.ITopicSession;
import com.moriset.bcephal.messenger.properties.FileManagerProperies;
import com.moriset.bcephal.messenger.properties.HostAddress;
import com.moriset.bcephal.messenger.send.mail.DownloadFileManager;
import com.moriset.bcephal.messenger.send.mail.IMailService;
import com.moriset.bcephal.messenger.send.mail.MailService;
import com.moriset.bcephal.messenger.send.mail.ReceiveMessageEmailImpl;
import com.moriset.bcephal.messenger.send.mail.microsoft.MicrosoftMailGraphNativeService;
import com.moriset.bcephal.messenger.send.sms.ReceiveMessageSmsImpl;

@Configuration
//@ComponentScan(basePackages = { "com.moriset.bcephal.*" })
//@Import(WebSecurityConfig.class)
public class ServerConfig {

//	@Bean
//	public MessageConverter messageConverter() {
//		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//		converter.setTargetType(MessageType.TEXT);
//		converter.setTypeIdPropertyName("_type");
//		converter.setObjectMapper(objectMapper());
//		return converter;
//	}

	@LoadBalanced
	@Bean(DownloadFileManager.RESTNAME)
	@ConditionalOnProperty(havingValue=FileManagerProperies.HAVING_VALUE,name=FileManagerProperies._NAME)
	RestTemplate restTemplate(FileManagerProperies fileManagerProperies) {
		RestTemplate template = new RestTemplate();
//		if(fileManagerProperies.gatewayUrl().startsWith("https")) {
//			try {
//				template = new RestTemplate(getRequestFactory());
//			} catch (Exception e) {
//			}
//		}
		template.setUriTemplateHandler(new DefaultUriBuilderFactory(fileManagerProperies.gatewayUrl()));
		addConverters(template);
		return template;
	}
	
//	private HttpComponentsClientHttpRequestFactory getRequestFactory() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
//		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
//	    SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
//	    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, (hostname, session) -> true);	    
//	    
//	    PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
//	    	      .setSSLSocketFactory(csf).build();
//	    	    
//	    CloseableHttpClient httpClient = HttpClients.custom()
//	    	      .setConnectionManager(connectionManager).build();	    
//	    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//	    requestFactory.setHttpClient(httpClient);
//	    return requestFactory;
//	}
	
//	@Bean(GridApiService.RESTNAME)
//	@ConditionalOnProperty(havingValue=GridApiProperties.HAVING_VALUE,name=GridApiProperties._NAME)
//	RestTemplate restTemplateApi(GridApiProperties gridApiProperties) {
//		RestTemplate template = new RestTemplate();
//		template.setUriTemplateHandler(new DefaultUriBuilderFactory(gridApiProperties.getUrl()));
//		addConverters(template);
//		template.setRequestFactory(new SimpleClientHttpRequestFactory() {
//			   @Override
//			    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
//			        if (connection instanceof HttpsURLConnection) {
//			            ((HttpsURLConnection) connection).setHostnameVerifier(new NoopHostnameVerifier());
//			        }
//			        super.prepareConnection(connection, httpMethod);
//			    }
//			});
//		return template;
//	}
	
	
	@Bean
	@ConditionalOnProperty(havingValue=FileManagerProperies.HAVING_VALUE,name=FileManagerProperies._NAME)
	boolean setDownloadFileManager(@Qualifier(IMailService.iMailService_)IMailService service, 
			DownloadFileManager downloadFileManager) {
		if (service.isMicrosoft()) {
			((MicrosoftMailGraphNativeService)service).setDownloadFileManager(downloadFileManager);
		} else if (service.isLocalSystem()) {
			((MailService)service).setDownloadFileManager(downloadFileManager);
		}
		return true;
	}

	protected void addConverters(RestTemplate restTemplate) {
		ArrayList<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>(
				Arrays.asList(new MappingJackson2HttpMessageConverter(), 
						new ResourceHttpMessageConverter(),
						new FormHttpMessageConverter(), 
						new ByteArrayHttpMessageConverter(),
						new StringHttpMessageConverter(), 
						new BufferedImageHttpMessageConverter()));
		restTemplate.getMessageConverters().addAll(converters);
		return;
	}
	
	@Autowired
	public void startUpBrockerService( SslBcephalActivemqServer  sslBcephalActivemqServer,
			BcephalActivemqServer bcephalActivemqServer , HostAddress hostAddress) {
		if(hostAddress.isSecure()) {
			sslBcephalActivemqServer.builder();
		}else{
			bcephalActivemqServer.builder();
		}
	}

	@Autowired
	public void startReceiverMail(ITopicSession iSession, @Qualifier(value = Infos.MAIL) ReceiverClient receiverClient,
			ReceiveMessageEmailImpl receiveMessageEmailImpl) {
		receiverClient.start(Infos.MAIL, iSession, receiveMessageEmailImpl);
	}

	@Autowired
	public void startReceiverSms(@Qualifier(value = ReceiverConfig.SESSION_TOPIC + Infos.SMS) ITopicSession iSession,
			@Qualifier(value = Infos.SMS) ReceiverClient receiverClient, ReceiveMessageSmsImpl receiveMessageSmsImpl) {
		receiverClient.start(Infos.SMS, iSession, receiveMessageSmsImpl);
	}

	@Bean(IMailService.iMailService_)
	IMailService mailServiceConfig(MailService systemMailService,
											MicrosoftMailGraphNativeService microsoftMailService/*, 
											GridApiService gridApiService*/) {
		if (systemMailService.isMicrosoft()) {
			return microsoftMailService;
		} else 
//			if (systemMailService.isLocalSystem())
			{
			//systemMailService.initLog();
			return systemMailService;
		}
//		else {
//			gridApiService.initLog();
//			return gridApiService;
//		}
	}
}
